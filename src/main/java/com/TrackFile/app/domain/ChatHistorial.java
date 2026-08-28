package com.TrackFile.app.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_historial")
public class ChatHistorial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_chat")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "mensaje_usuario", nullable = false, columnDefinition = "TEXT")
    private String mensajeUsuario;

    @Column(name = "respuesta_bot", nullable = false, columnDefinition = "TEXT")
    private String respuestaBot;

    @Column(columnDefinition = "TEXT")
    private String metadatos;

    private LocalDateTime fecha;

    @PrePersist
    public void prePersist() {
        this.fecha = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getMensajeUsuario() { return mensajeUsuario; }
    public void setMensajeUsuario(String mensajeUsuario) { this.mensajeUsuario = mensajeUsuario; }

    public String getRespuestaBot() { return respuestaBot; }
    public void setRespuestaBot(String respuestaBot) { this.respuestaBot = respuestaBot; }

    public String getMetadatos() { return metadatos; }
    public void setMetadatos(String metadatos) { this.metadatos = metadatos; }

    public LocalDateTime getFecha() { return fecha; }
}