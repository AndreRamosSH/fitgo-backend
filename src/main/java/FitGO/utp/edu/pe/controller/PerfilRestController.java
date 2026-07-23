package FitGO.utp.edu.pe.controller;

import FitGO.utp.edu.pe.dto.PerfilRequest;
import FitGO.utp.edu.pe.entity.Usuario;
import FitGO.utp.edu.pe.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;

@RestController
@RequestMapping("/api/perfil")
public class PerfilRestController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public PerfilRestController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private Usuario obtenerUsuarioAutenticado(Authentication auth) {
        if (auth == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autorizado");
        }
        return usuarioRepository.findByCorreo(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    @GetMapping
    public ResponseEntity<?> obtenerPerfil(Authentication auth) {
        Usuario usuario = obtenerUsuarioAutenticado(auth);
        return ResponseEntity.ok(usuario.getDatosPerfil());
    }

    @PutMapping
    public ResponseEntity<?> actualizarPerfil(Authentication auth, @Valid @RequestBody PerfilRequest request) {
        Usuario usuario = obtenerUsuarioAutenticado(auth);

        if (request.getCorreo() != null && !request.getCorreo().equalsIgnoreCase(usuario.getCorreo())) {
            if (usuarioRepository.existsByCorreo(request.getCorreo())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "El correo ya está registrado"));
            }
            usuario.setCorreo(request.getCorreo());
        }

        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setTelefono(request.getTelefono());
        
        if (request.getFechaNacimiento() != null) {
            usuario.setFechaNacimiento(request.getFechaNacimiento());
        }
        if (request.getSexo() != null) {
            usuario.setSexo(request.getSexo());
        }
        if (request.getPesoObjetivo() != null) {
            usuario.setPesoObjetivo(request.getPesoObjetivo());
        }
        if (request.getGrasaObjetivo() != null) {
            usuario.setGrasaObjetivo(request.getGrasaObjetivo());
        }

        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            if (request.getPassword().length() < 6) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "La nueva contraseña debe tener al menos 6 caracteres"));
            }
            if (request.getPasswordActual() == null || request.getPasswordActual().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Debe proporcionar la contraseña actual"));
            }
            if (!passwordEncoder.matches(request.getPasswordActual(), usuario.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "La contraseña actual es incorrecta"));
            }
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        usuarioRepository.save(usuario);

        return ResponseEntity.ok(Map.of("mensaje", "Perfil actualizado exitosamente"));
    }
}
