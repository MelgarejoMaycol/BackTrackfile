package com.TrackFile.app.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "documentos")
public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_documento")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_vehiculo")
    private Vehiculo vehiculo;

    @ManyToOne
    @JoinColumn(name = "id_tipo", nullable = false)
    private TipoDocumento tipoDocumento;

    @Column(name = "nombre_archivo", length = 255)
    private String nombreArchivo;

    @Column(name = "url_storage", length = 255)
    private String urlStorage;

    @Column(name = "area", length = 100)
    private String area;

    @ManyToOne
    @JoinColumn(name = "responsable_usuario")
    private Usuario responsableUsuario;

    @Column(name = "fecha_creacion")
    private OffsetDateTime fechaCreacion = OffsetDateTime.now();

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "estado_documento")
    private Boolean estadoDocumento = true;

    @Column(name = "fecha_actualizacion")
    private OffsetDateTime fechaActualizacion;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public Integer getIdTipo() {
        return tipoDocumento != null ? tipoDocumento.getId() : null;
    }

    public void setIdTipo(Integer idTipo) {
        if (idTipo != null) {
            TipoDocumento tipo = new TipoDocumento();
            tipo.setId(idTipo);
            this.tipoDocumento = tipo;
        } else {
            this.tipoDocumento = null;
        }
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getUrlStorage() {
        return urlStorage;
    }

    public void setUrlStorage(String urlStorage) {
        this.urlStorage = urlStorage;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Usuario getResponsableUsuario() {
        return responsableUsuario;
    }

    public void setResponsableUsuario(Usuario responsableUsuario) {
        this.responsableUsuario = responsableUsuario;
    }

    public OffsetDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(OffsetDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
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

    public Boolean getEstadoDocumento() {
        return estadoDocumento;
    }

    public void setEstadoDocumento(Boolean estadoDocumento) {
        this.estadoDocumento = estadoDocumento;
    }

    public OffsetDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(OffsetDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}