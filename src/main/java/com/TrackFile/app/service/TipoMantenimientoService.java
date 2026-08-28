package com.TrackFile.app.service;

import com.TrackFile.app.domain.Empresa;
import com.TrackFile.app.domain.TipoMantenimiento;
import com.TrackFile.app.repository.TipoMantenimientoRepository;
import com.TrackFile.app.web.dto.response.CreateTipoMantenimientoRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoMantenimientoService {

    private final TipoMantenimientoRepository repository;
    private final CurrentUserService currentUserService;

    public TipoMantenimientoService(TipoMantenimientoRepository repository,
                                    CurrentUserService currentUserService) {
        this.repository = repository;
        this.currentUserService = currentUserService;
    }

    public TipoMantenimiento crear(CreateTipoMantenimientoRequest request) {

        Long empresaId = currentUserService.getEmpresaIdActual();

        if (repository.existsByNombreIgnoreCaseAndEmpresaId(request.getNombre(), empresaId)) {
            throw new RuntimeException("Ya existe un tipo de mantenimiento con ese nombre");
        }

        Empresa empresa = new Empresa();
        empresa.setId(empresaId);

        TipoMantenimiento tipo = new TipoMantenimiento();
        tipo.setNombre(request.getNombre());
        tipo.setDescripcion(request.getDescripcion());
        tipo.setEmpresa(empresa);

        return repository.save(tipo);
    }

    public List<TipoMantenimiento> listar() {
        return repository.findByEmpresaId(currentUserService.getEmpresaIdActual());
    }

    public TipoMantenimiento obtener(Long id) {
        return repository.findByIdAndEmpresaId(id, currentUserService.getEmpresaIdActual())
                .orElseThrow(() -> new RuntimeException("Tipo de mantenimiento no encontrado"));
    }

    public void eliminar(Long id) {
        TipoMantenimiento tipo = obtener(id);
        repository.delete(tipo);
    }
}