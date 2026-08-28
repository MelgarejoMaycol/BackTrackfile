package com.TrackFile.app.web.dto;

import java.time.LocalDate;

public class CreateConductorRequest {
    private Long idUsuario; // usuario ya creado con rol CONDUCTOR
    private String licenciaConduccion;
    private String categoriaLicencia;
    private LocalDate fechaVencimientoLicencia;

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public String getLicenciaConduccion() { return licenciaConduccion; }
    public void setLicenciaConduccion(String licenciaConduccion) { this.licenciaConduccion = licenciaConduccion; }

    public String getCategoriaLicencia() { return categoriaLicencia; }
    public void setCategoriaLicencia(String categoriaLicencia) { this.categoriaLicencia = categoriaLicencia; }

    public LocalDate getFechaVencimientoLicencia() { return fechaVencimientoLicencia; }
    public void setFechaVencimientoLicencia(LocalDate fechaVencimientoLicencia) { this.fechaVencimientoLicencia = fechaVencimientoLicencia; }
}