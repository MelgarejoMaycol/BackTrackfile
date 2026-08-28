package com.TrackFile.app.repository;

import com.TrackFile.app.domain.Documento;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DocumentoRepository extends JpaRepository<Documento, Integer> {

    @Query("""
                select d from Documento d
                join d.vehiculo v
                join v.propietario p
                join p.usuario u
                where v.id = :vehiculoId
                  and u.empresa.id = :empresaId
                order by d.fechaCreacion desc
            """)
    List<Documento> findAllByVehiculoIdAndEmpresaId(
            @Param("vehiculoId") Integer vehiculoId,
            @Param("empresaId") Long empresaId);

    @Query("""
                select d from Documento d
                left join d.usuario du
                left join d.vehiculo v
                left join v.propietario p
                left join p.usuario up
                where d.id = :documentoId
                  and (
                        du.empresa.id = :empresaId
                        or up.empresa.id = :empresaId
                      )
            """)
    Optional<Documento> findByIdAndEmpresaId(
            @Param("documentoId") Integer documentoId,
            @Param("empresaId") Long empresaId);

    @Query("""
                select d from Documento d
                left join d.usuario du
                left join d.vehiculo v
                left join v.propietario p
                left join p.usuario up
                where (
                        du.empresa.id = :empresaId
                        or up.empresa.id = :empresaId
                      )
                order by d.fechaCreacion desc
            """)
    List<Documento> findAllByEmpresaId(@Param("empresaId") Long empresaId);

    @Query(value = """
            SELECT d.*
            FROM documentos d
            INNER JOIN vehiculos v
                ON d.id_vehiculo = v.id_vehiculo
            INNER JOIN propietarios p
                ON v.id_propietario = p.id_propietario
            INNER JOIN usuarios u
                ON p.id_usuario = u.id_usuario
            WHERE u.id_empresa = :empresaId
            AND d.estado_documento = true
            """, nativeQuery = true)
    List<Documento> buscarDocumentosVigentesPorEmpresa(@Param("empresaId") Long empresaId);

    @Modifying
    @Query("""
                update Documento d
                set d.estadoDocumento = false,
                    d.fechaActualizacion = CURRENT_TIMESTAMP
                where d.vehiculo.id = :idVehiculo
                  and d.tipoDocumento.id = :idTipo
                  and d.estadoDocumento = true
            """)
    int desactivarDocumentosActivosVehiculo(
            @Param("idVehiculo") Integer idVehiculo,
            @Param("idTipo") Integer idTipo);

    @Modifying
    @Query("""
                update Documento d
                set d.estadoDocumento = false,
                    d.fechaActualizacion = CURRENT_TIMESTAMP
                where d.usuario.id = :idUsuario
                  and d.vehiculo is null
                  and d.tipoDocumento.id = :idTipo
                  and d.estadoDocumento = true
            """)
    int desactivarDocumentosActivosUsuario(
            @Param("idUsuario") Long idUsuario,
            @Param("idTipo") Integer idTipo);

    List<Documento> findByEstadoDocumentoTrueAndFechaVencimientoBetween(
            LocalDate desde,
            LocalDate hasta);
}