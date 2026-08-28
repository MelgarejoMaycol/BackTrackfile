package com.TrackFile.app.web.dto.response;

import com.TrackFile.app.domain.Mantenimiento;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MantenimientoResponse {

    public Long id;
    public Long vehiculoId;
    public String placa;
    public Long tipoMantenimientoId;
    public String tipoMantenimiento;
    public LocalDate fechaSugerida;
    public LocalDate fechaProgramada;
    public LocalDate fechaRealizado;
    public Integer kilometraje;
    public BigDecimal costo;
    public String taller;
    public String estado;
    public String observaciones;

    public MantenimientoResponse(Mantenimiento m) {
        this.id = m.getId();
        this.vehiculoId = Long.valueOf(m.getVehiculo().getId());
        this.placa = m.getVehiculo().getPlaca();
        this.tipoMantenimientoId = m.getTipoMantenimiento().getId();
        this.tipoMantenimiento = m.getTipoMantenimiento().getNombre();
        this.fechaSugerida = m.getFechaSugerida();
        this.fechaProgramada = m.getFechaProgramada();
        this.fechaRealizado = m.getFechaRealizado();
        this.kilometraje = m.getKilometraje();
        this.costo = m.getCosto();
        this.taller = m.getTaller();
        this.estado = m.getEstado().name();
        this.observaciones = m.getObservaciones();
    }
}