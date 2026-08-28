package com.TrackFile.app.repository;

import com.TrackFile.app.domain.TipoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoSolicitudRepository extends JpaRepository<TipoSolicitud, Long> {
    boolean existsByNombreIgnoreCase(String nombre);
}