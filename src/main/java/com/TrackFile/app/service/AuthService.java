package com.TrackFile.app.service;

import com.TrackFile.app.config.JwtService;
import com.TrackFile.app.domain.Usuario;
import com.TrackFile.app.repository.UsuarioRepository;
import com.TrackFile.app.web.dto.AuthResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Locale;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse login(String correo, String contrasena) {

        if (correo == null || correo.isBlank() || contrasena == null || contrasena.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Correo y contraseña son obligatorios");
        }

        String correoNormalizado = correo.trim().toLowerCase(Locale.ROOT);

        Usuario u = usuarioRepository.findByCorreo(correoNormalizado)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));

        if (!passwordEncoder.matches(contrasena, u.getContrasena())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }

        // 🔒 Bloquear si no confirmó correo
        if (u.getEmailConfirmado() == null || !u.getEmailConfirmado()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Debes verificar tu correo antes de iniciar sesión");
        }

        // ✅ Generar JWT
        String token = jwtService.generateToken(u.getCorreo());

        return buildResponse(u, token);
    }

    private AuthResponse buildResponse(Usuario u, String token) {

        Long empresaId = (u.getEmpresa() != null) ? u.getEmpresa().getId() : null;
        String estadoVerificacion = (u.getEmpresa() != null) ? u.getEmpresa().getEstadoVerificacion() : null;
        String nombreEmpresa = (u.getEmpresa() != null) ? u.getEmpresa().getNombreEmpresa() : null;

        AuthResponse resp = new AuthResponse(
                u.getId(),
                empresaId,
                u.getCorreo(),
                u.getRol().name(),
                estadoVerificacion
        );

        resp.setNombreEmpresa(nombreEmpresa);
        resp.setEmailConfirmado(true);
        resp.setToken(token);

        return resp;
    }
}