package com.TrackFile.app.web.dto;

public class AuthResponse {

    private Long usuarioId;
    private Long empresaId;
    private String nombreEmpresa;
    private String correo;
    private String rol;
    private String estadoVerificacion;
    private Boolean emailConfirmado;
    private String verificationLink;
    private String token;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    // 🔹 NUEVO: link de verificación (Fase 1, sin SMTP)
    

    public AuthResponse() {}

    public AuthResponse(Long usuarioId,
                        Long empresaId,
                        String correo,
                        String rol,
                        String estadoVerificacion) {
        this.usuarioId = usuarioId;
        this.empresaId = empresaId;
        this.correo = correo;
        this.rol = rol;
        this.estadoVerificacion = estadoVerificacion;
    }

    // =========================
    // Getters & Setters
    // =========================

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getEstadoVerificacion() {
        return estadoVerificacion;
    }

    public void setEstadoVerificacion(String estadoVerificacion) {
        this.estadoVerificacion = estadoVerificacion;
    }

    public Boolean getEmailConfirmado() {
        return emailConfirmado;
    }

    public void setEmailConfirmado(Boolean emailConfirmado) {
        this.emailConfirmado = emailConfirmado;
    }

    public String getVerificationLink() {
        return verificationLink;
    }

    public void setVerificationLink(String verificationLink) {
        this.verificationLink = verificationLink;
    }
}
