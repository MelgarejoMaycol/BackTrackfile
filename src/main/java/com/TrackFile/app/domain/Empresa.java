package com.TrackFile.app.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "empresas")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empresa")
    private Long id;

    @Column(name = "nombre_empresa", nullable = false, length = 150)
    private String nombreEmpresa;

    @Column(unique = true, length = 50)
    private String nit;

    private String direccion;

    @Column(length = 20)
    private String telefono;

    @Column(length = 120)
    private String correo;

    @Column(name = "representante_legal", length = 150)
    private String representanteLegal;

    @Column(name = "cedula_representante", length = 50)
    private String cedulaRepresentante;

    @Column(name = "rut_pdf_url")
    private String rutPdfUrl;

    private Boolean firmaRutValida = false;

    @Column(name = "estado_verificacion", length = 50)
    private String estadoVerificacion = "PENDIENTE";

    @Column(name = "fecha_creacion")
    private OffsetDateTime fechaCreacion = OffsetDateTime.now();

    // =============================
    // Getters & Setters
    // =============================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombreEmpresa() { return nombreEmpresa; }
    public void setNombreEmpresa(String nombreEmpresa) { this.nombreEmpresa = nombreEmpresa; }

    public String getNit() { return nit; }
    public void setNit(String nit) { this.nit = nit; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getRepresentanteLegal() { return representanteLegal; }
    public void setRepresentanteLegal(String representanteLegal) { this.representanteLegal = representanteLegal; }

    public String getCedulaRepresentante() { return cedulaRepresentante; }
    public void setCedulaRepresentante(String cedulaRepresentante) { this.cedulaRepresentante = cedulaRepresentante; }

    public String getRutPdfUrl() { return rutPdfUrl; }
    public void setRutPdfUrl(String rutPdfUrl) { this.rutPdfUrl = rutPdfUrl; }

    public Boolean getFirmaRutValida() { return firmaRutValida; }
    public void setFirmaRutValida(Boolean firmaRutValida) { this.firmaRutValida = firmaRutValida; }

    public String getEstadoVerificacion() { return estadoVerificacion; }
    public void setEstadoVerificacion(String estadoVerificacion) { this.estadoVerificacion = estadoVerificacion; }

    public OffsetDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(OffsetDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
