package com.TrackFile.app.service;

import com.TrackFile.app.domain.Usuario;
import com.TrackFile.app.repository.UsuarioRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CurrentUserService {

    private final UsuarioRepository usuarioRepository;

    public CurrentUserService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario getUsuarioActual() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String correo = auth.getName();

        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado para el token"));
    }

    public Long getEmpresaIdActual() {
        return getUsuarioActual().getEmpresa().getId();
    }

    public List<Usuario> listarMiEmpresa() {
        Long empresaId = getEmpresaIdActual();
        return usuarioRepository.findAllByEmpresaId(empresaId);
    }
}