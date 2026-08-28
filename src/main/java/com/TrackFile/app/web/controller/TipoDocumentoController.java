package com.TrackFile.app.web.controller;

import com.TrackFile.app.service.TipoDocumentoService;
import com.TrackFile.app.web.dto.CreateTipoDocumentoRequest;
import com.TrackFile.app.web.dto.response.TipoDocumentoResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-documento")
public class TipoDocumentoController {

    private final TipoDocumentoService tipoDocumentoService;

    public TipoDocumentoController(TipoDocumentoService tipoDocumentoService) {
        this.tipoDocumentoService = tipoDocumentoService;
    }

    @GetMapping
    public List<TipoDocumentoResponse> listar() {
        return tipoDocumentoService.listar();
    }

    @GetMapping("/{idTipo}")
    public TipoDocumentoResponse obtener(@PathVariable Long idTipo) {
        return tipoDocumentoService.obtener(idTipo);
    }

    @PostMapping
    public TipoDocumentoResponse crear(@RequestBody CreateTipoDocumentoRequest req) {
        return tipoDocumentoService.crear(req);
    }

    @PutMapping("/{idTipo}")
    public TipoDocumentoResponse actualizar(@PathVariable Long idTipo,
                                            @RequestBody CreateTipoDocumentoRequest req) {
        return tipoDocumentoService.actualizar(idTipo, req);
    }

    @DeleteMapping("/{idTipo}")
    public void eliminar(@PathVariable Long idTipo) {
        tipoDocumentoService.eliminar(idTipo);
    }
}
