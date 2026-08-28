package com.TrackFile.app.web.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record DocumentoTablaResponse(
        Long idDocumento,
        Long idUsuario,
        Long idVehiculo,
        Long idTipo,
        String nombreArchivo,
        String area,
        LocalDateTime fechaCreacion,
        LocalDate fechaVencimiento,
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