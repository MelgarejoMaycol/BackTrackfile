package com.TrackFile.app.domain;

import com.TrackFile.app.domain.enums.EstadoSolicitud;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "historial_solicitudes")
public class HistorialSolicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historial")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_solicitud", nullable = false)
    private Solicitud solicitud;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoSolicitud accion;

    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    public HistorialSolicitud() {}

    public HistorialSolicitud(Solicitud solicitud, EstadoSolicitud accion, String observaciones) {
        this.solicitud = solicitud;
        this.accion = accion;
        this.observaciones = observaciones;
        this.fecha = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Solicitud getSolicitud() {
        return solicitud;
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setSolicitud(Solicitud solicitud) {
        this.solicitud = solicitud;
    }

    public void setAccion(EstadoSolicitud accion) {
        this.accion = accion;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}