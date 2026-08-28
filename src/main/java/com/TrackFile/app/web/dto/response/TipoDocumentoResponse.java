package com.TrackFile.app.web.dto.response;

public record TipoDocumentoResponse(
        Long idTipo,
        String nombre,
        String descripcion
) {}
