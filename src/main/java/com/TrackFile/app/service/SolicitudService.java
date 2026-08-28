package com.TrackFile.app.service;

import com.TrackFile.app.domain.Documento;
import com.TrackFile.app.domain.HistorialSolicitud;
import com.TrackFile.app.domain.Solicitud;
import com.TrackFile.app.domain.TipoSolicitud;
import com.TrackFile.app.domain.Usuario;
import com.TrackFile.app.domain.Vehiculo;
import com.TrackFile.app.domain.enums.EstadoSolicitud;
import com.TrackFile.app.domain.enums.RolUsuario;
import com.TrackFile.app.repository.DocumentoRepository;
import com.TrackFile.app.repository.HistorialSolicitudRepository;
import com.TrackFile.app.repository.SolicitudRepository;
import com.TrackFile.app.repository.TipoSolicitudRepository;
import com.TrackFile.app.repository.UsuarioRepository;
import com.TrackFile.app.repository.VehiculoRepository;
import com.TrackFile.app.service.storage.CloudinaryService;
import com.TrackFile.app.web.dto.CreateTipoSolicitudRequest;
import com.TrackFile.app.web.dto.UpdateSolicitudRequest;
import com.TrackFile.app.web.dto.UpdateTipoSolicitudRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class SolicitudService {

    private final TipoSolicitudRepository tipoSolicitudRepository;
    private final SolicitudRepository solicitudRepository;
    private final HistorialSolicitudRepository historialSolicitudRepository;
    private final DocumentoRepository documentoRepository;
    private final VehiculoRepository vehiculoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CurrentUserService currentUserService;
    private final NotificacionService notificacionService;
    private final CloudinaryService cloudinaryService;

    public SolicitudService(
            TipoSolicitudRepository tipoSolicitudRepository,
            SolicitudRepository solicitudRepository,
            HistorialSolicitudRepository historialSolicitudRepository,
            DocumentoRepository documentoRepository,
            VehiculoRepository vehiculoRepository,
            UsuarioRepository usuarioRepository,
            CurrentUserService currentUserService,
            NotificacionService notificacionService,
            CloudinaryService cloudinaryService
    ) {
        this.tipoSolicitudRepository = tipoSolicitudRepository;
        this.solicitudRepository = solicitudRepository;
        this.historialSolicitudRepository = historialSolicitudRepository;
        this.documentoRepository = documentoRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.usuarioRepository = usuarioRepository;
        this.currentUserService = currentUserService;
        this.notificacionService = notificacionService;
        this.cloudinaryService = cloudinaryService;
    }

    public TipoSolicitud crearTipoSolicitud(CreateTipoSolicitudRequest request) {
        if (request.getNombre() == null || request.getNombre().isBlank()) {
            throw new RuntimeException("El nombre del tipo de solicitud es obligatorio");
        }

        if (tipoSolicitudRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new RuntimeException("Ya existe un tipo de solicitud con ese nombre");
        }

        TipoSolicitud tipo = new TipoSolicitud();
        tipo.setNombre(request.getNombre().trim());
        tipo.setDescripcion(request.getDescripcion());

        return tipoSolicitudRepository.save(tipo);
    }

    public List<TipoSolicitud> listarTiposSolicitud() {
        return tipoSolicitudRepository.findAll();
    }

    @Transactional
    public TipoSolicitud actualizarTipoSolicitud(Long id, UpdateTipoSolicitudRequest request) {
        TipoSolicitud tipo = tipoSolicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de solicitud no encontrado"));

        if (request.getNombre() != null && !request.getNombre().isBlank()) {
            tipo.setNombre(request.getNombre().trim());
        }

        tipo.setDescripcion(request.getDescripcion());

        return tipoSolicitudRepository.save(tipo);
    }

    @Transactional
    public void eliminarTipoSolicitud(Long id) {
        TipoSolicitud tipo = tipoSolicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de solicitud no encontrado"));

        tipoSolicitudRepository.delete(tipo);
    }

    @Transactional
    public Solicitud crearSolicitud(
            Long tipoSolicitudId,
            String descripcion,
            Integer documentoId,
            Integer vehiculoId,
            MultipartFile archivo
    ) {
        Usuario usuarioActual = currentUserService.getUsuarioActual();

        TipoSolicitud tipoSolicitud = tipoSolicitudRepository.findById(tipoSolicitudId)
                .orElseThrow(() -> new RuntimeException("Tipo de solicitud no encontrado"));

        Solicitud solicitud = new Solicitud();
        solicitud.setUsuario(usuarioActual);
        solicitud.setTipoSolicitud(tipoSolicitud);
        solicitud.setDescripcion(descripcion);
        solicitud.setEstado(EstadoSolicitud.EN_REVISION);

        if (documentoId != null) {
            Documento documento = documentoRepository.findById(documentoId)
                    .orElseThrow(() -> new RuntimeException("Documento no encontrado"));
            solicitud.setDocumento(documento);
        }

        if (vehiculoId != null) {
            Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId)
                    .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));
            solicitud.setVehiculo(vehiculo);
        }

        if (archivo != null && !archivo.isEmpty()) {
            String urlArchivo = guardarArchivo(archivo, usuarioActual);
            solicitud.setUrlDocumento(urlArchivo);
            solicitud.setNombreArchivo(archivo.getOriginalFilename());
        }

        Solicitud guardada = solicitudRepository.save(solicitud);

        historialSolicitudRepository.save(new HistorialSolicitud(
                guardada,
                EstadoSolicitud.EN_REVISION,
                "Solicitud creada por el usuario"
        ));

        usuarioRepository
                .findFirstByEmpresa_IdAndRol(usuarioActual.getEmpresa().getId(), RolUsuario.EMPRESA)
                .ifPresent(usuarioEmpresa -> {
                    String nombreCompleto = usuarioActual.getNombre() + " " + usuarioActual.getApellido();

                    notificacionService.notificarSolicitudCreadaAEmpresa(
                            usuarioEmpresa,
                            nombreCompleto,
                            tipoSolicitud.getNombre()
                    );
                });

        return guardada;
    }

    public List<Solicitud> listarSolicitudes() {
        Usuario usuarioActual = currentUserService.getUsuarioActual();

        if (
                usuarioActual.getRol() == RolUsuario.EMPRESA ||
                usuarioActual.getRol() == RolUsuario.ADMIN ||
                usuarioActual.getRol() == RolUsuario.SECRETARIA
        ) {
            return solicitudRepository.findByUsuarioEmpresaIdOrderByFechaEnvioDesc(
                    usuarioActual.getEmpresa().getId()
            );
        }

        return solicitudRepository.findByUsuarioIdOrderByFechaEnvioDesc(usuarioActual.getId());
    }

    public Solicitud obtenerSolicitud(Long id) {
        Usuario usuarioActual = currentUserService.getUsuarioActual();

        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if (
                usuarioActual.getRol() == RolUsuario.EMPRESA ||
                usuarioActual.getRol() == RolUsuario.ADMIN ||
                usuarioActual.getRol() == RolUsuario.SECRETARIA
        ) {
            Long empresaActualId = usuarioActual.getEmpresa().getId();
            Long empresaSolicitudId = solicitud.getUsuario().getEmpresa().getId();

            if (!empresaActualId.equals(empresaSolicitudId)) {
                throw new RuntimeException("No tienes permiso para ver esta solicitud");
            }

            return solicitud;
        }

        if (!solicitud.getUsuario().getId().equals(usuarioActual.getId())) {
            throw new RuntimeException("No tienes permiso para ver esta solicitud");
        }

        return solicitud;
    }

    @Transactional
    public Solicitud actualizarSolicitud(
            Long id,
            UpdateSolicitudRequest request,
            MultipartFile archivo
    ) {
        Usuario usuarioActual = currentUserService.getUsuarioActual();

        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if (!solicitud.getUsuario().getId().equals(usuarioActual.getId())) {
            throw new RuntimeException("No puedes editar una solicitud que no es tuya");
        }

        if (solicitud.getEstado() != EstadoSolicitud.EN_REVISION) {
            throw new RuntimeException("Solo puedes editar solicitudes en revisión");
        }

        if (request.getTipoSolicitudId() != null) {
            TipoSolicitud tipo = tipoSolicitudRepository.findById(request.getTipoSolicitudId())
                    .orElseThrow(() -> new RuntimeException("Tipo de solicitud no encontrado"));
            solicitud.setTipoSolicitud(tipo);
        }

        if (request.getDescripcion() != null) {
            solicitud.setDescripcion(request.getDescripcion());
        }

        if (request.getDocumentoId() != null) {
            Documento documento = documentoRepository.findById(request.getDocumentoId())
                    .orElseThrow(() -> new RuntimeException("Documento no encontrado"));
            solicitud.setDocumento(documento);
        }

        if (request.getVehiculoId() != null) {
            Vehiculo vehiculo = vehiculoRepository.findById(request.getVehiculoId())
                    .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));
            solicitud.setVehiculo(vehiculo);
        }

        if (archivo != null && !archivo.isEmpty()) {
            String urlArchivo = guardarArchivo(archivo, usuarioActual);
            solicitud.setUrlDocumento(urlArchivo);
            solicitud.setNombreArchivo(archivo.getOriginalFilename());
        }

        Solicitud actualizada = solicitudRepository.save(solicitud);

        historialSolicitudRepository.save(new HistorialSolicitud(
                actualizada,
                EstadoSolicitud.EN_REVISION,
                "Solicitud editada por el usuario"
        ));

        return actualizada;
    }

    @Transactional
    public Solicitud actualizarEstadoConArchivo(
            Long id,
            EstadoSolicitud estado,
            String observaciones,
            MultipartFile archivo
    ) {
        Usuario usuarioActual = currentUserService.getUsuarioActual();

        if (
                usuarioActual.getRol() != RolUsuario.EMPRESA &&
                usuarioActual.getRol() != RolUsuario.ADMIN &&
                usuarioActual.getRol() != RolUsuario.SECRETARIA
        ) {
            throw new RuntimeException("No tienes permiso para responder solicitudes");
        }

        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        Long empresaActualId = usuarioActual.getEmpresa().getId();
        Long empresaSolicitudId = solicitud.getUsuario().getEmpresa().getId();

        if (!empresaActualId.equals(empresaSolicitudId)) {
            throw new RuntimeException("No puedes responder solicitudes de otra empresa");
        }

        if (estado == null) {
            throw new RuntimeException("El estado es obligatorio");
        }

        if (estado == EstadoSolicitud.EN_REVISION) {
            throw new RuntimeException("Solo puedes aceptar o rechazar la solicitud");
        }

        solicitud.setEstado(estado);

        if (archivo != null && !archivo.isEmpty()) {
            String urlArchivo = guardarArchivo(archivo, solicitud.getUsuario());
            solicitud.setUrlDocumento(urlArchivo);
            solicitud.setNombreArchivo(archivo.getOriginalFilename());
        }

        Solicitud actualizada = solicitudRepository.save(solicitud);

        historialSolicitudRepository.save(new HistorialSolicitud(
                actualizada,
                estado,
                observaciones
        ));

        notificacionService.notificarSolicitudActualizada(
                solicitud.getUsuario(),
                solicitud.getTipoSolicitud().getNombre(),
                estado.name()
        );

        return actualizada;
    }

    @Transactional
    public void eliminarSolicitud(Long id) {
        Usuario usuarioActual = currentUserService.getUsuarioActual();

        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if (!solicitud.getUsuario().getId().equals(usuarioActual.getId())) {
            throw new RuntimeException("No puedes eliminar una solicitud que no es tuya");
        }

        if (solicitud.getEstado() != EstadoSolicitud.EN_REVISION) {
            throw new RuntimeException("Solo puedes eliminar solicitudes en revisión");
        }

        solicitudRepository.delete(solicitud);
    }

    public List<HistorialSolicitud> obtenerHistorial(Long solicitudId) {
        obtenerSolicitud(solicitudId);
        return historialSolicitudRepository.findBySolicitudIdOrderByFechaDesc(solicitudId);
    }

    private String guardarArchivo(MultipartFile archivo, Usuario usuarioReferencia) {
        try {
            String nombreOriginal = archivo.getOriginalFilename();

            if (nombreOriginal == null || nombreOriginal.isBlank()) {
                throw new RuntimeException("El archivo no tiene nombre válido");
            }

            Long empresaId = usuarioReferencia.getEmpresa().getId();
            Long usuarioId = usuarioReferencia.getId();

            String carpeta = "trackfile/empresa_" + empresaId + "/solicitudes/usuario_" + usuarioId;

            return cloudinaryService.subirArchivo(archivo, carpeta);

        } catch (Exception e) {
            throw new RuntimeException("Error al subir el archivo a Cloudinary: " + e.getMessage());
        }
    }
}