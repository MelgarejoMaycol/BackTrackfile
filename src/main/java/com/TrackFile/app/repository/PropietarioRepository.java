package com.TrackFile.app.repository;

import com.TrackFile.app.domain.Propietario;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PropietarioRepository extends JpaRepository<Propietario, Integer> {

    @Query("""
        select p from Propietario p
        join p.usuario u
        where p.id = :id and u.empresa.id = :empresaId
    """)
    Optional<Propietario> findByIdAndEmpresaId(@Param("id") Integer id, @Param("empresaId") Long empresaId);

    @Query("""
        select p from Propietario p
        join p.usuario u
        where u.empresa.id = :empresaId
        order by u.nombre asc, u.apellido asc
    """)
    List<Propietario> findAllByEmpresaId(@Param("empresaId") Long empresaId);

    Optional<Propietario> findByUsuarioId(Long idUsuario);
}