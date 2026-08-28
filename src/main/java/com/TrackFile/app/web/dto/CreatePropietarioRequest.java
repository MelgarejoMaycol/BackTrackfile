package com.TrackFile.app.web.dto;

public class CreatePropietarioRequest {
    private Long idUsuario; // usuario ya creado con rol PROPIETARIO
    private String documentoPropietario;

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }
    public String getDocumentoPropietario() { return documentoPropietario; }
    public void setDocumentoPropietario(String documentoPropietario) { this.documentoPropietario = documentoPropietario; }
}