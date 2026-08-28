package com.TrackFile.app.web.dto;

public class VerificacionEmpresaRequest {

    private boolean firmaValida;
    private String estadoVerificacion;

    public boolean isFirmaValida() { return firmaValida; }
    public void setFirmaValida(boolean firmaValida) { this.firmaValida = firmaValida; }

    public String getEstadoVerificacion() { return estadoVerificacion; }
    public void setEstadoVerificacion(String estadoVerificacion) { this.estadoVerificacion = estadoVerificacion; }
}
