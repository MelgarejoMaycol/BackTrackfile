package com.TrackFile.app.web.controller;

import com.TrackFile.app.service.ConductorService;
import com.TrackFile.app.web.dto.CreateConductorRequest;
import com.TrackFile.app.web.dto.UpdateEstadoUsuarioRequest;
import com.TrackFile.app.web.dto.response.ConductorDetalleResponse;
import com.TrackFile.app.web.dto.response.PersonaListaResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conductores")
public class ConductorController {

    private final ConductorService conductorService;

    public ConductorController(ConductorService conductorService) {
        this.conductorService = conductorService;
    }

    @GetMapping
    public List<PersonaListaResponse> listar() {
        return conductorService.listar();
    }

    @GetMapping("/{idConductor}/detalle")
    public ConductorDetalleResponse obtenerDetalle(@PathVariable Integer idConductor) {
        return conductorService.obtenerDetalle(idConductor);
    }

    @PostMapping
    public ConductorDetalleResponse crear(@RequestBody CreateConductorRequest request) {
        return conductorService.crear(request);
    }

    @PutMapping("/{idConductor}")
    public ConductorDetalleResponse editar(
            @PathVariable Integer idConductor,
            @RequestBody CreateConductorRequest request) {
        return conductorService.editar(idConductor, request);
    }

    @PatchMapping("/{idConductor}/estado")
    public ConductorDetalleResponse cambiarEstado(
            @PathVariable Integer idConductor,
            @RequestBody UpdateEstadoUsuarioRequest request) {
        return conductorService.cambiarEstado(idConductor, request);
    }

    @DeleteMapping("/{idConductor}")
    public ConductorDetalleResponse eliminarLogico(@PathVariable Integer idConductor) {
        return conductorService.eliminarLogico(idConductor);
    }
}