package com.TrackFile.app.web.controller;

import com.TrackFile.app.domain.Vehiculo;
import com.TrackFile.app.service.VehiculoService;
import com.TrackFile.app.web.dto.AssignConductorRequest;
import com.TrackFile.app.web.dto.CreateVehiculoRequest;
import com.TrackFile.app.web.dto.response.VehiculoDetalleResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController {

    private final VehiculoService vehiculoService;

    public VehiculoController(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

    @GetMapping
    public List<Vehiculo> listar() {
        return vehiculoService.listarMiFlota();
    }

    @GetMapping("/{id}")
    public Vehiculo obtener(@PathVariable Integer id) {
        return vehiculoService.obtener(id);
    }

    @GetMapping("/{id}/detalle")
    public VehiculoDetalleResponse obtenerDetalle(@PathVariable Long id) {
        return vehiculoService.obtenerDetalle(id);
    }

    @PostMapping
    public Vehiculo crear(@RequestBody CreateVehiculoRequest req) {
        return vehiculoService.crear(req);
    }

    @PutMapping("/{id}")
    public Vehiculo actualizar(@PathVariable Integer id, @RequestBody CreateVehiculoRequest req) {
        return vehiculoService.actualizar(id, req);
    }

    @PutMapping("/{id}/asignar-conductor")
    public Vehiculo asignarConductor(@PathVariable Integer id, @RequestBody AssignConductorRequest req) {
        return vehiculoService.asignarConductor(id, req);
    }

    @PutMapping("/{id}/desasignar-conductor")
    public Vehiculo desasignarConductor(@PathVariable Integer id) {
        return vehiculoService.desasignarConductor(id);
    }
}
