package com.TrackFile.app.repository;

import com.TrackFile.app.domain.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Integer> {

    boolean existsByPlaca(String placa);

    @Query("""
        select v from Vehiculo v
        join v.propietario p
        join p.usuario u
        where u.empresa.id = :empresaId
    """)
    List<Vehiculo> findAllByEmpresaId(@Param("empresaId") Long empresaId);

    @Query("""
        select v from Vehiculo v
        join v.propietario p
        join p.usuario u
        where v.id = :vehiculoId and u.empresa.id = :empresaId
    """)
    Optional<Vehiculo> findByIdAndEmpresaId(@Param("vehiculoId") Integer vehiculoId,
                                            @Param("empresaId") Long empresaId);

    @Query("""
        select v from Vehiculo v
        join v.conductor c
        join c.usuario u
        where c.id = :conductorId and u.empresa.id = :empresaId
    """)
    List<Vehiculo> findAllByConductorIdAndEmpresaId(@Param("conductorId") Integer conductorId,
                                                    @Param("empresaId") Long empresaId);

    @Query("""
        select v from Vehiculo v
        join v.propietario p
        join p.usuario u
        where p.id = :propietarioId and u.empresa.id = :empresaId
    """)
    List<Vehiculo> findAllByPropietarioIdAndEmpresaId(@Param("propietarioId") Integer propietarioId,
                                                      @Param("empresaId") Long empresaId);
}