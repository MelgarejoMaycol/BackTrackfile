package com.TrackFile.app.service;

import com.TrackFile.app.domain.Documento;
import com.TrackFile.app.repository.DocumentoRepository;
import com.TrackFile.app.web.dto.response.AlertaInteligenteResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class AlertaInteligenteService {

    private final DocumentoRepository documentoRepository;
    private final CurrentUserService currentUserService;

    public AlertaInteligenteService(DocumentoRepository documentoRepository,
            CurrentUserService currentUserService) {
        this.documentoRepository = documentoRepository;
        this.currentUserService = currentUserService;
    }

    public List<AlertaInteligenteResponse> listarAlertas() {
        Long empresaId = currentUserService.getEmpresaIdActual();

       List<Documento> documentos =
        documentoRepository.buscarDocumentosVigentesPorEmpresa(empresaId);

        List<AlertaInteligenteResponse> alertas = new ArrayList<>();
        LocalDate hoy = LocalDate.now();
        LocalDate limite30Dias = hoy.plusDays(30);

        for (Documento documento : documentos) {
            if (documento.getFechaVencimiento() == null) {
                continue;
            }

            String tipoDocumento = "Documento ID " + documento.getIdTipo();

            String placa = documento.getVehiculo() != null
                    ? documento.getVehiculo().getPlaca()
                    : "Sin placa";

            if (documento.getFechaVencimiento().isBefore(hoy)) {
                alertas.add(new AlertaInteligenteResponse(
                        "DOCUMENTO_VENCIDO",
                        "ALTA",
                        "Documento vencido",
                        "El documento " + tipoDocumento + " del vehículo " + placa + " está vencido.",
                        "Renovar este documento lo antes posible para evitar sanciones o problemas operativos.",
                        placa));
            } else if (!documento.getFechaVencimiento().isAfter(limite30Dias)) {
                long diasRestantes = java.time.temporal.ChronoUnit.DAYS.between(hoy, documento.getFechaVencimiento());

                String prioridad = diasRestantes <= 15 ? "MEDIA" : "BAJA";

                alertas.add(new AlertaInteligenteResponse(
                        "DOCUMENTO_PROXIMO_A_VENCER",
                        prioridad,
                        "Documento próximo a vencer",
                        "El documento " + tipoDocumento + " del vehículo " + placa + " vence en " + diasRestantes
                                + " días.",
                        "Programar la renovación antes de la fecha de vencimiento.",
                        placa));
            }
        }

        return alertas;
    }
}