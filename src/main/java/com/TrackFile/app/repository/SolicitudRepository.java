package com.TrackFile.app.repository;

import com.TrackFile.app.domain.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {

    List<Solicitud> findByUsuarioIdOrderByFechaEnvioDesc(Long usuarioId);

    List<Solicitud> findByUsuarioEmpresaIdOrderByFechaEnvioDesc(Long empresaId);
}