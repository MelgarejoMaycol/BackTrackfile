package com.TrackFile.app.repository;

import com.TrackFile.app.domain.TipoMantenimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TipoMantenimientoRepository extends JpaRepository<TipoMantenimiento, Long> {

    List<TipoMantenimiento> findByEmpresaId(Long empresaId);

    Optional<TipoMantenimiento> findByIdAndEmpresaId(Long id, Long empresaId);

    boolean existsByNombreIgnoreCaseAndEmpresaId(String nombre, Long empresaId);
}