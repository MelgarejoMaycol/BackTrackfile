package com.TrackFile.app.service;

import com.TrackFile.app.domain.Conductor;
import com.TrackFile.app.domain.Usuario;
import com.TrackFile.app.domain.enums.EstadoUsuario;
import com.TrackFile.app.domain.enums.RolUsuario;
import com.TrackFile.app.repository.ConductorRepository;
import com.TrackFile.app.repository.UsuarioRepository;
import com.TrackFile.app.web.dto.CreateConductorRequest;
import com.TrackFile.app.web.dto.UpdateEstadoUsuarioRequest;
import com.TrackFile.app.web.dto.response.ConductorDetalleResponse;
import com.TrackFile.app.web.dto.response.PersonaListaResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ConductorService {

    private final ConductorRepository conductorRepository;
    private final UsuarioRepository usuarioRepository;
    private final CurrentUserService currentUserService;

    public ConductorService(
            ConductorRepository conductorRepository,
            UsuarioRepository usuarioRepository,
            CurrentUserService currentUserService
    ) {
        this.conductorRepository = conductorRepository;
        this.usuarioRepository = usuarioRepository;
        this.currentUserService = currentUserService;
    }

    public List<PersonaListaResponse> listar() {
        Long empresaId = currentUserService.getEmpresaIdActual();

        return conductorRepository.findAll()
                .stream()
                .filter(c -> c.getUsuario().getEmpresa().getId().equals(empresaId))
                .map(this::toListaResponse)
                .toList();
    }

    public ConductorDetalleResponse obtenerDetalle(Integer idConductor) {
        Long empresaId = currentUserService.getEmpresaIdActual();

        Conductor conductor = conductorRepository
                .findByIdAndUsuarioEmpresaId(idConductor, empresaId)
                .orElseThrow(() -> new RuntimeException("Conductor no encontrado"));

        return toDetalleResponse(conductor);
    }

    public ConductorDetalleResponse crear(CreateConductorRequest request) {
        Long empresaId = currentUserService.getEmpresaIdActual();

        Usuario usuario = usuarioRepository.findById(request.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!usuario.getEmpresa().getId().equals(empresaId)) {
            throw new RuntimeException("El usuario no pertenece a tu empresa");
        }

        conductorRepository.findByUsuarioId(request.getIdUsuario())
                .ifPresent(c -> {
                    throw new RuntimeException("Este usuario ya está registrado como conductor");
                });

        usuario.setRol(RolUsuario.CONDUCTOR);
        usuario.setEstado(EstadoUsuario.ACTIVO);
        usuarioRepository.save(usuario);

        Conductor conductor = new Conductor();
        conductor.setUsuario(usuario);
        conductor.setLicenciaConduccion(request.getLicenciaConduccion());
        conductor.setCategoriaLicencia(request.getCategoriaLicencia());
        conductor.setFechaVencimientoLicencia(request.getFechaVencimientoLicencia());

        Conductor guardado = conductorRepository.save(conductor);

        return toDetalleResponse(guardado);
    }

    public ConductorDetalleResponse editar(Integer idConductor, CreateConductorRequest request) {
        Long empresaId = currentUserService.getEmpresaIdActual();

        Conductor conductor = conductorRepository
                .findByIdAndUsuarioEmpresaId(idConductor, empresaId)
                .orElseThrow(() -> new RuntimeException("Conductor no encontrado"));

        conductor.setLicenciaConduccion(request.getLicenciaConduccion());
        conductor.setCategoriaLicencia(request.getCategoriaLicencia());
        conductor.setFechaVencimientoLicencia(request.getFechaVencimientoLicencia());

        Conductor guardado = conductorRepository.save(conductor);

        return toDetalleResponse(guardado);
    }

    public ConductorDetalleResponse cambiarEstado(Integer idConductor, UpdateEstadoUsuarioRequest request) {
        Long empresaId = currentUserService.getEmpresaIdActual();

        Conductor conductor = conductorRepository
                .findByIdAndUsuarioEmpresaId(idConductor, empresaId)
                .orElseThrow(() -> new RuntimeException("Conductor no encontrado"));

        Usuario usuario = conductor.getUsuario();
        usuario.setEstado(request.getEstado());
        usuarioRepository.save(usuario);

        return toDetalleResponse(conductor);
    }

    public ConductorDetalleResponse eliminarLogico(Integer idConductor) {
        Long empresaId = currentUserService.getEmpresaIdActual();

        Conductor conductor = conductorRepository
                .findByIdAndUsuarioEmpresaId(idConductor, empresaId)
                .orElseThrow(() -> new RuntimeException("Conductor no encontrado"));

        Usuario usuario = conductor.getUsuario();
        usuario.setEstado(EstadoUsuario.INACTIVO);
        usuarioRepository.save(usuario);

        return toDetalleResponse(conductor);
    }

    private PersonaListaResponse toListaResponse(Conductor conductor) {
        Usuario usuario = conductor.getUsuario();

        return new PersonaListaResponse(
                conductor.getId().longValue(),
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getNombre() + " " + usuario.getApellido(),
                usuario.getNumeroDocumento(),
                usuario.getCorreo(),
                usuario.getTelefono(),
                conductor.getCategoriaLicencia()
        );
    }

    private ConductorDetalleResponse toDetalleResponse(Conductor conductor) {
        Usuario usuario = conductor.getUsuario();

        LocalDate fechaVencimiento = conductor.getFechaVencimientoLicencia();
        Integer diasRestantes = null;
        Boolean licenciaVigente = null;

        if (fechaVencimiento != null) {
            diasRestantes = (int) ChronoUnit.DAYS.between(LocalDate.now(), fechaVencimiento);
            licenciaVigente = !fechaVencimiento.isBefore(LocalDate.now());
        }

        return new ConductorDetalleResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getTipoDocumento(),
                usuario.getNumeroDocumento(),
                usuario.getCorreo(),
                usuario.getTelefono(),
                usuario.getDireccion(),
                usuario.getEstado().name(),
                usuario.getRol().name(),
                conductor.getId().longValue(),
                conductor.getLicenciaConduccion(),
                conductor.getCategoriaLicencia(),
                conductor.getFechaVencimientoLicencia(),
                0L,
                0L,
                0L,
                licenciaVigente,
                diasRestantes
        );
    }
}