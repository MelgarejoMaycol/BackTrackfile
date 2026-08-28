package com.TrackFile.app.domain;

import com.TrackFile.app.domain.enums.EstadoMantenimiento;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "mantenimientos")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Mantenimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔥 CORREGIDO
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vehiculo", nullable = false)
    private Vehiculo vehiculo;

    // 🔥 CORREGIDO
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_mantenimiento", nullable = false)
    private TipoMantenimiento tipoMantenimiento;

    // 🔥 CORREGIDO
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empresa", nullable = false)
    private Empresa empresa;

    private LocalDate fechaSugerida;

    private LocalDate fechaProgramada;

    private LocalDate fechaRealizado;

    private Integer kilometraje;

    private BigDecimal costo;

    private String taller;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoMantenimiento estado = EstadoMantenimiento.SUGERIDO;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    private LocalDateTime fechaCreacion;

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();

        if (this.estado == null) {
            this.estado = EstadoMantenimiento.SUGERIDO;
        }
    }

    // ===== GETTERS Y SETTERS =====

    public Long getId() {
        return id;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public TipoMantenimiento getTipoMantenimiento() {
        return tipoMantenimiento;
    }

    public void setTipoMantenimiento(TipoMantenimiento tipoMantenimiento) {
        this.tipoMantenimiento = tipoMantenimiento;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
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

    public LocalDate getFechaRealizado() {
        return fechaRealizado;
    }

    public void setFechaRealizado(LocalDate fechaRealizado) {
        this.fechaRealizado = fechaRealizado;
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

    public EstadoMantenimiento getEstado() {
        return estado;
    }

    public void setEstado(EstadoMantenimiento estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
}