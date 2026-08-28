package com.TrackFile.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

@Entity
@Table(name = "propietarios")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Propietario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_propietario")
    private Integer id;

    @OneToOne(optional = false)
    @JoinColumn(name = "id_usuario", unique = true, nullable = false)
    private Usuario usuario;

    @Column(name = "documento_propietario", length = 100)
    private String documentoPropietario;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getDocumentoPropietario() { return documentoPropietario; }
    public void setDocumentoPropietario(String documentoPropietario) { this.documentoPropietario = documentoPropietario; }
}