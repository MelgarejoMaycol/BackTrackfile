package com.TrackFile.app.repository;

import com.TrackFile.app.domain.Mantenimiento;
import com.TrackFile.app.domain.enums.EstadoMantenimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MantenimientoRepository extends JpaRepository<Mantenimiento, Long> {

    List<Mantenimiento> findByEmpresaIdOrderByFechaCreacionDesc(Long empresaId);

    Optional<Mantenimiento> findByIdAndEmpresaId(Long id, Long empresaId);

    List<Mantenimiento> findByVehiculoIdAndEmpresaIdOrderByFechaCreacionDesc(Long vehiculoId, Long empresaId);

    List<Mantenimiento> findByEstadoAndEmpresaId(EstadoMantenimiento estado, Long empresaId);

    List<Mantenimiento> findByFechaProgramadaBeforeAndEstadoAndEmpresaId(
            LocalDate fecha,
            EstadoMantenimiento estado,
            Long empresaId
    );

    List<Mantenimiento> findByFechaProgramadaBetweenAndEstadoAndEmpresaId(
            LocalDate desde,
            LocalDate hasta,
            EstadoMantenimiento estado,
            Long empresaId
    );
}