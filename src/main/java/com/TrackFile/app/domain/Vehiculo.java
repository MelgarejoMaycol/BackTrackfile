package com.TrackFile.app.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "vehiculos")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vehiculo")
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_propietario", nullable = false)
    private Propietario propietario;

    // (Nuevo) conductor asignado (nullable)
    @ManyToOne
    @JoinColumn(name = "id_conductor")
    private Conductor conductor;

    @Column(unique = true, nullable = false, length = 20)
    private String placa;

    @Column(unique = true, length = 50)
    private String vin;

    private String marca;
    private String modelo;
    private Integer anio;
    private String color;

    @Column(name = "kilometraje_actual")
    private Integer kilometrajeActual = 0;

    @Column(name = "estado_vehiculo", length = 50)
    private String estadoVehiculo = "ACTIVO";

    @Column(name = "fecha_creacion")
    private OffsetDateTime fechaCreacion = OffsetDateTime.now();

    // getters/setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Propietario getPropietario() { return propietario; }
    public void setPropietario(Propietario propietario) { this.propietario = propietario; }

    public Conductor getConductor() { return conductor; }
    public void setConductor(Conductor conductor) { this.conductor = conductor; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) { this.anio = anio; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public Integer getKilometrajeActual() { return kilometrajeActual; }
    public void setKilometrajeActual(Integer kilometrajeActual) { this.kilometrajeActual = kilometrajeActual; }

    public String getEstadoVehiculo() { return estadoVehiculo; }
    public void setEstadoVehiculo(String estadoVehiculo) { this.estadoVehiculo = estadoVehiculo; }

    public OffsetDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(OffsetDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}