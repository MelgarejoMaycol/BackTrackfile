package com.TrackFile.app.repository;

import com.TrackFile.app.domain.ChatHistorial;
import com.TrackFile.app.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatHistorialRepository extends JpaRepository<ChatHistorial, Long> {

    List<ChatHistorial> findByUsuarioOrderByFechaDesc(Usuario usuario);
}