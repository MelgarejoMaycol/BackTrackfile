package com.TrackFile.app.domain;

import com.TrackFile.app.domain.enums.EstadoNotificacion;
import com.TrackFile.app.domain.enums.TipoAlerta;
import com.TrackFile.app.domain.enums.UrgenciaNotificacion;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion")
    private Long idNotificacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoNotificacion estado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private UrgenciaNotificacion urgencia;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_alerta", nullable = false, length = 50)
    private TipoAlerta tipoAlerta;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(name = "push_enviado")
    private Boolean pushEnviado;

    @Column(name = "email_enviado")
    private Boolean emailEnviado;

    @PrePersist
    public void prePersist() {
        if (fechaEnvio == null) fechaEnvio = LocalDateTime.now();
        if (estado == null) estado = EstadoNotificacion.ENVIADA;
        if (urgencia == null) urgencia = UrgenciaNotificacion.MEDIA;
        if (pushEnviado == null) pushEnviado = false;
        if (emailEnviado == null) emailEnviado = false;
    }

    public Long getIdNotificacion() {
        return idNotificacion;
    }

    public void setIdNotificacion(Long idNotificacion) {
        this.idNotificacion = idNotificacion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(LocalDateTime fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public EstadoNotificacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoNotificacion estado) {
        this.estado = estado;
    }

    public UrgenciaNotificacion getUrgencia() {
        return urgencia;
    }

    public void setUrgencia(UrgenciaNotificacion urgencia) {
        this.urgencia = urgencia;
    }

    public TipoAlerta getTipoAlerta() {
        return tipoAlerta;
    }

    public void setTipoAlerta(TipoAlerta tipoAlerta) {
        this.tipoAlerta = tipoAlerta;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public Boolean getPushEnviado() {
        return pushEnviado;
    }

    public void setPushEnviado(Boolean pushEnviado) {
        this.pushEnviado = pushEnviado;
    }

    public Boolean getEmailEnviado() {
        return emailEnviado;
    }

    public void setEmailEnviado(Boolean emailEnviado) {
        this.emailEnviado = emailEnviado;
    }
}