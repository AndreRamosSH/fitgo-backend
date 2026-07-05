package FitGO.utp.edu.pe.controller;

import FitGO.utp.edu.pe.dto.AsistenciaRequest;
import FitGO.utp.edu.pe.entity.Entrenador;
import FitGO.utp.edu.pe.entity.Usuario;
import FitGO.utp.edu.pe.repository.EntrenadorRepository;
import FitGO.utp.edu.pe.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/entrenador")
public class EntrenadorRestController {

    private final AuthService authService;
    private final EntrenadorRepository entrenadorRepository;

    public EntrenadorRestController(AuthService authService, EntrenadorRepository entrenadorRepository) {
        this.authService = authService;
        this.entrenadorRepository = entrenadorRepository;
    }

    @GetMapping("/resumen")
    public ResponseEntity<?> obtenerResumen(Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));

        List<Usuario> miembros = authService.listarMiembrosPorEntrenador(auth.getName());
        Map<String, Object> response = new HashMap<>();
        response.put("miembros", miembros);
        response.put("totalMiembros", miembros.size());
        response.put("asistencias", authService.listarAsistencias());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/miembros")
    public ResponseEntity<?> obtenerMiembros(Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));
        List<Usuario> miembros = authService.listarMiembrosPorEntrenador(auth.getName());
        return ResponseEntity.ok(Map.of("miembros", miembros, "totalMiembros", miembros.size()));
    }

    @GetMapping("/horario")
    public ResponseEntity<?> obtenerHorario(Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));

        Optional<Usuario> usuarioOpt = authService.buscarPorCorreo(auth.getName());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Usuario no encontrado"));
        }

        Entrenador entrenador = entrenadorRepository.findByUsuarioId(usuarioOpt.get().getId());
        if (entrenador == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Entrenador no encontrado"));
        }

        return ResponseEntity.ok(entrenador);
    }

    @GetMapping("/perfil")
    public ResponseEntity<?> obtenerPerfil(Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));

        Optional<Usuario> usuarioOpt = authService.buscarPorCorreo(auth.getName());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Usuario no encontrado"));
        }

        Usuario usuario = usuarioOpt.get();
        Entrenador entrenador = entrenadorRepository.findByUsuarioId(usuario.getId());
        if (entrenador == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Perfil de entrenador no encontrado"));
        }

        List<Usuario> miembros = authService.listarMiembrosPorEntrenador(auth.getName());

        Map<String, Object> response = new HashMap<>();
        response.put("nombre", usuario.getNombre());
        response.put("apellido", usuario.getApellido());
        response.put("correo", usuario.getCorreo());
        response.put("rol", usuario.getRol());
        response.put("turno", entrenador.getTurno());
        response.put("experienciaAnios", entrenador.getExperienciaAnios());
        response.put("maxMiembros", entrenador.getMaxMiembros());
        response.put("totalMiembrosAsignados", miembros.size());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/asistencia")
    public ResponseEntity<?> registrarAsistencia(Authentication auth, @RequestBody AsistenciaRequest request) {
        if (auth == null) return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));

        if (request.getCorreo() == null || request.getCorreo().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El correo del miembro es obligatorio"));
        }

        authService.registrarAsistencia(request);
        return ResponseEntity.ok(Map.of("mensaje", "Asistencia registrada exitosamente"));
    }
}
