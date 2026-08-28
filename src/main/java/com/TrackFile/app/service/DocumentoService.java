package com.TrackFile.app.service;

import com.TrackFile.app.domain.Documento;
import com.TrackFile.app.domain.Usuario;
import com.TrackFile.app.domain.Vehiculo;
import com.TrackFile.app.repository.DocumentoRepository;
import com.TrackFile.app.repository.UsuarioRepository;
import com.TrackFile.app.repository.VehiculoRepository;
import com.TrackFile.app.service.storage.CloudinaryService;
import com.TrackFile.app.web.dto.CreateDocumentoRequest;
import com.TrackFile.app.web.dto.response.DocumentoDetalleResponse;
import com.TrackFile.app.web.dto.response.DocumentoTablaResponse;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentoService {

    private final DocumentoRepository documentoRepository;
    private final VehiculoRepository vehiculoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CurrentUserService currentUserService;
    private final JdbcTemplate jdbcTemplate;
    private final CloudinaryService cloudinaryService;

    public DocumentoService(DocumentoRepository documentoRepository,
            VehiculoRepository vehiculoRepository,
            UsuarioRepository usuarioRepository,
            CurrentUserService currentUserService,
            JdbcTemplate jdbcTemplate,
            CloudinaryService cloudinaryService) {
        this.documentoRepository = documentoRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.usuarioRepository = usuarioRepository;
        this.currentUserService = currentUserService;
        this.jdbcTemplate = jdbcTemplate;
        this.cloudinaryService = cloudinaryService;
    }

    public List<Documento> listarPorVehiculo(Integer vehiculoId) {
        Long empresaId = currentUserService.getEmpresaIdActual();

        vehiculoRepository.findByIdAndEmpresaId(vehiculoId, empresaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehículo no encontrado"));

        return documentoRepository.findAllByVehiculoIdAndEmpresaId(vehiculoId, empresaId);
    }

    public Documento obtener(Integer documentoId) {
        return documentoRepository.findByIdAndEmpresaId(documentoId, currentUserService.getEmpresaIdActual())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Documento no encontrado"));
    }

    @Transactional
    public Documento crear(CreateDocumentoRequest req, MultipartFile archivo) {
        Long empresaId = currentUserService.getEmpresaIdActual();

        if (req.getIdTipo() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El idTipo es obligatorio");
        }

        if (archivo == null || archivo.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debes adjuntar un archivo");
        }

        if (req.getIdVehiculo() == null && req.getIdUsuario() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debes enviar idVehiculo o idUsuario");
        }

        Vehiculo vehiculo = null;
        if (req.getIdVehiculo() != null) {
            vehiculo = vehiculoRepository.findByIdAndEmpresaId(req.getIdVehiculo(), empresaId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.FORBIDDEN,
                            "El vehículo no pertenece a tu empresa"));
        }

        Usuario usuarioAsociado = null;
        if (req.getIdUsuario() != null) {
            usuarioAsociado = usuarioRepository.findById(req.getIdUsuario())
                    .filter(u -> u.getEmpresa() != null && empresaId.equals(u.getEmpresa().getId()))
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.FORBIDDEN,
                            "El usuario no pertenece a tu empresa"));
        }

        Usuario responsable = null;
        if (req.getResponsableUsuarioId() != null) {
            responsable = usuarioRepository.findById(req.getResponsableUsuarioId())
                    .filter(u -> u.getEmpresa() != null && empresaId.equals(u.getEmpresa().getId()))
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.FORBIDDEN,
                            "El responsable no pertenece a tu empresa"));
        }

        if (vehiculo != null) {
            documentoRepository.desactivarDocumentosActivosVehiculo(
                    vehiculo.getId(),
                    req.getIdTipo());
        }

        if (usuarioAsociado != null && vehiculo == null) {
            documentoRepository.desactivarDocumentosActivosUsuario(
                    usuarioAsociado.getId(),
                    req.getIdTipo());
        }

        Integer vehiculoIdForPath = (vehiculo != null) ? vehiculo.getId() : 0;
        ArchivoGuardado archivoGuardado = guardarArchivoPro(archivo, empresaId, vehiculoIdForPath);

        Documento documento = new Documento();
        documento.setUsuario(usuarioAsociado);
        documento.setVehiculo(vehiculo);
        documento.setIdTipo(req.getIdTipo());
        documento.setNombreArchivo(archivoGuardado.nombreOriginal());
        documento.setUrlStorage(archivoGuardado.urlStorage());
        documento.setArea(req.getArea());
        documento.setResponsableUsuario(responsable);
        documento.setFechaCreacion(OffsetDateTime.now());
        documento.setFechaActualizacion(OffsetDateTime.now());
        documento.setFechaVencimiento(req.getFechaVencimiento());
        documento.setObservaciones(req.getObservaciones());
        documento.setEstadoDocumento(true);

        return documentoRepository.save(documento);
    }

    public Documento actualizar(Integer documentoId, CreateDocumentoRequest req) {
        Long empresaId = currentUserService.getEmpresaIdActual();

        Documento documento = documentoRepository.findByIdAndEmpresaId(documentoId, empresaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Documento no encontrado"));

        if (req.getIdTipo() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El idTipo es obligatorio");
        }

        Vehiculo vehiculo = null;
        if (req.getIdVehiculo() != null) {
            vehiculo = vehiculoRepository.findByIdAndEmpresaId(req.getIdVehiculo(), empresaId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.FORBIDDEN,
                            "El vehículo no pertenece a tu empresa"));
        }

        Usuario usuarioAsociado = null;
        if (req.getIdUsuario() != null) {
            usuarioAsociado = usuarioRepository.findById(req.getIdUsuario())
                    .filter(u -> u.getEmpresa() != null && empresaId.equals(u.getEmpresa().getId()))
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.FORBIDDEN,
                            "El usuario no pertenece a tu empresa"));
        }

        Usuario responsable = null;
        if (req.getResponsableUsuarioId() != null) {
            responsable = usuarioRepository.findById(req.getResponsableUsuarioId())
                    .filter(u -> u.getEmpresa() != null && empresaId.equals(u.getEmpresa().getId()))
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.FORBIDDEN,
                            "El responsable no pertenece a tu empresa"));
        }

        documento.setUsuario(usuarioAsociado);
        documento.setVehiculo(vehiculo);
        documento.setIdTipo(req.getIdTipo());
        documento.setArea(req.getArea());
        documento.setResponsableUsuario(responsable);
        documento.setFechaVencimiento(req.getFechaVencimiento());
        documento.setObservaciones(req.getObservaciones());
        documento.setFechaActualizacion(OffsetDateTime.now());

        return documentoRepository.save(documento);
    }

    public Documento actualizarEstado(Integer documentoId, Boolean estado) {
        Long empresaId = currentUserService.getEmpresaIdActual();

        if (estado == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El estado es obligatorio");
        }

        Documento documento = documentoRepository.findByIdAndEmpresaId(documentoId, empresaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Documento no encontrado"));

        Usuario usuarioActual = currentUserService.getUsuarioActual();
        if (usuarioActual.getRol() == null || !"EMPRESA".equalsIgnoreCase(usuarioActual.getRol().name())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Solo el rol EMPRESA puede cambiar el estado del documento");
        }

        documento.setEstadoDocumento(estado);
        documento.setFechaActualizacion(OffsetDateTime.now());

        return documentoRepository.save(documento);
    }

    public void eliminar(Integer documentoId) {
        Documento doc = documentoRepository.findByIdAndEmpresaId(documentoId, currentUserService.getEmpresaIdActual())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Documento no encontrado"));

        documentoRepository.delete(doc);
    }

    public DocumentoDetalleResponse obtenerDetalle(Long idDocumento) {
        Long empresaId = currentUserService.getEmpresaIdActual();

        String sql = """
                SELECT
                    d.id_documento,
                    d.id_usuario,
                    d.id_vehiculo,
                    d.id_tipo,
                    d.nombre_archivo,
                    d.url_storage,
                    d.area,
                    d.responsable_usuario,
                    d.fecha_creacion,
                    d.fecha_vencimiento,
                    d.observaciones,
                    d.estado_documento,
                    td.nombre AS nombre_tipo_documento,
                    u.nombre AS nombre_usuario,
                    u.apellido AS apellido_usuario,
                    v.placa,
                    up.nombre AS nombre_propietario,
                    up.apellido AS apellido_propietario,
                    ur.nombre AS nombre_responsable,
                    ur.apellido AS apellido_responsable,
                    CASE
                        WHEN d.fecha_vencimiento IS NULL THEN NULL
                        ELSE (d.fecha_vencimiento - CURRENT_DATE)
                    END AS dias_restantes_vencimiento,
                    CASE
                        WHEN d.id_usuario IS NOT NULL AND d.id_vehiculo IS NULL THEN 'persona'
                        WHEN d.id_usuario IS NULL AND d.id_vehiculo IS NOT NULL THEN 'vehiculo'
                        WHEN d.id_usuario IS NOT NULL AND d.id_vehiculo IS NOT NULL THEN 'ambos'
                        ELSE 'sin_relacion'
                    END AS entidad_relacionada_tipo
                FROM documentos d
                INNER JOIN tipo_documento td ON td.id_tipo = d.id_tipo
                LEFT JOIN usuarios u ON u.id_usuario = d.id_usuario
                LEFT JOIN usuarios ur ON ur.id_usuario = d.responsable_usuario
                LEFT JOIN vehiculos v ON v.id_vehiculo = d.id_vehiculo
                LEFT JOIN propietarios p ON p.id_propietario = v.id_propietario
                LEFT JOIN usuarios up ON up.id_usuario = p.id_usuario
                WHERE d.id_documento = ?
                  AND (u.id_empresa = ? OR up.id_empresa = ?)
                """;

        return jdbcTemplate.query(sql, rs -> {
            if (!rs.next()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Documento no encontrado");
            }

            return new DocumentoDetalleResponse(
                    ((Number) rs.getObject("id_documento")).longValue(),
                    rs.getObject("id_usuario") == null ? null : ((Number) rs.getObject("id_usuario")).longValue(),
                    rs.getObject("id_vehiculo") == null ? null : ((Number) rs.getObject("id_vehiculo")).longValue(),
                    ((Number) rs.getObject("id_tipo")).longValue(),
                    rs.getString("nombre_archivo"),
                    rs.getString("url_storage"),
                    rs.getString("area"),
                    rs.getObject("responsable_usuario") == null ? null
                            : ((Number) rs.getObject("responsable_usuario")).longValue(),
                    rs.getObject("fecha_creacion", LocalDateTime.class),
                    rs.getObject("fecha_vencimiento", LocalDate.class),
                    rs.getString("observaciones"),
                    rs.getString("nombre_tipo_documento"),
                    rs.getString("nombre_usuario"),
                    rs.getString("apellido_usuario"),
                    rs.getString("placa"),
                    rs.getString("nombre_propietario"),
                    rs.getString("apellido_propietario"),
                    rs.getString("nombre_responsable"),
                    rs.getString("apellido_responsable"),
                    rs.getObject("estado_documento") == null ? null : rs.getBoolean("estado_documento"),
                    rs.getObject("dias_restantes_vencimiento") == null ? null
                            : ((Number) rs.getObject("dias_restantes_vencimiento")).intValue(),
                    rs.getString("entidad_relacionada_tipo"));
        }, idDocumento, empresaId, empresaId);
    }

    public List<DocumentoTablaResponse> listarTabla(String estado, Integer diasMaximos) {
        Long empresaId = currentUserService.getEmpresaIdActual();

        StringBuilder sql = new StringBuilder("""
                SELECT
                    d.id_documento,
                    d.id_usuario,
                    d.id_vehiculo,
                    d.id_tipo,
                    d.nombre_archivo,
                    d.area,
                    d.fecha_creacion,
                    d.fecha_vencimiento,
                    d.estado_documento,
                    td.nombre AS nombre_tipo_documento,
                    u.nombre AS nombre_usuario,
                    u.apellido AS apellido_usuario,
                    v.placa,
                    up.nombre AS nombre_propietario,
                    up.apellido AS apellido_propietario,
                    ur.nombre AS nombre_responsable,
                    ur.apellido AS apellido_responsable,
                    CASE
                        WHEN d.fecha_vencimiento IS NULL THEN NULL
                        ELSE (d.fecha_vencimiento - CURRENT_DATE)
                    END AS dias_restantes_vencimiento,
                    CASE
                        WHEN d.id_usuario IS NOT NULL AND d.id_vehiculo IS NULL THEN 'persona'
                        WHEN d.id_usuario IS NULL AND d.id_vehiculo IS NOT NULL THEN 'vehiculo'
                        WHEN d.id_usuario IS NOT NULL AND d.id_vehiculo IS NOT NULL THEN 'ambos'
                        ELSE 'sin_relacion'
                    END AS entidad_relacionada_tipo
                FROM documentos d
                INNER JOIN tipo_documento td ON td.id_tipo = d.id_tipo
                LEFT JOIN usuarios u ON u.id_usuario = d.id_usuario
                LEFT JOIN usuarios ur ON ur.id_usuario = d.responsable_usuario
                LEFT JOIN vehiculos v ON v.id_vehiculo = d.id_vehiculo
                LEFT JOIN propietarios p ON p.id_propietario = v.id_propietario
                LEFT JOIN usuarios up ON up.id_usuario = p.id_usuario
                WHERE (u.id_empresa = ? OR up.id_empresa = ?)
                """);

        List<Object> params = new ArrayList<>();
        params.add(empresaId);
        params.add(empresaId);

        if (estado != null && !estado.isBlank()) {
            if ("activo".equalsIgnoreCase(estado) || "true".equalsIgnoreCase(estado)) {
                sql.append(" AND d.estado_documento = TRUE ");
            } else if ("inactivo".equalsIgnoreCase(estado) || "false".equalsIgnoreCase(estado)) {
                sql.append(" AND d.estado_documento = FALSE ");
            }
        }

        if (diasMaximos != null) {
            sql.append(" AND d.fecha_vencimiento IS NOT NULL AND (d.fecha_vencimiento - CURRENT_DATE) <= ? ");
            params.add(diasMaximos);
        }

        sql.append(" ORDER BY d.fecha_creacion DESC ");

        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> new DocumentoTablaResponse(
                ((Number) rs.getObject("id_documento")).longValue(),
                rs.getObject("id_usuario") == null ? null : ((Number) rs.getObject("id_usuario")).longValue(),
                rs.getObject("id_vehiculo") == null ? null : ((Number) rs.getObject("id_vehiculo")).longValue(),
                ((Number) rs.getObject("id_tipo")).longValue(),
                rs.getString("nombre_archivo"),
                rs.getString("area"),
                rs.getObject("fecha_creacion", LocalDateTime.class),
                rs.getObject("fecha_vencimiento", LocalDate.class),
                rs.getString("nombre_tipo_documento"),
                rs.getString("nombre_usuario"),
                rs.getString("apellido_usuario"),
                rs.getString("placa"),
                rs.getString("nombre_propietario"),
                rs.getString("apellido_propietario"),
                rs.getString("nombre_responsable"),
                rs.getString("apellido_responsable"),
                rs.getObject("estado_documento") == null ? null : rs.getBoolean("estado_documento"),
                rs.getObject("dias_restantes_vencimiento") == null ? null
                        : ((Number) rs.getObject("dias_restantes_vencimiento")).intValue(),
                rs.getString("entidad_relacionada_tipo")), params.toArray());
    }

    private ArchivoGuardado guardarArchivoPro(MultipartFile archivo, Long empresaId, Integer vehiculoId) {
        try {
            String originalFilename = archivo.getOriginalFilename();

            if (originalFilename == null || originalFilename.isBlank()) {
                originalFilename = "archivo";
            }

            String originalName = StringUtils.cleanPath(originalFilename);

            String carpeta = "trackfile/empresa_" + empresaId + "/vehiculo_" + vehiculoId;

            String urlCloudinary = cloudinaryService.subirArchivo(archivo, carpeta);

            return new ArchivoGuardado(originalName, urlCloudinary);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "ERROR CLOUDINARY: " + e.getClass().getName() + " - " + e.getMessage());
        }
    }

    private record ArchivoGuardado(String nombreOriginal, String urlStorage) {
    }
}