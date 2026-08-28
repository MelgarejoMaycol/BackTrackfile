package com.TrackFile.app.service;

import com.TrackFile.app.domain.Documento;
import com.TrackFile.app.domain.Notificacion;
import com.TrackFile.app.domain.Usuario;
import com.TrackFile.app.domain.enums.EstadoNotificacion;
import com.TrackFile.app.domain.enums.TipoAlerta;
import com.TrackFile.app.domain.enums.UrgenciaNotificacion;
import com.TrackFile.app.repository.DocumentoRepository;
import com.TrackFile.app.repository.NotificacionRepository;
import com.TrackFile.app.web.dto.response.NotificacionResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final CurrentUserService currentUserService;
    private final DocumentoRepository documentoRepository;

    public NotificacionService(
            NotificacionRepository notificacionRepository,
            CurrentUserService currentUserService,
            DocumentoRepository documentoRepository) {
        this.notificacionRepository = notificacionRepository;
        this.currentUserService = currentUserService;
        this.documentoRepository = documentoRepository;
    }

    public List<NotificacionResponse> listarMisNotificaciones() {
        Usuario usuario = currentUserService.getUsuarioActual();

        return notificacionRepository
                .findByUsuarioOrderByFechaEnvioDesc(usuario)
                .stream()
                .map(NotificacionResponse::new)
                .toList();
    }

    public List<NotificacionResponse> listarNoLeidas() {
        Usuario usuario = currentUserService.getUsuarioActual();

        return notificacionRepository
                .findByUsuarioAndEstadoOrderByFechaEnvioDesc(
                        usuario,
                        EstadoNotificacion.ENVIADA)
                .stream()
                .map(NotificacionResponse::new)
                .toList();
    }

    public long contarNoLeidas() {
        Usuario usuario = currentUserService.getUsuarioActual();

        return notificacionRepository.countByUsuarioAndEstado(
                usuario,
                EstadoNotificacion.ENVIADA);
    }

    public NotificacionResponse marcarComoLeida(Long idNotificacion) {
        Usuario usuarioActual = currentUserService.getUsuarioActual();

        Notificacion notificacion = notificacionRepository.findById(idNotificacion)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));

        if (!notificacion.getUsuario().getId().equals(usuarioActual.getId())) {
            throw new RuntimeException("No tienes permiso para modificar esta notificación");
        }

        notificacion.setEstado(EstadoNotificacion.LEIDA);

        return new NotificacionResponse(notificacionRepository.save(notificacion));
    }

    public void marcarTodasComoLeidas() {
        Usuario usuario = currentUserService.getUsuarioActual();

        List<Notificacion> notificaciones = notificacionRepository
                .findByUsuarioAndEstadoOrderByFechaEnvioDesc(
                        usuario,
                        EstadoNotificacion.ENVIADA);

        for (Notificacion notificacion : notificaciones) {
            notificacion.setEstado(EstadoNotificacion.LEIDA);
        }

        notificacionRepository.saveAll(notificaciones);
    }

    public void crearNotificacion(
            Usuario usuarioDestino,
            String titulo,
            String mensaje,
            TipoAlerta tipoAlerta,
            UrgenciaNotificacion urgencia) {
        if (usuarioDestino == null) {
            return;
        }

        Notificacion notificacion = new Notificacion();
        notificacion.setUsuario(usuarioDestino);
        notificacion.setTitulo(titulo);
        notificacion.setMensaje(mensaje);
        notificacion.setEstado(EstadoNotificacion.ENVIADA);
        notificacion.setUrgencia(urgencia);
        notificacion.setTipoAlerta(tipoAlerta);
        notificacion.setPushEnviado(false);
        notificacion.setEmailEnviado(false);

        notificacionRepository.save(notificacion);
    }

    public void notificarSolicitudCreadaAEmpresa(
            Usuario usuarioEmpresa,
            String nombreSolicitante,
            String tipoSolicitud) {
        crearNotificacion(
                usuarioEmpresa,
                "Nueva solicitud pendiente",
                nombreSolicitante + " creó una solicitud de " + tipoSolicitud + ". Requiere revisión.",
                TipoAlerta.SOLICITUD_CREADA,
                UrgenciaNotificacion.MEDIA);
    }

    public void notificarSolicitudActualizada(
            Usuario usuarioSolicitante,
            String tipoSolicitud,
            String estado) {
        crearNotificacion(
                usuarioSolicitante,
                "Solicitud actualizada",
                "Tu solicitud de " + tipoSolicitud + " fue actualizada a estado: " + estado + ".",
                TipoAlerta.SOLICITUD_ACTUALIZADA,
                UrgenciaNotificacion.MEDIA);
    }

    public void notificarMantenimientoActualizado(
            Usuario usuarioDestino,
            String placa,
            String estado) {
        crearNotificacion(
                usuarioDestino,
                "Mantenimiento actualizado",
                "El mantenimiento del vehículo " + placa + " cambió a estado: " + estado + ".",
                TipoAlerta.MANTENIMIENTO_ACTUALIZADO,
                UrgenciaNotificacion.MEDIA);
    }

    public String generarAlertasDocumentos() {
        LocalDate hoy = LocalDate.now();
        LocalDate limite = hoy.plusDays(30);

        List<Documento> documentos = documentoRepository
                .findByEstadoDocumentoTrueAndFechaVencimientoBetween(hoy, limite);

        int creadas = 0;

        for (Documento documento : documentos) {
            if (documento.getFechaVencimiento() == null) {
                continue;
            }

            Usuario usuarioDestino = documento.getUsuario();

            if (usuarioDestino == null && documento.getResponsableUsuario() != null) {
                usuarioDestino = documento.getResponsableUsuario();
            }

            if (usuarioDestino == null) {
                continue;
            }

            long diasRestantes = ChronoUnit.DAYS.between(
                    hoy,
                    documento.getFechaVencimiento());

            UrgenciaNotificacion urgencia = calcularUrgencia(diasRestantes);

            String nombreTipo = documento.getTipoDocumento() != null
                    ? documento.getTipoDocumento().getNombre()
                    : "Documento";

            String mensaje = "El documento " + nombreTipo +
                    " vence en " + diasRestantes + " días.";

            boolean existe = notificacionRepository
                    .existsByUsuarioAndTipoAlertaAndFechaVencimientoAndMensaje(
                            usuarioDestino,
                            TipoAlerta.DOCUMENTO_VENCIMIENTO,
                            documento.getFechaVencimiento(),
                            mensaje);

            if (existe) {
                continue;
            }

            Notificacion notificacion = new Notificacion();
            notificacion.setUsuario(usuarioDestino);
            notificacion.setTitulo("Documento próximo a vencer");
            notificacion.setMensaje(mensaje);
            notificacion.setEstado(EstadoNotificacion.ENVIADA);
            notificacion.setUrgencia(urgencia);
            notificacion.setTipoAlerta(TipoAlerta.DOCUMENTO_VENCIMIENTO);
            notificacion.setFechaVencimiento(documento.getFechaVencimiento());
            notificacion.setPushEnviado(false);
            notificacion.setEmailEnviado(false);

            notificacionRepository.save(notificacion);
            creadas++;
        }

        return "Alertas de documentos generadas: " + creadas;
    }

    @Scheduled(fixedRate = 60000)
    public void generarAlertasAutomaticasDiarias() {
        generarAlertasDocumentos();
    }

    private UrgenciaNotificacion calcularUrgencia(long diasRestantes) {
        if (diasRestantes <= 3) {
            return UrgenciaNotificacion.CRITICA;
        }

        if (diasRestantes <= 7) {
            return UrgenciaNotificacion.ALTA;
        }

        if (diasRestantes <= 15) {
            return UrgenciaNotificacion.MEDIA;
        }

        return UrgenciaNotificacion.BAJA;
    }

    public void notificarMantenimientoProximo(
            Usuario usuarioDestino,
            String placa,
            LocalDate fechaProgramada,
            long diasRestantes) {
        if (usuarioDestino == null) {
            return;
        }

        String mensaje = "El mantenimiento del vehículo " + placa +
                " está programado para dentro de " + diasRestantes + " días.";

        boolean existe = notificacionRepository
                .existsByUsuarioAndTipoAlertaAndFechaVencimientoAndMensaje(
                        usuarioDestino,
                        TipoAlerta.MANTENIMIENTO_PROXIMO,
                        fechaProgramada,
                        mensaje);

        if (existe) {
            return;
        }

        crearNotificacion(
                usuarioDestino,
                "Mantenimiento próximo",
                mensaje,
                TipoAlerta.MANTENIMIENTO_PROXIMO,
                diasRestantes <= 3
                        ? UrgenciaNotificacion.ALTA
                        : UrgenciaNotificacion.MEDIA);
    }
}