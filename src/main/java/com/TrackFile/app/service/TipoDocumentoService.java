package com.TrackFile.app.service;

import com.TrackFile.app.web.dto.CreateTipoDocumentoRequest;
import com.TrackFile.app.web.dto.response.TipoDocumentoResponse;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TipoDocumentoService {

    private final JdbcTemplate jdbcTemplate;

    public TipoDocumentoService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<TipoDocumentoResponse> listar() {
        String sql = """
            SELECT
                id_tipo,
                nombre,
                descripcion
            FROM tipo_documento
            ORDER BY nombre ASC
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new TipoDocumentoResponse(
                ((Number) rs.getObject("id_tipo")).longValue(),
                rs.getString("nombre"),
                rs.getString("descripcion")
        ));
    }

    public TipoDocumentoResponse obtener(Long idTipo) {
        String sql = """
            SELECT
                id_tipo,
                nombre,
                descripcion
            FROM tipo_documento
            WHERE id_tipo = ?
            """;

        return jdbcTemplate.query(sql, rs -> {
            if (!rs.next()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo de documento no encontrado");
            }

            return new TipoDocumentoResponse(
                    ((Number) rs.getObject("id_tipo")).longValue(),
                    rs.getString("nombre"),
                    rs.getString("descripcion")
            );
        }, idTipo);
    }

    public TipoDocumentoResponse crear(CreateTipoDocumentoRequest req) {
        if (req.getNombre() == null || req.getNombre().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre es obligatorio");
        }

        String sql = """
            INSERT INTO tipo_documento (nombre, descripcion)
            VALUES (?, ?)
            RETURNING id_tipo, nombre, descripcion
            """;

        return jdbcTemplate.query(sql, rs -> {
            if (!rs.next()) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo crear el tipo de documento");
            }

            return new TipoDocumentoResponse(
                    ((Number) rs.getObject("id_tipo")).longValue(),
                    rs.getString("nombre"),
                    rs.getString("descripcion")
            );
        }, req.getNombre().trim().toUpperCase(), req.getDescripcion());
    }

    public TipoDocumentoResponse actualizar(Long idTipo, CreateTipoDocumentoRequest req) {
        if (req.getNombre() == null || req.getNombre().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre es obligatorio");
        }

        String sql = """
            UPDATE tipo_documento
            SET nombre = ?, descripcion = ?
            WHERE id_tipo = ?
            RETURNING id_tipo, nombre, descripcion
            """;

        return jdbcTemplate.query(sql, rs -> {
            if (!rs.next()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo de documento no encontrado");
            }

            return new TipoDocumentoResponse(
                    ((Number) rs.getObject("id_tipo")).longValue(),
                    rs.getString("nombre"),
                    rs.getString("descripcion")
            );
        }, req.getNombre().trim().toUpperCase(), req.getDescripcion(), idTipo);
    }

    public void eliminar(Long idTipo) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM tipo_documento WHERE id_tipo = ?",
                Integer.class,
                idTipo
        );

        if (count == null || count == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo de documento no encontrado");
        }

        jdbcTemplate.update("DELETE FROM tipo_documento WHERE id_tipo = ?", idTipo);
    }
}
