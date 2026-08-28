package com.TrackFile.app.web.controller;

import com.TrackFile.app.domain.Solicitud;
import com.TrackFile.app.domain.enums.EstadoSolicitud;
import com.TrackFile.app.service.SolicitudService;
import com.TrackFile.app.web.dto.UpdateSolicitudRequest;
import com.TrackFile.app.web.dto.response.HistorialSolicitudResponse;
import com.TrackFile.app.web.dto.response.SolicitudResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
@CrossOrigin("*")
public class SolicitudController {

    private final SolicitudService solicitudService;

    public SolicitudController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public SolicitudResponse crear(
            @RequestParam Long tipoSolicitudId,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) Integer documentoId,
            @RequestParam(required = false) Integer vehiculoId,
            @RequestPart(required = false) MultipartFile archivo
    ) {
        Solicitud solicitud = solicitudService.crearSolicitud(
                tipoSolicitudId,
                descripcion,
                documentoId,
                vehiculoId,
                archivo
        );

        return new SolicitudResponse(solicitud);
    }

    @GetMapping
    public List<SolicitudResponse> listar() {
        return solicitudService.listarSolicitudes()
                .stream()
                .map(SolicitudResponse::new)
                .toList();
    }

    @GetMapping("/{id}")
    public SolicitudResponse obtener(@PathVariable Long id) {
        return new SolicitudResponse(solicitudService.obtenerSolicitud(id));
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public SolicitudResponse actualizar(
            @PathVariable Long id,
            @RequestParam(required = false) Long tipoSolicitudId,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) Integer documentoId,
            @RequestParam(required = false) Integer vehiculoId,
            @RequestPart(required = false) MultipartFile archivo
    ) {
        UpdateSolicitudRequest request = new UpdateSolicitudRequest();
        request.setTipoSolicitudId(tipoSolicitudId);
        request.setDescripcion(descripcion);
        request.setDocumentoId(documentoId);
        request.setVehiculoId(vehiculoId);

        return new SolicitudResponse(
                solicitudService.actualizarSolicitud(id, request, archivo)
        );
    }

    @PutMapping(value = "/{id}/estado", consumes = {"multipart/form-data"})
    public SolicitudResponse actualizarEstado(
            @PathVariable Long id,
            @RequestParam EstadoSolicitud estado,
            @RequestParam(required = false) String observaciones,
            @RequestPart(required = false) MultipartFile archivo
    ) {
        return new SolicitudResponse(
                solicitudService.actualizarEstadoConArchivo(id, estado, observaciones, archivo)
        );
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        solicitudService.eliminarSolicitud(id);
    }

    @GetMapping("/{id}/historial")
    public List<HistorialSolicitudResponse> historial(@PathVariable Long id) {
        return solicitudService.obtenerHistorial(id)
                .stream()
                .map(HistorialSolicitudResponse::new)
                .toList();
    }
}