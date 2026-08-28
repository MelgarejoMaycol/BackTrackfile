package com.TrackFile.app.web.controller;

import com.TrackFile.app.domain.Usuario;
import com.TrackFile.app.domain.enums.EstadoUsuario;
import com.TrackFile.app.repository.UsuarioRepository;
import com.TrackFile.app.service.CurrentUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioRepository repo;
    private final CurrentUserService currentUserService;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(
            UsuarioRepository repo,
            CurrentUserService currentUserService,
            PasswordEncoder passwordEncoder
    ) {
        this.repo = repo;
        this.currentUserService = currentUserService;
        this.passwordEncoder = passwordEncoder;
    }

    // ✅ VER MI INFORMACIÓN PERSONAL
    @GetMapping("/me")
    public ResponseEntity<Usuario> miPerfil() {
        return ResponseEntity.ok(currentUserService.getUsuarioActual());
    }

    // ✅ EDITAR MI PERFIL
    @PutMapping("/me")
    public ResponseEntity<Usuario> editarMiPerfil(@RequestBody Usuario datos) {

        Usuario usuario = currentUserService.getUsuarioActual();

        usuario.setNombre(datos.getNombre());
        usuario.setApellido(datos.getApellido());
        usuario.setTelefono(datos.getTelefono());
        usuario.setDireccion(datos.getDireccion());

        return ResponseEntity.ok(repo.save(usuario));
    }

    // ✅ CAMBIAR CONTRASEÑA
    @PutMapping("/me/password")
    public ResponseEntity<?> cambiarPassword(@RequestBody CambiarPasswordRequest request) {

        Usuario usuario = currentUserService.getUsuarioActual();

        if (!passwordEncoder.matches(request.getPasswordActual(), usuario.getContrasena())) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "La contraseña actual no es correcta")
            );
        }

        usuario.setContrasena(passwordEncoder.encode(request.getPasswordNueva()));
        repo.save(usuario);

        return ResponseEntity.ok(
                Map.of("mensaje", "Contraseña actualizada correctamente")
        );
    }

    // ✅ LISTAR SOLO USUARIOS DE MI EMPRESA
    @GetMapping
    public List<Usuario> listar() {
        return currentUserService.listarMiEmpresa();
    }

    // ✅ OBTENER USUARIO POR ID
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtener(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ CREAR USUARIO
    @PostMapping
    public ResponseEntity<Usuario> crear(@RequestBody Usuario u) {

        u.setEmpresa(currentUserService.getUsuarioActual().getEmpresa());
        u.setContrasena(passwordEncoder.encode(u.getContrasena()));
        u.setEstado(EstadoUsuario.ACTIVO);

        if (u.getCorreo() != null) {
            u.setCorreo(u.getCorreo().toLowerCase());
        }

        return ResponseEntity.ok(repo.save(u));
    }

    // DTO INTERNO PARA CAMBIAR CONTRASEÑA
    public static class CambiarPasswordRequest {
        private String passwordActual;
        private String passwordNueva;

        public String getPasswordActual() {
            return passwordActual;
        }

        public void setPasswordActual(String passwordActual) {
            this.passwordActual = passwordActual;
        }

        public String getPasswordNueva() {
            return passwordNueva;
        }

        public void setPasswordNueva(String passwordNueva) {
            this.passwordNueva = passwordNueva;
        }
    }
}