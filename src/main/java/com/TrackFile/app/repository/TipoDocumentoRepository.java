package com.TrackFile.app.repository;

import com.TrackFile.app.domain.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TipoDocumentoRepository extends JpaRepository<TipoDocumento, Integer> {

    List<TipoDocumento> findAllByOrderByNombreAsc();
}