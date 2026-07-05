package FitGO.utp.edu.pe.controller;

import FitGO.utp.edu.pe.dto.PerfilRequest;
import FitGO.utp.edu.pe.entity.Usuario;
import FitGO.utp.edu.pe.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/perfil")
public class PerfilRestController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public PerfilRestController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public ResponseEntity<?> obtenerPerfil(Authentication auth) {
        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No autorizado"));
        }
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(auth.getName());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Usuario no encontrado"));
        }

        Usuario usuario = usuarioOpt.get();
        Map<String, Object> details = new HashMap<>();
        details.put("nombre", usuario.getNombre());
        details.put("apellido", usuario.getApellido());
        details.put("correo", usuario.getCorreo());
        details.put("rol", usuario.getRol());
        details.put("telefono", usuario.getTelefono());

        return ResponseEntity.ok(details);
    }

    @PutMapping
    public ResponseEntity<?> actualizarPerfil(Authentication auth, @RequestBody PerfilRequest request) {
        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No autorizado"));
        }
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(auth.getName());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Usuario no encontrado"));
        }

        Usuario usuario = usuarioOpt.get();

        if (request.getCorreo() != null && !request.getCorreo().equalsIgnoreCase(usuario.getCorreo())) {
            if (usuarioRepository.existsByCorreo(request.getCorreo())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "El correo ya está registrado"));
            }
            usuario.setCorreo(request.getCorreo());
        }

        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setTelefono(request.getTelefono());

        if (request.getPassword() != null && !request.getPassword().trim().isEmpty() && request.getPassword().length() >= 6) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        usuarioRepository.save(usuario);

        return ResponseEntity.ok(Map.of("mensaje", "Perfil actualizado exitosamente"));
    }
}
