package com.TrackFile.app.web.dto.response;

import com.TrackFile.app.domain.HistorialSolicitud;
import com.TrackFile.app.domain.enums.EstadoSolicitud;

import java.time.LocalDateTime;

public class HistorialSolicitudResponse {

    private Long id;
    private EstadoSolicitud accion;
    private LocalDateTime fecha;
    private String observaciones;

    public HistorialSolicitudResponse(HistorialSolicitud historial) {
        this.id = historial.getId();
        this.accion = historial.getAccion();
        this.fecha = historial.getFecha();
        this.observaciones = historial.getObservaciones();
    }

    public Long getId() {
        return id;
    }

    public EstadoSolicitud getAccion() {
        return accion;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getObservaciones() {
        return observaciones;
    }
}