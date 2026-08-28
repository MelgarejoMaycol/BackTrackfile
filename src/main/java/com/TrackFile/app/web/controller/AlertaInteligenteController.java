package com.TrackFile.app.web.controller;

import com.TrackFile.app.service.AlertaInteligenteService;
import com.TrackFile.app.web.dto.response.AlertaInteligenteResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alertas-inteligentes")
public class AlertaInteligenteController {

    private final AlertaInteligenteService alertaInteligenteService;

    public AlertaInteligenteController(AlertaInteligenteService alertaInteligenteService) {
        this.alertaInteligenteService = alertaInteligenteService;
    }

    @GetMapping
    public List<AlertaInteligenteResponse> listar() {
        return alertaInteligenteService.listarAlertas();
    }
}