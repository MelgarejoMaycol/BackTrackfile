package com.TrackFile.app.web.dto;

import java.time.LocalDate;

public class CreateDocumentoRequest {

    private Integer idVehiculo;
    private Long idUsuario;
    private Integer idTipo;
    private String area;
    private Long responsableUsuarioId;
    private LocalDate fechaVencimiento;
    private String observaciones;

    public Integer getIdVehiculo() {
        return idVehiculo;
    }

    public void setIdVehiculo(Integer idVehiculo) {
        this.idVehiculo = idVehiculo;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Integer getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(Integer idTipo) {
        this.idTipo = idTipo;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Long getResponsableUsuarioId() {
        return responsableUsuarioId;
    }

    public void setResponsableUsuarioId(Long responsableUsuarioId) {
        this.responsableUsuarioId = responsableUsuarioId;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
