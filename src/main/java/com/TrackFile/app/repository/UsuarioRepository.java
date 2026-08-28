package com.TrackFile.app.repository;

import com.TrackFile.app.domain.Usuario;
import com.TrackFile.app.domain.enums.RolUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByCorreo(String correo);

    boolean existsByCorreoIgnoreCase(String correo);

    boolean existsByNumeroDocumento(String numeroDocumento);

    Optional<Usuario> findByCorreo(String correo);

    List<Usuario> findAllByEmpresaId(Long empresaId);

    List<Usuario> findByEmpresa_Id(Long idEmpresa);

    List<Usuario> findByEmpresa_IdAndRol(Long idEmpresa, RolUsuario rol);

    Optional<Usuario> findFirstByEmpresa_IdAndRol(Long empresaId, RolUsuario rol);
}