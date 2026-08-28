package com.TrackFile.app.web.dto.response;

public record PersonaListaResponse(
        Long id,
        Long idUsuario,
        String nombre,
        String apellido,
        String nombreCompleto,
        String numeroDocumento,
        String correo,
        String telefono,
        String extra
) {}