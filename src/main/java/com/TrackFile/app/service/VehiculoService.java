package com.TrackFile.app.service;

import com.TrackFile.app.domain.Conductor;
import com.TrackFile.app.domain.Propietario;
import com.TrackFile.app.domain.Vehiculo;
import com.TrackFile.app.repository.ConductorRepository;
import com.TrackFile.app.repository.PropietarioRepository;
import com.TrackFile.app.repository.VehiculoRepository;
import com.TrackFile.app.web.dto.AssignConductorRequest;
import com.TrackFile.app.web.dto.CreateVehiculoRequest;
import com.TrackFile.app.web.dto.response.VehiculoDetalleResponse;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final PropietarioRepository propietarioRepository;
    private final ConductorRepository conductorRepository;
    private final CurrentUserService currentUserService;
    private final JdbcTemplate jdbcTemplate;

    public Vehiculo actualizar(Integer id, CreateVehiculoRequest req) {

        Vehiculo vehiculo = vehiculoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));

        // Validar placa duplicada (si cambia)
        if (req.getPlaca() != null && !req.getPlaca().equals(vehiculo.getPlaca())) {
            boolean existe = vehiculoRepository.existsByPlaca(req.getPlaca());
            if (existe) {
                throw new RuntimeException("Ya existe un vehículo con esa placa");
            }
            vehiculo.setPlaca(req.getPlaca());
        }

        // Actualizar campos
        if (req.getVin() != null)
            vehiculo.setVin(req.getVin());
        if (req.getMarca() != null)
            vehiculo.setMarca(req.getMarca());
        if (req.getModelo() != null)
            vehiculo.setModelo(req.getModelo());
        if (req.getAnio() != null)
            vehiculo.setAnio(req.getAnio());
        if (req.getColor() != null)
            vehiculo.setColor(req.getColor());
        if (req.getKilometrajeActual() != null)
            vehiculo.setKilometrajeActual(req.getKilometrajeActual());

        return vehiculoRepository.save(vehiculo);
    }

    public VehiculoService(VehiculoRepository vehiculoRepository,
            PropietarioRepository propietarioRepository,
            ConductorRepository conductorRepository,
            CurrentUserService currentUserService,
            JdbcTemplate jdbcTemplate) {
        this.vehiculoRepository = vehiculoRepository;
        this.propietarioRepository = propietarioRepository;
        this.conductorRepository = conductorRepository;
        this.currentUserService = currentUserService;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Vehiculo> listarMiFlota() {
        Long empresaId = currentUserService.getEmpresaIdActual();
        return vehiculoRepository.findAllByEmpresaId(empresaId);
    }

    public Vehiculo obtener(Integer id) {
        Long empresaId = currentUserService.getEmpresaIdActual();

        return vehiculoRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehículo no encontrado"));
    }

    public Vehiculo crear(CreateVehiculoRequest req) {
        if (req.getPlaca() == null || req.getPlaca().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La placa es obligatoria");
        }

        String placa = req.getPlaca().trim().toUpperCase();

        if (vehiculoRepository.existsByPlaca(placa)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La placa ya existe");
        }

        Long empresaId = currentUserService.getEmpresaIdActual();

        Propietario propietario = propietarioRepository
                .findByIdAndEmpresaId(req.getIdPropietario(), empresaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Propietario no pertenece a tu empresa"));

        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setPropietario(propietario);
        vehiculo.setPlaca(placa);
        vehiculo.setVin(req.getVin());
        vehiculo.setMarca(req.getMarca());
        vehiculo.setModelo(req.getModelo());
        vehiculo.setAnio(req.getAnio());
        vehiculo.setColor(req.getColor());

        if (req.getKilometrajeActual() != null) {
            vehiculo.setKilometrajeActual(req.getKilometrajeActual());
        }

        return vehiculoRepository.save(vehiculo);
    }

    public Vehiculo asignarConductor(Integer idVehiculo, AssignConductorRequest req) {
        Long empresaId = currentUserService.getEmpresaIdActual();

        Vehiculo vehiculo = vehiculoRepository.findByIdAndEmpresaId(idVehiculo, empresaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehículo no encontrado"));

        if (req.getIdConductor() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El idConductor es obligatorio");
        }

        Conductor conductor = conductorRepository.findByIdAndUsuarioEmpresaId(req.getIdConductor(), empresaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Conductor no pertenece a tu empresa"));

        vehiculo.setConductor(conductor);
        return vehiculoRepository.save(vehiculo);
    }

    public Vehiculo desasignarConductor(Integer idVehiculo) {
        Long empresaId = currentUserService.getEmpresaIdActual();

        Vehiculo vehiculo = vehiculoRepository.findByIdAndEmpresaId(idVehiculo, empresaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehículo no encontrado"));

        vehiculo.setConductor(null);
        return vehiculoRepository.save(vehiculo);
    }

    public VehiculoDetalleResponse obtenerDetalle(Long idVehiculo) {
        Long empresaId = currentUserService.getEmpresaIdActual();

        String sql = """
                SELECT
                    v.id_vehiculo,
                    v.id_propietario,
                    v.id_conductor,
                    v.placa,
                    v.vin,
                    v.marca,
                    v.modelo,
                    v.anio,
                    v.color,
                    CAST(v.estado_vehiculo AS TEXT) AS estado_vehiculo,
                    v.fecha_creacion,
                    CONCAT(up.nombre, ' ', up.apellido) AS nombre_propietario,
                    p.documento_propietario,
                    CONCAT(uc.nombre, ' ', uc.apellido) AS nombre_conductor,
                    c.licencia_conduccion,
                    uc.telefono AS telefono_conductor,
                    (
                        SELECT COUNT(*)
                        FROM documentos d
                        WHERE d.id_vehiculo = v.id_vehiculo
                          AND (d.fecha_vencimiento IS NULL OR d.fecha_vencimiento >= CURRENT_DATE)
                    ) AS cantidad_documentos_vigentes_vehiculo,
                    (
                        SELECT COUNT(*)
                        FROM documentos d
                        WHERE d.id_vehiculo = v.id_vehiculo
                    ) AS cantidad_documentos_totales_vehiculo
                FROM vehiculos v
                INNER JOIN propietarios p ON p.id_propietario = v.id_propietario
                INNER JOIN usuarios up ON up.id_usuario = p.id_usuario
                LEFT JOIN conductores c ON c.id_conductor = v.id_conductor
                LEFT JOIN usuarios uc ON uc.id_usuario = c.id_usuario
                WHERE v.id_vehiculo = ?
                  AND up.id_empresa = ?
                """;

        return jdbcTemplate.query(sql, rs -> {
            if (!rs.next()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehículo no encontrado");
            }

            return new VehiculoDetalleResponse(
                    ((Number) rs.getObject("id_vehiculo")).longValue(),
                    ((Number) rs.getObject("id_propietario")).longValue(),
                    rs.getObject("id_conductor") == null ? null : ((Number) rs.getObject("id_conductor")).longValue(),
                    rs.getString("placa"),
                    rs.getString("vin"),
                    rs.getString("marca"),
                    rs.getString("modelo"),
                    rs.getObject("anio") == null ? null : ((Number) rs.getObject("anio")).intValue(),
                    rs.getString("color"),
                    rs.getString("estado_vehiculo"),
                    rs.getObject("fecha_creacion", java.time.LocalDateTime.class),
                    rs.getString("nombre_propietario"),
                    rs.getString("documento_propietario"),
                    rs.getString("nombre_conductor"),
                    rs.getString("licencia_conduccion"),
                    rs.getString("telefono_conductor"),
                    rs.getObject("cantidad_documentos_vigentes_vehiculo") == null
                            ? null
                            : ((Number) rs.getObject("cantidad_documentos_vigentes_vehiculo")).longValue(),
                    rs.getObject("cantidad_documentos_totales_vehiculo") == null
                            ? null
                            : ((Number) rs.getObject("cantidad_documentos_totales_vehiculo")).longValue());
        }, idVehiculo, empresaId);
    }
}