package com.TrackFile.app.web.dto;

import com.TrackFile.app.domain.enums.EstadoUsuario;

public class UpdateEstadoUsuarioRequest {

    private EstadoUsuario estado;

    public EstadoUsuario getEstado() {
        return estado;
    }

    public void setEstado(EstadoUsuario estado) {
        this.estado = estado;
    }
}