package com.TrackFile.app.service;

import com.TrackFile.app.domain.Empresa;
import com.TrackFile.app.domain.Mantenimiento;
import com.TrackFile.app.domain.TipoMantenimiento;
import com.TrackFile.app.domain.Usuario;
import com.TrackFile.app.domain.Vehiculo;
import com.TrackFile.app.domain.enums.EstadoMantenimiento;
import com.TrackFile.app.domain.enums.RolUsuario;
import com.TrackFile.app.repository.MantenimientoRepository;
import com.TrackFile.app.repository.TipoMantenimientoRepository;
import com.TrackFile.app.repository.UsuarioRepository;
import com.TrackFile.app.repository.VehiculoRepository;
import com.TrackFile.app.web.dto.response.CreateMantenimientoRequest;
import com.TrackFile.app.web.dto.response.UpdateMantenimientoRequest;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import java.time.temporal.ChronoUnit;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class MantenimientoService {

    private final MantenimientoRepository mantenimientoRepository;
    private final VehiculoRepository vehiculoRepository;
    private final TipoMantenimientoRepository tipoMantenimientoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CurrentUserService currentUserService;
    private final NotificacionService notificacionService;

    public MantenimientoService(
            MantenimientoRepository mantenimientoRepository,
            VehiculoRepository vehiculoRepository,
            TipoMantenimientoRepository tipoMantenimientoRepository,
            UsuarioRepository usuarioRepository,
            CurrentUserService currentUserService,
            NotificacionService notificacionService) {
        this.mantenimientoRepository = mantenimientoRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.tipoMantenimientoRepository = tipoMantenimientoRepository;
        this.usuarioRepository = usuarioRepository;
        this.currentUserService = currentUserService;
        this.notificacionService = notificacionService;
    }

    public Mantenimiento crear(CreateMantenimientoRequest request) {
        Long empresaId = currentUserService.getEmpresaIdActual();

        Vehiculo vehiculo = vehiculoRepository.findByIdAndEmpresaId(request.getVehiculoId().intValue(), empresaId)
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));

        TipoMantenimiento tipo = tipoMantenimientoRepository.findByIdAndEmpresaId(
                request.getTipoMantenimientoId(),
                empresaId)
                .orElseThrow(() -> new RuntimeException("Tipo de mantenimiento no encontrado"));

        Empresa empresa = new Empresa();
        empresa.setId(empresaId);

        Mantenimiento mantenimiento = new Mantenimiento();
        mantenimiento.setVehiculo(vehiculo);
        mantenimiento.setTipoMantenimiento(tipo);
        mantenimiento.setEmpresa(empresa);
        mantenimiento.setFechaSugerida(request.getFechaSugerida());
        mantenimiento.setFechaProgramada(request.getFechaProgramada());
        mantenimiento.setKilometraje(request.getKilometraje());
        mantenimiento.setCosto(request.getCosto());
        mantenimiento.setTaller(request.getTaller());
        mantenimiento.setObservaciones(request.getObservaciones());

        if (request.getFechaProgramada() != null) {
            mantenimiento.setEstado(EstadoMantenimiento.PROGRAMADO);
        } else {
            mantenimiento.setEstado(EstadoMantenimiento.SUGERIDO);
        }

        Mantenimiento guardado = mantenimientoRepository.save(mantenimiento);

        notificarMantenimientoAInvolucrados(guardado, guardado.getEstado().name());

        return guardado;
    }

    public List<Mantenimiento> listar() {
        Long empresaId = currentUserService.getEmpresaIdActual();
        return mantenimientoRepository.findByEmpresaIdOrderByFechaCreacionDesc(empresaId);
    }

    public Mantenimiento obtener(Long id) {
        Long empresaId = currentUserService.getEmpresaIdActual();

        return mantenimientoRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new RuntimeException("Mantenimiento no encontrado"));
    }

    public List<Mantenimiento> listarPorVehiculo(Long vehiculoId) {
        Long empresaId = currentUserService.getEmpresaIdActual();

        return mantenimientoRepository.findByVehiculoIdAndEmpresaIdOrderByFechaCreacionDesc(
                vehiculoId,
                empresaId);
    }

    public List<Mantenimiento> listarProgramados() {
        Long empresaId = currentUserService.getEmpresaIdActual();

        return mantenimientoRepository.findByEstadoAndEmpresaId(
                EstadoMantenimiento.PROGRAMADO,
                empresaId);
    }

    public List<Mantenimiento> listarVencidos() {
        Long empresaId = currentUserService.getEmpresaIdActual();

        return mantenimientoRepository.findByFechaProgramadaBeforeAndEstadoAndEmpresaId(
                LocalDate.now(),
                EstadoMantenimiento.PROGRAMADO,
                empresaId);
    }

    public List<Mantenimiento> listarProximos() {
        Long empresaId = currentUserService.getEmpresaIdActual();

        LocalDate hoy = LocalDate.now();
        LocalDate limite = hoy.plusDays(15);

        return mantenimientoRepository.findByFechaProgramadaBetweenAndEstadoAndEmpresaId(
                hoy,
                limite,
                EstadoMantenimiento.PROGRAMADO,
                empresaId);
    }

    public Mantenimiento actualizar(Long id, UpdateMantenimientoRequest request) {
        Mantenimiento mantenimiento = obtener(id);

        EstadoMantenimiento estadoAnterior = mantenimiento.getEstado();

        mantenimiento.setFechaProgramada(request.getFechaProgramada());
        mantenimiento.setFechaRealizado(request.getFechaRealizado());
        mantenimiento.setKilometraje(request.getKilometraje());
        mantenimiento.setCosto(request.getCosto());
        mantenimiento.setTaller(request.getTaller());
        mantenimiento.setObservaciones(request.getObservaciones());

        if (request.getEstado() != null) {
            mantenimiento.setEstado(request.getEstado());
        }

        Mantenimiento actualizado = mantenimientoRepository.save(mantenimiento);

        if (request.getEstado() != null && estadoAnterior != actualizado.getEstado()) {
            notificarMantenimientoAInvolucrados(actualizado, actualizado.getEstado().name());
        }

        return actualizado;
    }

    public Mantenimiento marcarComoRealizado(Long id, UpdateMantenimientoRequest request) {
        Mantenimiento mantenimiento = obtener(id);

        EstadoMantenimiento estadoAnterior = mantenimiento.getEstado();

        mantenimiento.setFechaRealizado(
                request.getFechaRealizado() != null
                        ? request.getFechaRealizado()
                        : LocalDate.now());

        mantenimiento.setKilometraje(request.getKilometraje());
        mantenimiento.setCosto(request.getCosto());
        mantenimiento.setTaller(request.getTaller());
        mantenimiento.setObservaciones(request.getObservaciones());
        mantenimiento.setEstado(EstadoMantenimiento.REALIZADO);

        Mantenimiento actualizado = mantenimientoRepository.save(mantenimiento);

        if (estadoAnterior != EstadoMantenimiento.REALIZADO) {
            notificarMantenimientoAInvolucrados(actualizado, EstadoMantenimiento.REALIZADO.name());
        }

        return actualizado;
    }

    public void eliminar(Long id) {
        Mantenimiento mantenimiento = obtener(id);
        mantenimientoRepository.delete(mantenimiento);
    }

    private void notificarMantenimientoAInvolucrados(Mantenimiento mantenimiento, String estado) {
        Set<Long> usuariosNotificados = new HashSet<>();

        String placa = mantenimiento.getVehiculo() != null
                ? mantenimiento.getVehiculo().getPlaca()
                : "sin placa";

        Long empresaId = mantenimiento.getEmpresa() != null
                ? mantenimiento.getEmpresa().getId()
                : currentUserService.getEmpresaIdActual();

        usuarioRepository.findFirstByEmpresa_IdAndRol(empresaId, RolUsuario.EMPRESA)
                .ifPresent(usuarioEmpresa -> notificarSiNoExiste(usuarioEmpresa, usuariosNotificados, placa, estado));

        if (mantenimiento.getVehiculo() != null &&
                mantenimiento.getVehiculo().getConductor() != null &&
                mantenimiento.getVehiculo().getConductor().getUsuario() != null) {

            notificarSiNoExiste(
                    mantenimiento.getVehiculo().getConductor().getUsuario(),
                    usuariosNotificados,
                    placa,
                    estado);
        }

        if (mantenimiento.getVehiculo() != null &&
                mantenimiento.getVehiculo().getPropietario() != null &&
                mantenimiento.getVehiculo().getPropietario().getUsuario() != null) {

            notificarSiNoExiste(
                    mantenimiento.getVehiculo().getPropietario().getUsuario(),
                    usuariosNotificados,
                    placa,
                    estado);
        }
    }

    private void notificarSiNoExiste(
            Usuario usuario,
            Set<Long> usuariosNotificados,
            String placa,
            String estado) {
        if (usuario == null || usuario.getId() == null) {
            return;
        }

        if (usuariosNotificados.contains(usuario.getId())) {
            return;
        }

        usuariosNotificados.add(usuario.getId());

        notificacionService.notificarMantenimientoActualizado(
                usuario,
                placa,
                estado);
    }

    @Scheduled(fixedRate = 60000)
    public void generarAlertasMantenimientosProximosAutomaticas() {
        generarAlertasMantenimientosProximos();
    }

    public String generarAlertasMantenimientosProximos() {
        Long empresaId = currentUserService.getEmpresaIdActual();

        LocalDate hoy = LocalDate.now();
        LocalDate limite = hoy.plusDays(15);

        List<Mantenimiento> mantenimientos = mantenimientoRepository
                .findByFechaProgramadaBetweenAndEstadoAndEmpresaId(
                        hoy,
                        limite,
                        EstadoMantenimiento.PROGRAMADO,
                        empresaId);

        int creadas = 0;

        for (Mantenimiento mantenimiento : mantenimientos) {
            if (mantenimiento.getFechaProgramada() == null) {
                continue;
            }

            long diasRestantes = ChronoUnit.DAYS.between(
                    hoy,
                    mantenimiento.getFechaProgramada());

            notificarMantenimientoProximoAInvolucrados(
                    mantenimiento,
                    diasRestantes);

            creadas++;
        }

        return "Alertas de mantenimientos próximos generadas: " + creadas;
    }

    private void notificarMantenimientoProximoAInvolucrados(
            Mantenimiento mantenimiento,
            long diasRestantes) {
        Set<Long> usuariosNotificados = new HashSet<>();

        String placa = mantenimiento.getVehiculo() != null
                ? mantenimiento.getVehiculo().getPlaca()
                : "sin placa";

        LocalDate fechaProgramada = mantenimiento.getFechaProgramada();

        Long empresaId = mantenimiento.getEmpresa() != null
                ? mantenimiento.getEmpresa().getId()
                : currentUserService.getEmpresaIdActual();

        usuarioRepository.findFirstByEmpresa_IdAndRol(empresaId, RolUsuario.EMPRESA)
                .ifPresent(usuarioEmpresa -> notificarMantenimientoProximoSiNoExiste(
                        usuarioEmpresa,
                        usuariosNotificados,
                        placa,
                        fechaProgramada,
                        diasRestantes));

        if (mantenimiento.getVehiculo() != null &&
                mantenimiento.getVehiculo().getConductor() != null &&
                mantenimiento.getVehiculo().getConductor().getUsuario() != null) {

            notificarMantenimientoProximoSiNoExiste(
                    mantenimiento.getVehiculo().getConductor().getUsuario(),
                    usuariosNotificados,
                    placa,
                    fechaProgramada,
                    diasRestantes);
        }

        if (mantenimiento.getVehiculo() != null &&
                mantenimiento.getVehiculo().getPropietario() != null &&
                mantenimiento.getVehiculo().getPropietario().getUsuario() != null) {

            notificarMantenimientoProximoSiNoExiste(
                    mantenimiento.getVehiculo().getPropietario().getUsuario(),
                    usuariosNotificados,
                    placa,
                    fechaProgramada,
                    diasRestantes);
        }
    }

    private void notificarMantenimientoProximoSiNoExiste(
            Usuario usuario,
            Set<Long> usuariosNotificados,
            String placa,
            LocalDate fechaProgramada,
            long diasRestantes) {
        if (usuario == null || usuario.getId() == null) {
            return;
        }

        if (usuariosNotificados.contains(usuario.getId())) {
            return;
        }

        usuariosNotificados.add(usuario.getId());

        notificacionService.notificarMantenimientoProximo(
                usuario,
                placa,
                fechaProgramada,
                diasRestantes);
    }
}