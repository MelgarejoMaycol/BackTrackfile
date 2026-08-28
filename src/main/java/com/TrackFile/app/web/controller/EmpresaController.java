package com.TrackFile.app.web.controller;

import com.TrackFile.app.domain.Empresa;
import com.TrackFile.app.repository.EmpresaRepository;
import com.TrackFile.app.service.CurrentUserService;
import com.TrackFile.app.service.EmpresaService;
import com.TrackFile.app.web.dto.response.EmpresaDetalleResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empresas")
@CrossOrigin("*")
public class EmpresaController {

    private final EmpresaRepository repo;
    private final EmpresaService empresaService;
    private final CurrentUserService currentUserService;

    public EmpresaController(
            EmpresaRepository repo,
            EmpresaService empresaService,
            CurrentUserService currentUserService
    ) {
        this.repo = repo;
        this.empresaService = empresaService;
        this.currentUserService = currentUserService;
    }

    // ✅ VER MI EMPRESA (IMPORTANTE PARA PERFIL)
    @GetMapping("/me")
    public ResponseEntity<Empresa> verMiEmpresa() {
        Empresa empresa = currentUserService.getUsuarioActual().getEmpresa();
        return ResponseEntity.ok(empresa);
    }

    // ✅ EDITAR MI EMPRESA
    @PutMapping("/me")
    public ResponseEntity<Empresa> editarMiEmpresa(@RequestBody Empresa datos) {

        Empresa empresa = currentUserService.getUsuarioActual().getEmpresa();

        // 🔒 SOLO CAMPOS EDITABLES
        empresa.setNombreEmpresa(datos.getNombreEmpresa());
        empresa.setTelefono(datos.getTelefono());
        empresa.setDireccion(datos.getDireccion());

        return ResponseEntity.ok(repo.save(empresa));
    }

    // 🔽 LOS DEMÁS ENDPOINTS QUE YA TENÍAS

    @GetMapping
    public List<Empresa> listar() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Empresa> obtener(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{idEmpresa}/detalle")
    public EmpresaDetalleResponse obtenerDetalle(@PathVariable Long idEmpresa) {
        return empresaService.obtenerDetalle(idEmpresa);
    }

    @PostMapping
    public ResponseEntity<Empresa> crear(@RequestBody Empresa e) {
        Empresa guardada = repo.save(e);
        return ResponseEntity.ok(guardada);
    }
}