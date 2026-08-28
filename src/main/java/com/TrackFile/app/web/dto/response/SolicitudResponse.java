package com.TrackFile.app.web.dto.response;

import com.TrackFile.app.domain.Solicitud;
import com.TrackFile.app.domain.enums.EstadoSolicitud;

import java.time.LocalDateTime;

public class SolicitudResponse {

    private Long id;
    private Long usuarioId;
    private String nombreUsuario;
    private String apellidoUsuario;
    private Long tipoSolicitudId;
    private String nombreTipoSolicitud;
    private String descripcion;
    private EstadoSolicitud estado;
    private LocalDateTime fechaEnvio;
    private String urlDocumento;
    private String nombreArchivo;
    private Integer documentoId;
    private Integer vehiculoId;
    private String placaVehiculo;

    public SolicitudResponse(Solicitud solicitud) {
        this.id = solicitud.getId();

        if (solicitud.getUsuario() != null) {
            this.usuarioId = solicitud.getUsuario().getId();
            this.nombreUsuario = solicitud.getUsuario().getNombre();
            this.apellidoUsuario = solicitud.getUsuario().getApellido();
        }

        if (solicitud.getTipoSolicitud() != null) {
            this.tipoSolicitudId = solicitud.getTipoSolicitud().getId();
            this.nombreTipoSolicitud = solicitud.getTipoSolicitud().getNombre();
        }

        this.descripcion = solicitud.getDescripcion();
        this.estado = solicitud.getEstado();
        this.fechaEnvio = solicitud.getFechaEnvio();
        this.urlDocumento = solicitud.getUrlDocumento();
        this.nombreArchivo = solicitud.getNombreArchivo();

        if (solicitud.getDocumento() != null) {
            this.documentoId = solicitud.getDocumento().getId();
        }

        if (solicitud.getVehiculo() != null) {
            this.vehiculoId = solicitud.getVehiculo().getId();
            this.placaVehiculo = solicitud.getVehiculo().getPlaca();
        }
    }

    public Long getId() {
        return id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getApellidoUsuario() {
        return apellidoUsuario;
    }

    public Long getTipoSolicitudId() {
        return tipoSolicitudId;
    }

    public String getNombreTipoSolicitud() {
        return nombreTipoSolicitud;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    public String getUrlDocumento() {
        return urlDocumento;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public Integer getDocumentoId() {
        return documentoId;
    }

    public Integer getVehiculoId() {
        return vehiculoId;
    }

    public String getPlacaVehiculo() {
        return placaVehiculo;
    }
}