package com.TrackFile.app.web.dto.response;

import java.time.LocalDateTime;

public class ChatResponse {

    private Long id;
    private String mensajeUsuario;
    private String respuestaBot;
    private LocalDateTime fecha;

    public ChatResponse(Long id, String mensajeUsuario, String respuestaBot, LocalDateTime fecha) {
        this.id = id;
        this.mensajeUsuario = mensajeUsuario;
        this.respuestaBot = respuestaBot;
        this.fecha = fecha;
    }

    public Long getId() { return id; }
    public String getMensajeUsuario() { return mensajeUsuario; }
    public String getRespuestaBot() { return respuestaBot; }
    public LocalDateTime getFecha() { return fecha; }
}