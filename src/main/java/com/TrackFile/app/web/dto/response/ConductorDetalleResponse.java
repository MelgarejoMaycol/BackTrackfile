package com.TrackFile.app.web.dto.response;

import java.time.LocalDate;

public record ConductorDetalleResponse(
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
        Long idConductor,
        String licenciaConduccion,
        String categoriaLicencia,
        LocalDate fechaVencimientoLicencia,
        Long cantidadDocumentosVigentesPersona,
        Long cantidadDocumentosTotalesPersona,
        Long cantidadVehiculosAsignados,
        Boolean licenciaVigente,
        Integer diasRestantesLicencia
) {}