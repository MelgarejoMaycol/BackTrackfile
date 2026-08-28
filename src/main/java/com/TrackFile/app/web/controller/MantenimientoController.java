package com.TrackFile.app.web.controller;

import com.TrackFile.app.service.MantenimientoService;
import com.TrackFile.app.web.dto.response.CreateMantenimientoRequest;
import com.TrackFile.app.web.dto.response.UpdateMantenimientoRequest;
import com.TrackFile.app.web.dto.response.MantenimientoResponse;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/mantenimientos")
public class MantenimientoController {

    private final MantenimientoService service;

    public MantenimientoController(MantenimientoService service) {
        this.service = service;
    }

    // ✅ Crear mantenimiento (ahora devuelve DTO)
    @PostMapping
    public MantenimientoResponse crear(@RequestBody CreateMantenimientoRequest request) {
        return new MantenimientoResponse(service.crear(request));
    }

    // ✅ Listar todos
    @GetMapping
    public List<MantenimientoResponse> listar() {
        return service.listar()
                .stream()
                .map(MantenimientoResponse::new)
                .collect(Collectors.toList());
    }

    // ✅ Obtener por ID
    @GetMapping("/{id}")
    public MantenimientoResponse obtener(@PathVariable Long id) {
        return new MantenimientoResponse(service.obtener(id));
    }

    // ✅ Listar por vehículo
    @GetMapping("/vehiculo/{vehiculoId}")
    public List<MantenimientoResponse> listarPorVehiculo(@PathVariable Long vehiculoId) {
        return service.listarPorVehiculo(vehiculoId)
                .stream()
                .map(MantenimientoResponse::new)
                .collect(Collectors.toList());
    }

    // ✅ Programados
    @GetMapping("/programados")
    public List<MantenimientoResponse> listarProgramados() {
        return service.listarProgramados()
                .stream()
                .map(MantenimientoResponse::new)
                .collect(Collectors.toList());
    }

    // ✅ Vencidos
    @GetMapping("/vencidos")
    public List<MantenimientoResponse> listarVencidos() {
        return service.listarVencidos()
                .stream()
                .map(MantenimientoResponse::new)
                .collect(Collectors.toList());
    }

    // ✅ Próximos
    @GetMapping("/proximos")
    public List<MantenimientoResponse> listarProximos() {
        return service.listarProximos()
                .stream()
                .map(MantenimientoResponse::new)
                .collect(Collectors.toList());
    }

    // 🔥 CORREGIDO: ahora devuelve DTO
    @PutMapping("/{id}")
    public MantenimientoResponse actualizar(@PathVariable Long id,
                                            @RequestBody UpdateMantenimientoRequest request) {
        return new MantenimientoResponse(service.actualizar(id, request));
    }

    // 🔥 CORREGIDO: ahora devuelve DTO
    @PutMapping("/{id}/realizar")
    public MantenimientoResponse marcarComoRealizado(@PathVariable Long id,
                                                     @RequestBody UpdateMantenimientoRequest request) {
        return new MantenimientoResponse(service.marcarComoRealizado(id, request));
    }

    // ✅ Eliminar
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}