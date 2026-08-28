package com.TrackFile.app.web.dto.response;

import java.time.LocalDateTime;

public record VehiculoDetalleResponse(
        Long idVehiculo,
        Long idPropietario,
        Long idConductor,
        String placa,
        String vin,
        String marca,
        String modelo,
        Integer anio,
        String color,
        String estadoVehiculo,
        LocalDateTime fechaCreacion,
        String nombrePropietario,
        String documentoPropietario,
        String nombreConductor,
        String licenciaConduccion,
        String telefonoConductor,
        Long cantidadDocumentosVigentesVehiculo,
        Long cantidadDocumentosTotalesVehiculo
) {}