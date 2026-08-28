package com.TrackFile.app.repository;

import com.TrackFile.app.domain.Notificacion;
import com.TrackFile.app.domain.Usuario;
import com.TrackFile.app.domain.enums.EstadoNotificacion;
import com.TrackFile.app.domain.enums.TipoAlerta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByUsuarioOrderByFechaEnvioDesc(Usuario usuario);

    List<Notificacion> findByUsuarioAndEstadoOrderByFechaEnvioDesc(
            Usuario usuario,
            EstadoNotificacion estado
    );

    long countByUsuarioAndEstado(
            Usuario usuario,
            EstadoNotificacion estado
    );

    boolean existsByUsuarioAndTipoAlertaAndFechaVencimientoAndTitulo(
            Usuario usuario,
            TipoAlerta tipoAlerta,
            LocalDate fechaVencimiento,
            String titulo
    );
    boolean existsByUsuarioAndTipoAlertaAndFechaVencimientoAndMensaje(
        Usuario usuario,
        TipoAlerta tipoAlerta,
        LocalDate fechaVencimiento,
        String mensaje
);
}