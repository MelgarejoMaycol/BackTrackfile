package com.TrackFile.app.web.dto;

import com.TrackFile.app.domain.enums.EstadoSolicitud;

public class UpdateEstadoSolicitudRequest {

    private EstadoSolicitud estado;
    private String observaciones;

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setEstado(EstadoSolicitud estado) {
        this.estado = estado;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}