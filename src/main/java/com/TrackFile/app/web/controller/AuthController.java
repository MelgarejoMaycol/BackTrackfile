package com.TrackFile.app.web.controller;

import com.TrackFile.app.config.JwtService;
import com.TrackFile.app.domain.Empresa;
import com.TrackFile.app.service.AuthService;
import com.TrackFile.app.service.EmpresaService;
import com.TrackFile.app.web.dto.AuthResponse;
import com.TrackFile.app.web.dto.LoginRequest;
import com.TrackFile.app.web.dto.RegistroEmpresaRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    private final EmpresaService empresaService;
    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(
            EmpresaService empresaService,
            AuthService authService,
            JwtService jwtService
    ) {
        this.empresaService = empresaService;
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping(value = "/registro-empresa", consumes = "multipart/form-data")
    public ResponseEntity<?> registrarEmpresa(@ModelAttribute RegistroEmpresaRequest req) {
        try {
            if (req.getRutPdf() == null || req.getRutPdf().isEmpty())
                return ResponseEntity.badRequest().body("Debe adjuntar el RUT (PDF).");

            if (req.getCorreo() == null || req.getCorreo().isBlank())
                return ResponseEntity.badRequest().body("Debe enviar correo.");

            if (req.getContrasena() == null || req.getContrasena().isBlank())
                return ResponseEntity.badRequest().body("Debe enviar contraseña.");

            if (req.getNit() == null || req.getNit().isBlank())
                return ResponseEntity.badRequest().body("Debe enviar NIT.");

            Empresa e = new Empresa();
            e.setNombreEmpresa(req.getNombreEmpresa());
            e.setNit(req.getNit());
            e.setCorreo(req.getCorreo());
            e.setTelefono(req.getTelefono());
            e.setDireccion(req.getDireccion());
            e.setRepresentanteLegal(req.getRepresentanteLegal());
            e.setCedulaRepresentante(req.getCedulaRepresentante());

            AuthResponse resp = empresaService.registrarEmpresaConUsuarioPrincipal(
                    e,
                    req.getContrasena(),
                    req.getRutPdf()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(resp);

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(500)
                    .body("Error interno registrando empresa: " + ex.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            AuthResponse resp = authService.login(req.getCorreo(), req.getContrasena());

            if (resp == null)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Correo o contraseña incorrectos.");

            if (!Boolean.TRUE.equals(resp.getEmailConfirmado())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Debe verificar su correo antes de iniciar sesión.");
            }

            return ResponseEntity.ok(resp);

        } catch (Exception ex) {
            return ResponseEntity.status(500)
                    .body("Error interno en login: " + ex.getMessage());
        }
    }

    @GetMapping("/refresh")
    public ResponseEntity<?> refreshToken(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
    ) {
        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token no enviado.");
            }

            String tokenActual = authorizationHeader.substring(7);
            String nuevoToken = jwtService.refreshToken(tokenActual);

            return ResponseEntity.ok(Map.of("token", nuevoToken));

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token inválido o expirado.");
        }
    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {
        try {
            boolean ok = empresaService.confirmarEmailPorToken(token);

            if (!ok)
                return ResponseEntity.badRequest()
                        .body("Token inválido, expirado o ya usado.");

            return ResponseEntity.ok(
                    "Correo verificado correctamente. Ya puedes iniciar sesión."
            );

        } catch (Exception ex) {
            return ResponseEntity.status(500)
                    .body("Error interno verificando email: " + ex.getMessage());
        }
    }
}