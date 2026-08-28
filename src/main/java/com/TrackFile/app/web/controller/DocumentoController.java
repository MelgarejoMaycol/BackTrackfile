package com.TrackFile.app.web.controller;

import com.TrackFile.app.domain.Documento;
import com.TrackFile.app.service.DocumentoService;
import com.TrackFile.app.web.dto.CreateDocumentoRequest;
import com.TrackFile.app.web.dto.UpdateEstadoDocumentoRequest;
import com.TrackFile.app.web.dto.response.DocumentoDetalleResponse;
import com.TrackFile.app.web.dto.response.DocumentoTablaResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class DocumentoController {

    private final DocumentoService documentoService;

    public DocumentoController(DocumentoService documentoService) {
        this.documentoService = documentoService;
    }

    @GetMapping("/vehiculos/{vehiculoId}/documentos")
    public List<Documento> listarPorVehiculo(@PathVariable Integer vehiculoId) {
        return documentoService.listarPorVehiculo(vehiculoId);
    }

    @GetMapping("/documentos/{documentoId}")
    public Documento obtener(@PathVariable Integer documentoId) {
        return documentoService.obtener(documentoId);
    }

    @GetMapping("/documentos/{idDocumento}/detalle")
    public DocumentoDetalleResponse obtenerDetalle(@PathVariable Long idDocumento) {
        return documentoService.obtenerDetalle(idDocumento);
    }

    @GetMapping("/documentos/tabla")
    public List<DocumentoTablaResponse> listarTabla(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Integer diasMaximos
    ) {
        return documentoService.listarTabla(estado, diasMaximos);
    }

    @PostMapping(value = "/documentos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Documento crear(
            @RequestParam(required = false) Integer idVehiculo,
            @RequestParam(required = false) Long idUsuario,
            @RequestParam Integer idTipo,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) Long responsableUsuarioId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaVencimiento,
            @RequestParam(required = false) String observaciones,
            @RequestParam("archivo") MultipartFile archivo
    ) {
        CreateDocumentoRequest req = new CreateDocumentoRequest();
        req.setIdVehiculo(idVehiculo);
        req.setIdUsuario(idUsuario);
        req.setIdTipo(idTipo);
        req.setArea(area);
        req.setResponsableUsuarioId(responsableUsuarioId);
        req.setFechaVencimiento(fechaVencimiento);
        req.setObservaciones(observaciones);

        return documentoService.crear(req, archivo);
    }

    @PutMapping("/documentos/{documentoId}")
    public Documento actualizar(@PathVariable Integer documentoId,
                                @RequestBody CreateDocumentoRequest req) {
        return documentoService.actualizar(documentoId, req);
    }

    @DeleteMapping("/documentos/{documentoId}")
    public void eliminar(@PathVariable Integer documentoId) {
        documentoService.eliminar(documentoId);
    }
    @PutMapping("/documentos/{documentoId}/estado")
public Documento actualizarEstado(@PathVariable Integer documentoId,
                                  @RequestBody UpdateEstadoDocumentoRequest req) {
    return documentoService.actualizarEstado(documentoId, req.getEstado());
}
}
