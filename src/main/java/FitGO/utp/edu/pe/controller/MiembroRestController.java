package FitGO.utp.edu.pe.controller;

import FitGO.utp.edu.pe.entity.Entrenador;
import FitGO.utp.edu.pe.entity.Membresia;
import FitGO.utp.edu.pe.entity.Usuario;
import FitGO.utp.edu.pe.repository.EntrenadorRepository;
import FitGO.utp.edu.pe.repository.MembresiaRepository;
import FitGO.utp.edu.pe.service.AsistenciaService;
import FitGO.utp.edu.pe.service.AuthService;
import FitGO.utp.edu.pe.service.DashboardMiembroService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/miembro")
public class MiembroRestController {

    private final AuthService authService;
    private final AsistenciaService asistenciaService;
    private final DashboardMiembroService dashboardMiembroService;
    private final EntrenadorRepository entrenadorRepository;
    private final MembresiaRepository membresiaRepository;

    public MiembroRestController(AuthService authService,
                                  AsistenciaService asistenciaService,
                                  DashboardMiembroService dashboardMiembroService,
                                  EntrenadorRepository entrenadorRepository,
                                  MembresiaRepository membresiaRepository) {
        this.authService = authService;
        this.asistenciaService = asistenciaService;
        this.dashboardMiembroService = dashboardMiembroService;
        this.entrenadorRepository = entrenadorRepository;
        this.membresiaRepository = membresiaRepository;
    }

    @GetMapping("/resumen")
    public ResponseEntity<?> obtenerResumen(Authentication auth) {
        if (auth == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));
        }

        String correo = auth.getName();
        Optional<Usuario> usuarioOpt = authService.buscarPorCorreo(correo);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Usuario no encontrado"));
        }

        Usuario usuario = usuarioOpt.get();
        int racha = asistenciaService.calcularRachaActual(correo);
        Double pesoActual = dashboardMiembroService.obtenerUltimoPeso(correo);
        Membresia membresia = dashboardMiembroService.obtenerMembresiaReciente(correo);

        Map<String, Object> response = new HashMap<>();
        response.put("usuario", Map.of(
                "nombre", usuario.getNombre(),
                "apellido", usuario.getApellido(),
                "correo", usuario.getCorreo(),
                "rol", usuario.getRol(),
                "entrenador", usuario.getEntrenador() != null ? Map.of(
                        "id", usuario.getEntrenador().getId(),
                        "nombre", usuario.getEntrenador().getUsuario().getNombre() + " " + usuario.getEntrenador().getUsuario().getApellido()
                ) : "Sin asignar"
        ));
        response.put("rachaActual", racha);
        response.put("pesoActual", pesoActual);
        response.put("membresia", membresia);
        response.put("entrenadores", entrenadorRepository.findAll());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/progreso")
    public ResponseEntity<?> obtenerProgreso(Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));
        Double pesoActual = dashboardMiembroService.obtenerUltimoPeso(auth.getName());
        return ResponseEntity.ok(Map.of("pesoActual", pesoActual));
    }

    @GetMapping("/membresia")
    public ResponseEntity<?> obtenerMembresia(Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));
        Membresia membresia = dashboardMiembroService.obtenerMembresiaActiva(auth.getName());
        return ResponseEntity.ok(Map.of("membresia", membresia != null ? membresia : "Sin membresia activa"));
    }

    @GetMapping("/entrenadores")
    public ResponseEntity<?> obtenerEntrenadores(Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));
        return ResponseEntity.ok(entrenadorRepository.findAll());
    }

    @PostMapping("/entrenadores/elegir")
    public ResponseEntity<?> elegirEntrenador(Authentication auth, @RequestBody Map<String, Long> payload) {
        if (auth == null) return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));
        Long entrenadorId = payload.get("entrenadorId");
        if (entrenadorId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "entrenadorId es obligatorio"));
        }

        Optional<Usuario> usuarioOpt = authService.buscarPorCorreo(auth.getName());
        Optional<Entrenador> entrenadorOpt = entrenadorRepository.findById(entrenadorId);

        if (usuarioOpt.isPresent() && entrenadorOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            List<Membresia> mems = membresiaRepository.findByUsuarioId(usuario.getId());
            boolean tieneAcceso = mems.stream().anyMatch(Membresia::tieneAcceso);
            if (!tieneAcceso) {
                return ResponseEntity.badRequest().body(Map.of("error", "El miembro no tiene una membresía activa ni está en período de gracia."));
            }

            usuario.setEntrenador(entrenadorOpt.get());
            authService.guardarUsuario(usuario);
            return ResponseEntity.ok(Map.of("mensaje", "Entrenador elegido exitosamente"));
        }

        return ResponseEntity.badRequest().body(Map.of("error", "Usuario o entrenador no encontrado"));
    }
}
