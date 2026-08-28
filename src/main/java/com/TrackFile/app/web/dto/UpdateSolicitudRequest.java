package com.TrackFile.app.web.dto;

public class UpdateSolicitudRequest {

    private Long tipoSolicitudId;
    private String descripcion;
    private Integer documentoId;
    private Integer vehiculoId;

    public Long getTipoSolicitudId() {
        return tipoSolicitudId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Integer getDocumentoId() {
        return documentoId;
    }

    public Integer getVehiculoId() {
        return vehiculoId;
    }

    public void setTipoSolicitudId(Long tipoSolicitudId) {
        this.tipoSolicitudId = tipoSolicitudId;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setDocumentoId(Integer documentoId) {
        this.documentoId = documentoId;
    }

    public void setVehiculoId(Integer vehiculoId) {
        this.vehiculoId = vehiculoId;
    }
}