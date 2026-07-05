package FitGO.utp.edu.pe.controller;

import FitGO.utp.edu.pe.dto.MembresiaRequest;
import FitGO.utp.edu.pe.dto.PlanRequest;
import FitGO.utp.edu.pe.dto.RegistroRequest;
import FitGO.utp.edu.pe.entity.Entrenador;
import FitGO.utp.edu.pe.entity.Membresia;
import FitGO.utp.edu.pe.entity.Plan;
import FitGO.utp.edu.pe.entity.Usuario;
import FitGO.utp.edu.pe.repository.EntrenadorRepository;
import FitGO.utp.edu.pe.service.AuthService;
import FitGO.utp.edu.pe.service.MembresiaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class AdminRestController {

    private final AuthService authService;
    private final MembresiaService membresiaService;
    private final EntrenadorRepository entrenadorRepository;

    public AdminRestController(AuthService authService,
                               MembresiaService membresiaService,
                               EntrenadorRepository entrenadorRepository) {
        this.authService = authService;
        this.membresiaService = membresiaService;
        this.entrenadorRepository = entrenadorRepository;
    }

    @GetMapping("/resumen")
    public ResponseEntity<?> obtenerResumen(Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));

        Map<String, Integer> resumenMiembros = membresiaService.obtenerResumenMiembros();
        int activosYPorVencer = resumenMiembros.getOrDefault("activos", 0) + resumenMiembros.getOrDefault("porVencer", 0);

        Map<String, Object> response = new HashMap<>();
        response.put("totalUsuarios", authService.listarUsuarios().size());
        response.put("miembrosActivos", activosYPorVencer);
        response.put("totalEntrenadores", entrenadorRepository.count());
        response.put("membresiasPorVencer", resumenMiembros.getOrDefault("porVencer", 0));
        response.put("asistenciasHoy", authService.contarAsistenciasHoy());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/usuarios/registro")
    public ResponseEntity<?> registrarUsuario(Authentication auth, @RequestBody RegistroRequest request) {
        if (auth == null) return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));

        try {
            authService.registrarUsuario(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("mensaje", "Usuario registrado exitosamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/usuarios")
    public ResponseEntity<?> listarUsuarios(Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));
        return ResponseEntity.ok(authService.listarUsuarios());
    }

    @GetMapping("/miembros")
    public ResponseEntity<?> listarMiembros(Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));

        List<Usuario> miembros = authService.listarMiembros();
        Map<String, Integer> resumen = membresiaService.obtenerResumenMiembros();

        Map<String, Object> response = new HashMap<>();
        response.put("miembros", miembros);
        response.put("resumen", resumen);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/miembros/{id}")
    public ResponseEntity<?> eliminarMiembro(Authentication auth, @PathVariable Long id) {
        if (auth == null) return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));
        authService.eliminarMiembro(id);
        return ResponseEntity.ok(Map.of("mensaje", "Miembro eliminado exitosamente"));
    }

    @GetMapping("/entrenadores")
    public ResponseEntity<?> listarEntrenadores(Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));

        List<Entrenador> entrenadores = entrenadorRepository.findAll();
        return ResponseEntity.ok(entrenadores);
    }

    @DeleteMapping("/entrenadores/{id}")
    public ResponseEntity<?> eliminarEntrenador(Authentication auth, @PathVariable Long id) {
        if (auth == null) return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));
        authService.eliminarEntrenador(id);
        return ResponseEntity.ok(Map.of("mensaje", "Entrenador eliminado exitosamente"));
    }


    @GetMapping("/membresias")
    public ResponseEntity<?> listarMembresiasYPlanes(Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));

        List<Plan> planes = membresiaService.listarPlanes();
        List<Membresia> membresias = membresiaService.listarMembresias();

        Map<String, Object> response = new HashMap<>();
        response.put("planes", planes);
        response.put("membresias", membresias);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/membresias/nuevo-plan")
    public ResponseEntity<?> crearPlan(Authentication auth, @RequestBody PlanRequest request) {
        if (auth == null) return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));

        try {
            membresiaService.guardarPlan(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("mensaje", "Plan creado exitosamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/membresias/plan/{id}")
    public ResponseEntity<?> buscarPlan(Authentication auth, @PathVariable Long id) {
        if (auth == null) return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));

        Optional<Plan> planOpt = membresiaService.buscarPlanPorId(id);
        if (planOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Plan no encontrado"));
        }
        return ResponseEntity.ok(planOpt.get());
    }

    @PutMapping("/membresias/plan/{id}")
    public ResponseEntity<?> actualizarPlan(Authentication auth, @PathVariable Long id, @RequestBody PlanRequest request) {
        if (auth == null) return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));

        try {
            membresiaService.actualizarPlan(id, request);
            return ResponseEntity.ok(Map.of("mensaje", "Plan actualizado exitosamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/membresias/plan/{id}")
    public ResponseEntity<?> eliminarPlan(Authentication auth, @PathVariable Long id) {
        if (auth == null) return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));
        try {
            membresiaService.eliminarPlan(id);
            return ResponseEntity.ok(Map.of("mensaje", "Plan eliminado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "No se pudo eliminar el plan. Verifique que no esté asignado a membresías activas."));
        }
    }

    @GetMapping("/membresias/asignar/elegibles")
    public ResponseEntity<?> listarElegibles(Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));
        List<Usuario> elegibles = membresiaService.obtenerUsuariosElegiblesParaMembresia();
        return ResponseEntity.ok(elegibles);
    }

    @PostMapping("/membresias/asignar")
    public ResponseEntity<?> asignarMembresia(Authentication auth, @RequestBody MembresiaRequest request) {
        if (auth == null) return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));

        try {
            membresiaService.asignarMembresia(request);
            return ResponseEntity.ok(Map.of("mensaje", "Membresía asignada exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "No se pudo asignar la membresía."));
        }
    }
}
