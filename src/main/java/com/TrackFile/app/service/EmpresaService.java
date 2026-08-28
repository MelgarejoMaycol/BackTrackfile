package com.TrackFile.app.service;

import com.TrackFile.app.domain.EmailVerificationToken;
import com.TrackFile.app.domain.Empresa;
import com.TrackFile.app.domain.Usuario;
import com.TrackFile.app.domain.enums.RolUsuario;
import com.TrackFile.app.repository.EmailVerificationTokenRepository;
import com.TrackFile.app.repository.EmpresaRepository;
import com.TrackFile.app.repository.UsuarioRepository;
import com.TrackFile.app.web.dto.AuthResponse;
import com.TrackFile.app.web.dto.response.EmpresaDetalleResponse;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmailVerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final JdbcTemplate jdbcTemplate;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public EmpresaService(EmpresaRepository empresaRepository,
            UsuarioRepository usuarioRepository,
            EmailVerificationTokenRepository tokenRepository,
            PasswordEncoder passwordEncoder,
            JavaMailSender mailSender,
            JdbcTemplate jdbcTemplate) {
        this.empresaRepository = empresaRepository;
        this.usuarioRepository = usuarioRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public AuthResponse registrarEmpresaConUsuarioPrincipal(Empresa empresa,
            String rawPassword,
            MultipartFile rutPdf) {

        if (empresa == null) {
            throw new IllegalArgumentException("Empresa es obligatoria");
        }

        if (rutPdf == null || rutPdf.isEmpty()) {
            throw new IllegalArgumentException("Debe adjuntar el PDF del RUT");
        }

        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Debe enviar contraseña");
        }

        Set<String> correosPdf = extraerCorreosDelPdf(rutPdf);
        String correoIngresado = normalizeEmail(empresa.getCorreo());

        if (correoIngresado == null || correoIngresado.isBlank()) {
            throw new IllegalArgumentException("Debe enviar correo empresarial");
        }

        if (correosPdf.isEmpty()) {
            throw new IllegalArgumentException("No se encontró ningún correo dentro del PDF del RUT.");
        }

        if (!correosPdf.contains(correoIngresado)) {
            throw new IllegalArgumentException(
                    "El correo ingresado no coincide con ningún correo encontrado en el RUT. " +
                            "Correos detectados: " + correosPdf);
        }

        Empresa savedEmpresa = empresaRepository.save(empresa);

        Usuario u = new Usuario();
        u.setEmpresa(savedEmpresa);

        String rep = empresa.getRepresentanteLegal() == null ? "" : empresa.getRepresentanteLegal().trim();
        String[] parts = rep.isBlank() ? new String[] { "Representante", "Legal" } : rep.split("\\s+", 2);

        u.setNombre(parts[0]);
        u.setApellido(parts.length > 1 ? parts[1] : "N/A");

        u.setTipoDocumento("CC");
        u.setNumeroDocumento(
                (empresa.getCedulaRepresentante() == null || empresa.getCedulaRepresentante().isBlank())
                        ? UUID.randomUUID().toString()
                        : empresa.getCedulaRepresentante());

        u.setCorreo(correoIngresado);
        u.setTelefono(empresa.getTelefono());
        u.setDireccion(empresa.getDireccion());
        u.setRol(RolUsuario.EMPRESA);
        u.setContrasena(passwordEncoder.encode(rawPassword));
        u.setEmailConfirmado(false);

        Usuario savedUser = usuarioRepository.save(u);

        String token = crearTokenVerificacion(savedUser);
        String link = buildVerificationLink(token);

        enviarCorreoVerificacion(savedUser.getCorreo(), link);

        AuthResponse resp = new AuthResponse(
                savedUser.getId(),
                savedEmpresa.getId(),
                savedUser.getCorreo(),
                savedUser.getRol().name(),
                savedEmpresa.getEstadoVerificacion());

        resp.setEmailConfirmado(savedUser.getEmailConfirmado());
        resp.setVerificationLink(link);

        return resp;
    }

    @Transactional
    public boolean confirmarEmailPorToken(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }

        var opt = tokenRepository.findByToken(token);
        if (opt.isEmpty()) {
            return false;
        }

        var t = opt.get();

        if (t.getUsedAt() != null) {
            return false;
        }

        if (t.getExpiresAt() != null && t.getExpiresAt().isBefore(OffsetDateTime.now())) {
            return false;
        }

        Usuario u = usuarioRepository.findById(t.getUsuarioId()).orElse(null);
        if (u == null) {
            return false;
        }

        u.setEmailConfirmado(true);
        usuarioRepository.save(u);

        t.setUsedAt(OffsetDateTime.now());
        tokenRepository.save(t);

        return true;
    }

    public EmpresaDetalleResponse obtenerDetalle(Long idEmpresa) {
        String sql = """
                SELECT
                    e.id_empresa,
                    e.nombre_empresa,
                    e.nit,
                    e.direccion,
                    e.telefono,
                    e.correo,
                    e.cedula_representante,
                    COALESCE(CAST(e.estado_verificacion AS TEXT), 'ACTIVO') AS estado,
                    e.rut_pdf_url,
                    COALESCE(e.firma_rut_valida, FALSE) AS firma_valida,
                    e.fecha_creacion,
                    (
                        SELECT COUNT(*)
                        FROM usuarios u
                        WHERE u.id_empresa = e.id_empresa
                    ) AS cantidad_usuarios_asociados,
                    (
                        SELECT COUNT(*)
                        FROM conductores c
                        INNER JOIN usuarios u ON u.id_usuario = c.id_usuario
                        WHERE u.id_empresa = e.id_empresa
                    ) AS cantidad_conductores_asociados,
                    (
                        SELECT COUNT(*)
                        FROM propietarios p
                        INNER JOIN usuarios u ON u.id_usuario = p.id_usuario
                        WHERE u.id_empresa = e.id_empresa
                    ) AS cantidad_propietarios_asociados,
                    (
                        SELECT COUNT(*)
                        FROM vehiculos v
                        INNER JOIN propietarios p ON p.id_propietario = v.id_propietario
                        INNER JOIN usuarios u ON u.id_usuario = p.id_usuario
                        WHERE u.id_empresa = e.id_empresa
                    ) AS cantidad_vehiculos_asociados,
                    (
                        SELECT COUNT(*)
                        FROM documentos d
                        LEFT JOIN usuarios u ON u.id_usuario = d.id_usuario
                        LEFT JOIN vehiculos v ON v.id_vehiculo = d.id_vehiculo
                        LEFT JOIN propietarios p ON p.id_propietario = v.id_propietario
                        LEFT JOIN usuarios up ON up.id_usuario = p.id_usuario
                        WHERE (u.id_empresa = e.id_empresa OR up.id_empresa = e.id_empresa)
                          AND (d.fecha_vencimiento IS NULL OR d.fecha_vencimiento >= CURRENT_DATE)
                    ) AS cantidad_documentos_vigentes_empresa,
                    (
                        SELECT COUNT(*)
                        FROM documentos d
                        LEFT JOIN usuarios u ON u.id_usuario = d.id_usuario
                        LEFT JOIN vehiculos v ON v.id_vehiculo = d.id_vehiculo
                        LEFT JOIN propietarios p ON p.id_propietario = v.id_propietario
                        LEFT JOIN usuarios up ON up.id_usuario = p.id_usuario
                        WHERE (u.id_empresa = e.id_empresa OR up.id_empresa = e.id_empresa)
                    ) AS cantidad_documentos_totales_empresa
                FROM empresas e
                WHERE e.id_empresa = ?
                """;

        return jdbcTemplate.query(sql, rs -> {
            if (!rs.next()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa no encontrada");
            }

            return new EmpresaDetalleResponse(
                    ((Number) rs.getObject("id_empresa")).longValue(),
                    rs.getString("nombre_empresa"),
                    rs.getString("nit"),
                    rs.getString("direccion"),
                    rs.getString("telefono"),
                    rs.getString("correo"),
                    rs.getString("cedula_representante"),
                    rs.getString("estado"),
                    rs.getString("rut_pdf_url"),
                    rs.getObject("firma_valida") == null ? null : rs.getBoolean("firma_valida"),
                    rs.getObject("fecha_creacion", java.time.LocalDateTime.class),
                    rs.getObject("cantidad_usuarios_asociados") == null
                            ? null
                            : ((Number) rs.getObject("cantidad_usuarios_asociados")).longValue(),
                    rs.getObject("cantidad_conductores_asociados") == null
                            ? null
                            : ((Number) rs.getObject("cantidad_conductores_asociados")).longValue(),
                    rs.getObject("cantidad_propietarios_asociados") == null
                            ? null
                            : ((Number) rs.getObject("cantidad_propietarios_asociados")).longValue(),
                    rs.getObject("cantidad_vehiculos_asociados") == null
                            ? null
                            : ((Number) rs.getObject("cantidad_vehiculos_asociados")).longValue(),
                    rs.getObject("cantidad_documentos_vigentes_empresa") == null
                            ? null
                            : ((Number) rs.getObject("cantidad_documentos_vigentes_empresa")).longValue(),
                    rs.getObject("cantidad_documentos_totales_empresa") == null
                            ? null
                            : ((Number) rs.getObject("cantidad_documentos_totales_empresa")).longValue());
        }, idEmpresa);
    }

    private String crearTokenVerificacion(Usuario user) {
        String token = UUID.randomUUID().toString().replace("-", "");

        EmailVerificationToken t = new EmailVerificationToken();
        t.setUsuarioId(user.getId());
        t.setToken(token);
        t.setCreatedAt(OffsetDateTime.now());
        t.setExpiresAt(OffsetDateTime.now().plusHours(24));

        tokenRepository.save(t);
        return token;
    }

    private String buildVerificationLink(String token) {
        String cleanBase = (baseUrl == null) ? "http://localhost:8080" : baseUrl.trim();
        if (cleanBase.endsWith("/")) {
            cleanBase = cleanBase.substring(0, cleanBase.length() - 1);
        }
        return cleanBase + "/api/auth/verify-email?token=" + token;
    }

    @Value("${MAIL_FROM}")
    private String mailFrom;

    private void enviarCorreoVerificacion(String correo, String link) {
        try {

            SimpleMailMessage msg = new SimpleMailMessage();

            // 🔥 REMITENTE VALIDADO EN BREVO
            msg.setFrom(mailFrom);

            msg.setTo(correo);

            msg.setSubject("TrackFile - Verificación de correo");

            msg.setText(
                    "Hola,\n\n" +
                            "Para verificar tu correo, haz clic en el siguiente enlace:\n\n" +
                            link + "\n\n" +
                            "Este enlace expira en 24 horas.\n\n" +
                            "Si no fuiste tú, ignora este mensaje.");

            mailSender.send(msg);

            System.out.println("✅ Correo de verificación enviado a: " + correo);

        } catch (Exception ex) {

            System.out.println("⚠️ No se pudo enviar correo (SMTP). Link de verificación:");
            System.out.println(link);
            System.out.println("Detalle: " + ex.getMessage());
        }
    }

    private Set<String> extraerCorreosDelPdf(MultipartFile pdf) {
        Set<String> correos = new HashSet<>();

        try (PDDocument doc = PDDocument.load(pdf.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String texto = stripper.getText(doc);

            Pattern pattern = Pattern.compile(
                    "[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}",
                    Pattern.CASE_INSENSITIVE);

            Matcher matcher = pattern.matcher(texto);
            while (matcher.find()) {
                correos.add(matcher.group().toLowerCase(Locale.ROOT));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al leer el PDF del RUT", e);
        }

        return correos;
    }

    private String normalizeEmail(String email) {
        if (email == null) {
            return null;
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }
}