package com.TrackFile.app.web.dto.response;

import com.TrackFile.app.domain.Notificacion;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class NotificacionResponse {

    private Long idNotificacion;
    private String titulo;
    private String mensaje;
    private LocalDateTime fechaEnvio;
    private String estado;
    private String urgencia;
    private String tipoAlerta;
    private LocalDate fechaVencimiento;
    private Boolean pushEnviado;
    private Boolean emailEnviado;

    public NotificacionResponse(Notificacion notificacion) {
        this.idNotificacion = notificacion.getIdNotificacion();
        this.titulo = notificacion.getTitulo();
        this.mensaje = notificacion.getMensaje();
        this.fechaEnvio = notificacion.getFechaEnvio();
        this.estado = notificacion.getEstado().name();
        this.urgencia = notificacion.getUrgencia().name();
        this.tipoAlerta = notificacion.getTipoAlerta().name();
        this.fechaVencimiento = notificacion.getFechaVencimiento();
        this.pushEnviado = notificacion.getPushEnviado();
        this.emailEnviado = notificacion.getEmailEnviado();
    }

    public Long getIdNotificacion() {
        return idNotificacion;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    public String getEstado() {
        return estado;
    }

    public String getUrgencia() {
        return urgencia;
    }

    public String getTipoAlerta() {
        return tipoAlerta;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public Boolean getPushEnviado() {
        return pushEnviado;
    }

    public Boolean getEmailEnviado() {
        return emailEnviado;
    }
}