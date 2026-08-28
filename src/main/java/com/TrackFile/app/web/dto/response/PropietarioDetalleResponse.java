package com.TrackFile.app.web.dto.response;

public record PropietarioDetalleResponse(
        Long idUsuario,
        String nombre,
        String apellido,
        String tipoDocumento,
        String numeroDocumento,
        String correo,
        String telefono,
        String direccion,
        String estado,
        String rol,
        Long idPropietario,
        String documentoPropietario,
        Long cantidadDocumentosVigentesPersona,
        Long cantidadDocumentosTotalesPersona,
        Long cantidadVehiculosPropios
) {}