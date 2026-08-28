package com.TrackFile.app.web.controller;

import com.TrackFile.app.domain.TipoSolicitud;
import com.TrackFile.app.service.SolicitudService;
import com.TrackFile.app.web.dto.CreateTipoSolicitudRequest;
import com.TrackFile.app.web.dto.UpdateTipoSolicitudRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-solicitud")
@CrossOrigin("*")
public class TipoSolicitudController {

    private final SolicitudService solicitudService;

    public TipoSolicitudController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    @PostMapping
    public TipoSolicitud crear(@RequestBody CreateTipoSolicitudRequest request) {
        return solicitudService.crearTipoSolicitud(request);
    }

    @GetMapping
    public List<TipoSolicitud> listar() {
        return solicitudService.listarTiposSolicitud();
    }

    @PutMapping("/{id}")
    public TipoSolicitud actualizar(
            @PathVariable Long id,
            @RequestBody UpdateTipoSolicitudRequest request
    ) {
        return solicitudService.actualizarTipoSolicitud(id, request);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        solicitudService.eliminarTipoSolicitud(id);
    }
}