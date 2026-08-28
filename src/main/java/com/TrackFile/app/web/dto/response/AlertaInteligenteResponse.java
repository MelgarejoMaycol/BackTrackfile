package com.TrackFile.app.web.dto.response;

public class AlertaInteligenteResponse {

    private String tipo;
    private String prioridad;
    private String titulo;
    private String mensaje;
    private String recomendacion;
    private String placaVehiculo;

    public AlertaInteligenteResponse() {
    }

    public AlertaInteligenteResponse(String tipo, String prioridad, String titulo,
                                     String mensaje, String recomendacion, String placaVehiculo) {
        this.tipo = tipo;
        this.prioridad = prioridad;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.recomendacion = recomendacion;
        this.placaVehiculo = placaVehiculo;
    }

    public String getTipo() { return tipo; }
    public String getPrioridad() { return prioridad; }
    public String getTitulo() { return titulo; }
    public String getMensaje() { return mensaje; }
    public String getRecomendacion() { return recomendacion; }
    public String getPlacaVehiculo() { return placaVehiculo; }

    public void setTipo(String tipo) { this.tipo = tipo; }
    public void setPrioridad(String prioridad) { this.prioridad = prioridad; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public void setRecomendacion(String recomendacion) { this.recomendacion = recomendacion; }
    public void setPlacaVehiculo(String placaVehiculo) { this.placaVehiculo = placaVehiculo; }
}