package com.TrackFile.app.repository;

import com.TrackFile.app.domain.Conductor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConductorRepository extends JpaRepository<Conductor, Integer> {

    Optional<Conductor> findByUsuarioId(Long idUsuario);

    Optional<Conductor> findByIdAndUsuarioEmpresaId(Integer id, Long empresaId);
}