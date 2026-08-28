package com.TrackFile.app.web.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record DocumentoDetalleResponse(
        Long idDocumento,
        Long idUsuario,
        Long idVehiculo,
        Long idTipo,
        String nombreArchivo,
        String urlStorage,
        String area,
        Long responsableUsuario,
        LocalDateTime fechaCreacion,
        LocalDate fechaVencimiento,
        String observaciones,
        String nombreTipoDocumento,
        String nombreUsuario,
        String apellidoUsuario,
        String placa,
        String nombrePropietario,
        String apellidoPropietario,
        String nombreResponsable,
        String apellidoResponsable,
        Boolean estadoDocumento,
        Integer diasRestantesVencimiento,
        String entidadRelacionadaTipo
) {}