package com.TrackFile.app.web.controller;

import com.TrackFile.app.domain.TipoMantenimiento;
import com.TrackFile.app.service.TipoMantenimientoService;
import com.TrackFile.app.web.dto.response.CreateTipoMantenimientoRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-mantenimiento")
public class TipoMantenimientoController {

    private final TipoMantenimientoService service;

    public TipoMantenimientoController(TipoMantenimientoService service) {
        this.service = service;
    }

    @PostMapping
    public TipoMantenimiento crear(@RequestBody CreateTipoMantenimientoRequest request) {
        return service.crear(request);
    }

    @GetMapping
    public List<TipoMantenimiento> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public TipoMantenimiento obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}