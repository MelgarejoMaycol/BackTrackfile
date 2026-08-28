package com.TrackFile.app.web.dto;

import org.springframework.web.multipart.MultipartFile;

public class RegistroEmpresaRequest {

    private String nombreEmpresa;
    private String nit;
    private String correo;
    private String direccion;
    private String telefono;
    private String representanteLegal;
    private String cedulaRepresentante;

    // NUEVO: contraseña para el usuario principal (rol EMPRESA)
    private String contrasena;

    // PDF RUT
    private MultipartFile rutPdf;

    public String getDireccion() {
		return direccion;
	}
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	public String getNombreEmpresa() { return nombreEmpresa; }
    public void setNombreEmpresa(String nombreEmpresa) { this.nombreEmpresa = nombreEmpresa; }

    public String getNit() { return nit; }
    public void setNit(String nit) { this.nit = nit; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getRepresentanteLegal() { return representanteLegal; }
    public void setRepresentanteLegal(String representanteLegal) { this.representanteLegal = representanteLegal; }

    public String getCedulaRepresentante() { return cedulaRepresentante; }
    public void setCedulaRepresentante(String cedulaRepresentante) { this.cedulaRepresentante = cedulaRepresentante; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public MultipartFile getRutPdf() { return rutPdf; }
    public void setRutPdf(MultipartFile rutPdf) { this.rutPdf = rutPdf; }
}
