package com.TrackFile.app.repository;

import com.TrackFile.app.domain.HistorialSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistorialSolicitudRepository extends JpaRepository<HistorialSolicitud, Long> {

    List<HistorialSolicitud> findBySolicitudIdOrderByFechaDesc(Long solicitudId);
}