package com.TrackFile.app.web.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateMantenimientoRequest {

    private Long vehiculoId;
    private Long tipoMantenimientoId;

    private LocalDate fechaSugerida;
    private LocalDate fechaProgramada;

    private Integer kilometraje;
    private BigDecimal costo;

    private String taller;
    private String observaciones;

    // Getters y Setters

    public Long getVehiculoId() {
        return vehiculoId;
    }

    public void setVehiculoId(Long vehiculoId) {
        this.vehiculoId = vehiculoId;
    }

    public Long getTipoMantenimientoId() {
        return tipoMantenimientoId;
    }

    public void setTipoMantenimientoId(Long tipoMantenimientoId) {
        this.tipoMantenimientoId = tipoMantenimientoId;
    }

    public LocalDate getFechaSugerida() {
        return fechaSugerida;
    }

    public void setFechaSugerida(LocalDate fechaSugerida) {
        this.fechaSugerida = fechaSugerida;
    }

    public LocalDate getFechaProgramada() {
        return fechaProgramada;
    }

    public void setFechaProgramada(LocalDate fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
    }

    public Integer getKilometraje() {
        return kilometraje;
    }

    public void setKilometraje(Integer kilometraje) {
        this.kilometraje = kilometraje;
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public void setCosto(BigDecimal costo) {
        this.costo = costo;
    }

    public String getTaller() {
        return taller;
    }

    public void setTaller(String taller) {
        this.taller = taller;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}