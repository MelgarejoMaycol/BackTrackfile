package com.TrackFile.app.web.dto.response;

import java.time.LocalDateTime;

public record EmpresaDetalleResponse(
        Long idEmpresa,
        String nombreEmpresa,
        String nit,
        String direccion,
        String telefono,
        String correo,
        String cedulaRepresentante,
        String estado,
        String rutPdfUrl,
        Boolean firmaValida,
        LocalDateTime fechaCreacion,
        Long cantidadUsuariosAsociados,
        Long cantidadConductoresAsociados,
        Long cantidadPropietariosAsociados,
        Long cantidadVehiculosAsociados,
        Long cantidadDocumentosVigentesEmpresa,
        Long cantidadDocumentosTotalesEmpresa
) {}