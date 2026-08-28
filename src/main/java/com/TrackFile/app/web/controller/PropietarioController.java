package com.TrackFile.app.web.controller;

import com.TrackFile.app.service.PropietarioService;
import com.TrackFile.app.web.dto.CreatePropietarioRequest;
import com.TrackFile.app.web.dto.UpdateEstadoUsuarioRequest;
import com.TrackFile.app.web.dto.response.PersonaListaResponse;
import com.TrackFile.app.web.dto.response.PropietarioDetalleResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/propietarios")
public class PropietarioController {

    private final PropietarioService propietarioService;

    public PropietarioController(PropietarioService propietarioService) {
        this.propietarioService = propietarioService;
    }

    @GetMapping
    public List<PersonaListaResponse> listar() {
        return propietarioService.listar();
    }

    @GetMapping("/{idPropietario}/detalle")
    public PropietarioDetalleResponse obtenerDetalle(@PathVariable Long idPropietario) {
        return propietarioService.obtenerDetalle(idPropietario);
    }

    @PostMapping
    public PropietarioDetalleResponse crear(@RequestBody CreatePropietarioRequest request) {
        return propietarioService.crear(request);
    }

    @PutMapping("/{idPropietario}")
    public PropietarioDetalleResponse editar(
            @PathVariable Long idPropietario,
            @RequestBody CreatePropietarioRequest request
    ) {
        return propietarioService.editar(idPropietario, request);
    }

    @PatchMapping("/{idPropietario}/estado")
    public PropietarioDetalleResponse cambiarEstado(
            @PathVariable Long idPropietario,
            @RequestBody UpdateEstadoUsuarioRequest request
    ) {
        return propietarioService.cambiarEstado(idPropietario, request);
    }

    @DeleteMapping("/{idPropietario}")
    public PropietarioDetalleResponse eliminarLogico(@PathVariable Long idPropietario) {
        return propietarioService.eliminarLogico(idPropietario);
    }
}