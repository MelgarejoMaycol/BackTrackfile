package com.TrackFile.app.service;

import com.TrackFile.app.domain.Propietario;
import com.TrackFile.app.domain.Usuario;
import com.TrackFile.app.domain.enums.EstadoUsuario;
import com.TrackFile.app.domain.enums.RolUsuario;
import com.TrackFile.app.repository.PropietarioRepository;
import com.TrackFile.app.repository.UsuarioRepository;
import com.TrackFile.app.web.dto.CreatePropietarioRequest;
import com.TrackFile.app.web.dto.UpdateEstadoUsuarioRequest;
import com.TrackFile.app.web.dto.response.PersonaListaResponse;
import com.TrackFile.app.web.dto.response.PropietarioDetalleResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropietarioService {

    private final PropietarioRepository propietarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final CurrentUserService currentUserService;

    public PropietarioService(
            PropietarioRepository propietarioRepository,
            UsuarioRepository usuarioRepository,
            CurrentUserService currentUserService
    ) {
        this.propietarioRepository = propietarioRepository;
        this.usuarioRepository = usuarioRepository;
        this.currentUserService = currentUserService;
    }

    public List<PersonaListaResponse> listar() {
        Long empresaId = currentUserService.getEmpresaIdActual();

        return propietarioRepository.findAllByEmpresaId(empresaId)
                .stream()
                .map(this::toListaResponse)
                .toList();
    }

    public PropietarioDetalleResponse obtenerDetalle(Long idPropietario) {
        Long empresaId = currentUserService.getEmpresaIdActual();

        Propietario propietario = propietarioRepository
                .findByIdAndEmpresaId(idPropietario.intValue(), empresaId)
                .orElseThrow(() -> new RuntimeException("Propietario no encontrado"));

        return toDetalleResponse(propietario);
    }

    public PropietarioDetalleResponse crear(CreatePropietarioRequest request) {
        Long empresaId = currentUserService.getEmpresaIdActual();

        Usuario usuario = usuarioRepository.findById(request.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!usuario.getEmpresa().getId().equals(empresaId)) {
            throw new RuntimeException("El usuario no pertenece a tu empresa");
        }

        propietarioRepository.findByUsuarioId(request.getIdUsuario())
                .ifPresent(p -> {
                    throw new RuntimeException("Este usuario ya está registrado como propietario");
                });

        usuario.setRol(RolUsuario.PROPIETARIO);
        usuario.setEstado(EstadoUsuario.ACTIVO);
        usuarioRepository.save(usuario);

        Propietario propietario = new Propietario();
        propietario.setUsuario(usuario);
        propietario.setDocumentoPropietario(request.getDocumentoPropietario());

        Propietario guardado = propietarioRepository.save(propietario);

        return toDetalleResponse(guardado);
    }

    public PropietarioDetalleResponse editar(Long idPropietario, CreatePropietarioRequest request) {
        Long empresaId = currentUserService.getEmpresaIdActual();

        Propietario propietario = propietarioRepository
                .findByIdAndEmpresaId(idPropietario.intValue(), empresaId)
                .orElseThrow(() -> new RuntimeException("Propietario no encontrado"));

        propietario.setDocumentoPropietario(request.getDocumentoPropietario());

        Propietario guardado = propietarioRepository.save(propietario);

        return toDetalleResponse(guardado);
    }

    public PropietarioDetalleResponse cambiarEstado(Long idPropietario, UpdateEstadoUsuarioRequest request) {
        Long empresaId = currentUserService.getEmpresaIdActual();

        Propietario propietario = propietarioRepository
                .findByIdAndEmpresaId(idPropietario.intValue(), empresaId)
                .orElseThrow(() -> new RuntimeException("Propietario no encontrado"));

        Usuario usuario = propietario.getUsuario();
        usuario.setEstado(request.getEstado());
        usuarioRepository.save(usuario);

        return toDetalleResponse(propietario);
    }

    public PropietarioDetalleResponse eliminarLogico(Long idPropietario) {
        Long empresaId = currentUserService.getEmpresaIdActual();

        Propietario propietario = propietarioRepository
                .findByIdAndEmpresaId(idPropietario.intValue(), empresaId)
                .orElseThrow(() -> new RuntimeException("Propietario no encontrado"));

        Usuario usuario = propietario.getUsuario();
        usuario.setEstado(EstadoUsuario.INACTIVO);
        usuarioRepository.save(usuario);

        return toDetalleResponse(propietario);
    }

    private PersonaListaResponse toListaResponse(Propietario propietario) {
        Usuario usuario = propietario.getUsuario();

        return new PersonaListaResponse(
                propietario.getId().longValue(),
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getNombre() + " " + usuario.getApellido(),
                usuario.getNumeroDocumento(),
                usuario.getCorreo(),
                usuario.getTelefono(),
                propietario.getDocumentoPropietario()
        );
    }

    private PropietarioDetalleResponse toDetalleResponse(Propietario propietario) {
        Usuario usuario = propietario.getUsuario();

        return new PropietarioDetalleResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getTipoDocumento(),
                usuario.getNumeroDocumento(),
                usuario.getCorreo(),
                usuario.getTelefono(),
                usuario.getDireccion(),
                usuario.getEstado().name(),
                usuario.getRol().name(),
                propietario.getId().longValue(),
                propietario.getDocumentoPropietario(),
                0L,
                0L,
                0L
        );
    }
}