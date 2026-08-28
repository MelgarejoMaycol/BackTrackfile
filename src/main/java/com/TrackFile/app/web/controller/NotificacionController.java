package com.TrackFile.app.web.controller;

import com.TrackFile.app.service.NotificacionService;
import com.TrackFile.app.web.dto.response.NotificacionResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @GetMapping
    public List<NotificacionResponse> listarMisNotificaciones() {
        return notificacionService.listarMisNotificaciones();
    }

    @GetMapping("/no-leidas")
    public List<NotificacionResponse> listarNoLeidas() {
        return notificacionService.listarNoLeidas();
    }

    @GetMapping("/contador")
    public Map<String, Long> contarNoLeidas() {
        return Map.of("noLeidas", notificacionService.contarNoLeidas());
    }

    @PatchMapping("/{id}/leer")
    public NotificacionResponse marcarComoLeida(@PathVariable Long id) {
        return notificacionService.marcarComoLeida(id);
    }

    @PatchMapping("/leer-todas")
    public Map<String, String> marcarTodasComoLeidas() {
        notificacionService.marcarTodasComoLeidas();
        return Map.of("mensaje", "Todas las notificaciones fueron marcadas como leídas");
    }

    @PostMapping("/generar-alertas/documentos")
    public Map<String, String> generarAlertasDocumentos() {
        return Map.of("mensaje", notificacionService.generarAlertasDocumentos());
    }
}