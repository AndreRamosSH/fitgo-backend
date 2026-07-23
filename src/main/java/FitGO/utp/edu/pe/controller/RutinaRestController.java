package FitGO.utp.edu.pe.controller;

import FitGO.utp.edu.pe.dto.RutinaAsignarRequest;
import FitGO.utp.edu.pe.dto.RutinaEjercicioRequest;
import FitGO.utp.edu.pe.dto.RutinaRequest;
import FitGO.utp.edu.pe.entity.Rol;
import FitGO.utp.edu.pe.entity.Rutina;
import FitGO.utp.edu.pe.entity.RutinaEjercicio;
import FitGO.utp.edu.pe.entity.Usuario;
import FitGO.utp.edu.pe.entity.EntrenamientoRealizado;
import FitGO.utp.edu.pe.repository.RutinaEjercicioRepository;
import FitGO.utp.edu.pe.repository.RutinaRepository;
import FitGO.utp.edu.pe.repository.UsuarioRepository;
import FitGO.utp.edu.pe.repository.EntrenamientoRealizadoRepository;
import FitGO.utp.edu.pe.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/api/rutinas")
@CrossOrigin(origins = "http://localhost:4200")
public class RutinaRestController {

    private final RutinaRepository rutinaRepository;
    private final RutinaEjercicioRepository rutinaEjercicioRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuthService authService;
    private final EntrenamientoRealizadoRepository entrenamientoRealizadoRepository;

    public RutinaRestController(RutinaRepository rutinaRepository,
                                  RutinaEjercicioRepository rutinaEjercicioRepository,
                                  UsuarioRepository usuarioRepository,
                                  AuthService authService,
                                  EntrenamientoRealizadoRepository entrenamientoRealizadoRepository) {
        this.rutinaRepository = rutinaRepository;
        this.rutinaEjercicioRepository = rutinaEjercicioRepository;
        this.usuarioRepository = usuarioRepository;
        this.authService = authService;
        this.entrenamientoRealizadoRepository = entrenamientoRealizadoRepository;
    }

    private Usuario obtenerUsuarioAutenticado(Authentication auth) {
        if (auth == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }
        return usuarioRepository.findByCorreo(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    @GetMapping
    public ResponseEntity<?> listarRutinas(Authentication auth) {
        Usuario usuario = obtenerUsuarioAutenticado(auth);
        List<Map<String, Object>> response = new ArrayList<>();

        if (usuario.getRol() == Rol.ENTRENADOR) {
            // El entrenador ve sus rutinas plantilla creadas por él
            List<Rutina> plantillas = rutinaRepository.findByCreadorId(usuario.getId()).stream()
                    .filter(r -> r.getMiembro().getId().equals(usuario.getId()) || "PROPIA".equalsIgnoreCase(r.getTipo()))
                    .toList();

            // Buscar también todas las asignaciones existentes hechas por este entrenador
            List<Rutina> asignadas = rutinaRepository.findByCreadorIdAndTipo(usuario.getId(), "ASIGNADA");

            for (Rutina r : plantillas) {
                // IDs de miembros que tienen esta rutina asignada
                List<String> miembrosAsignados = asignadas.stream()
                        .filter(a -> a.getNombre().equalsIgnoreCase(r.getNombre()))
                        .map(a -> a.getMiembro().getId().toString())
                        .toList();

                response.add(mapearRutinaAResponse(r, usuario, miembrosAsignados));
            }
            return ResponseEntity.ok(response);
        }

        // Para miembros: ver las rutinas asignadas a él o creadas por él
        List<Rutina> listaRutinas = rutinaRepository.findByMiembroId(usuario.getId());

        for (Rutina r : listaRutinas) {
            response.add(mapearRutinaAResponse(r, usuario, null));
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> crearRutina(Authentication auth, @RequestBody RutinaRequest payload) {
        Usuario creador = obtenerUsuarioAutenticado(auth);

        if (payload.getNombre() == null || payload.getNombre().trim().isEmpty() || 
            payload.getDias() == null || payload.getDias().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Nombre y días son campos obligatorios"));
        }

        Rutina rutina = new Rutina();
        rutina.setNombre(payload.getNombre());
        rutina.setDias(payload.getDias());
        rutina.setTipo("PROPIA");
        rutina.setCreador(creador);
        rutina.setMiembro(creador);

        Rutina rutinaGuardada = rutinaRepository.save(rutina);

        // Guardar ejercicios
        if (payload.getEjercicios() != null) {
            int orden = 0;
            for (RutinaEjercicioRequest ep : payload.getEjercicios()) {
                RutinaEjercicio re = new RutinaEjercicio();
                re.setRutina(rutinaGuardada);
                re.setEjercicioId(ep.getEjercicioId());
                re.setNombre(ep.getNombre());
                re.setOrden(orden++);
                re.setSeries(ep.getSeries());
                re.setReps(ep.getReps());
                re.setPeso(ep.getPeso());
                re.setDescanso(ep.getDescanso());

                rutinaEjercicioRepository.save(re);
            }
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "id", rutinaGuardada.getId().toString(),
                "mensaje", "Rutina creada exitosamente"
        ));
    }

    @PostMapping("/{id}/asignar")
    @Transactional
    public ResponseEntity<?> asignarRutina(Authentication auth, @PathVariable Long id, @RequestBody RutinaAsignarRequest payload) {
        Usuario entrenador = obtenerUsuarioAutenticado(auth);
        if (entrenador.getRol() != Rol.ENTRENADOR) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Solo los entrenadores pueden asignar rutinas"));
        }

        Rutina plantilla = rutinaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rutina plantilla no encontrada"));

        Set<Long> miembroIds = new HashSet<>();
        if (payload.getMiembroIds() != null) {
            miembroIds.addAll(payload.getMiembroIds());
        }

        // Miembros a cargo de este entrenador
        List<Usuario> misMiembros = authService.listarMiembrosPorEntrenador(entrenador.getCorreo());
        List<RutinaEjercicio> ejerciciosPlantilla = rutinaEjercicioRepository.findByRutinaIdOrderByOrdenAsc(plantilla.getId());

        for (Usuario m : misMiembros) {
            boolean debeEstarAsignado = miembroIds.contains(m.getId());
            Optional<Rutina> rutinaAsignadaOpt = rutinaRepository.findByCreadorIdAndMiembroIdAndNombre(entrenador.getId(), m.getId(), plantilla.getNombre());

            if (debeEstarAsignado) {
                Rutina rAsignada;
                if (rutinaAsignadaOpt.isPresent()) {
                    rAsignada = rutinaAsignadaOpt.get();
                    rAsignada.setDias(plantilla.getDias());
                    rutinaRepository.save(rAsignada);
                    rutinaEjercicioRepository.deleteByRutinaId(rAsignada.getId());
                } else {
                    rAsignada = new Rutina();
                    rAsignada.setNombre(plantilla.getNombre());
                    rAsignada.setDias(plantilla.getDias());
                    rAsignada.setTipo("ASIGNADA");
                    rAsignada.setCreador(entrenador);
                    rAsignada.setMiembro(m);
                    rAsignada = rutinaRepository.save(rAsignada);
                }

                // Copiar ejercicios
                int orden = 0;
                for (RutinaEjercicio ep : ejerciciosPlantilla) {
                    RutinaEjercicio re = new RutinaEjercicio();
                    re.setRutina(rAsignada);
                    re.setEjercicioId(ep.getEjercicioId());
                    re.setNombre(ep.getNombre());
                    re.setOrden(orden++);
                    re.setSeries(ep.getSeries());
                    re.setReps(ep.getReps());
                    re.setPeso(ep.getPeso());
                    re.setDescanso(ep.getDescanso());
                    rutinaEjercicioRepository.save(re);
                }
            } else {
                // Desasignar si existía
                if (rutinaAsignadaOpt.isPresent()) {
                    rutinaEjercicioRepository.deleteByRutinaId(rutinaAsignadaOpt.get().getId());
                    rutinaRepository.delete(rutinaAsignadaOpt.get());
                }
            }
        }

        return ResponseEntity.ok(Map.of("mensaje", "Asignaciones actualizadas correctamente"));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> editarRutina(Authentication auth, @PathVariable Long id, @RequestBody RutinaRequest payload) {
        Usuario usuario = obtenerUsuarioAutenticado(auth);
        Rutina rutina = rutinaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rutina no encontrada"));

        if (!rutina.getCreador().getId().equals(usuario.getId()) && !rutina.getMiembro().getId().equals(usuario.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "No tienes permisos para modificar esta rutina"));
        }

        if (payload.getNombre() == null || payload.getNombre().trim().isEmpty() || 
            payload.getDias() == null || payload.getDias().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Nombre y días son obligatorios"));
        }

        rutina.setNombre(payload.getNombre());
        rutina.setDias(payload.getDias());
        rutinaRepository.save(rutina);

        rutinaEjercicioRepository.deleteByRutinaId(rutina.getId());

        if (payload.getEjercicios() != null) {
            int orden = 0;
            for (RutinaEjercicioRequest ep : payload.getEjercicios()) {
                RutinaEjercicio re = new RutinaEjercicio();
                re.setRutina(rutina);
                re.setEjercicioId(ep.getEjercicioId());
                re.setNombre(ep.getNombre());
                re.setOrden(orden++);
                re.setSeries(ep.getSeries());
                re.setReps(ep.getReps());
                re.setPeso(ep.getPeso());
                re.setDescanso(ep.getDescanso());

                rutinaEjercicioRepository.save(re);
            }
        }

        return ResponseEntity.ok(Map.of("mensaje", "Rutina actualizada exitosamente"));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> eliminarRutina(Authentication auth, @PathVariable Long id) {
        Usuario usuario = obtenerUsuarioAutenticado(auth);
        Rutina rutina = rutinaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rutina no encontrada"));

        if (!rutina.getCreador().getId().equals(usuario.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "No tienes permisos para eliminar esta rutina"));
        }

        // Desvincular entrenamientos completados para evitar fallos por clave foránea
        List<EntrenamientoRealizado> entrenamientosAsociados = entrenamientoRealizadoRepository.findByRutinaId(rutina.getId());
        for (EntrenamientoRealizado er : entrenamientosAsociados) {
            er.setRutina(null);
            entrenamientoRealizadoRepository.save(er);
        }

        rutinaEjercicioRepository.deleteByRutinaId(rutina.getId());
        rutinaRepository.delete(rutina);

        return ResponseEntity.ok(Map.of("mensaje", "Rutina cantidad eliminada exitosamente"));
    }

    // --- Métodos Auxiliares de Mapeo (Evitan Código Duplicado) ---

    private Map<String, Object> mapearRutinaAResponse(Rutina r, Usuario usuarioAutenticado, List<String> miembrosAsignados) {
        Map<String, Object> rMap = new HashMap<>();
        rMap.put("id", r.getId().toString());
        rMap.put("nombre", r.getNombre());
        rMap.put("dias", r.getDias());
        rMap.put("tipo", r.getTipo());

        String autor = "Sistema";
        if (r.getCreador() != null) {
            if (r.getCreador().getId().equals(usuarioAutenticado.getId())) {
                autor = "Yo";
            } else {
                autor = r.getCreador().getNombre() + " " + r.getCreador().getApellido();
            }
        }
        rMap.put("autor", autor);

        List<RutinaEjercicio> ejercicios = rutinaEjercicioRepository.findByRutinaIdOrderByOrdenAsc(r.getId());
        rMap.put("ejerciciosCount", ejercicios.size());
        rMap.put("ejercicios", mapearEjercicios(ejercicios));

        if (miembrosAsignados != null) {
            rMap.put("asignados", miembrosAsignados);
        }

        return rMap;
    }

    private List<Map<String, Object>> mapearEjercicios(List<RutinaEjercicio> ejercicios) {
        List<Map<String, Object>> eList = new ArrayList<>();
        for (RutinaEjercicio re : ejercicios) {
            Map<String, Object> eMap = new HashMap<>();
            eMap.put("id", re.getId().toString());
            eMap.put("ejercicioId", re.getEjercicioId());
            eMap.put("nombre", re.getNombre());
            eMap.put("series", re.getSeries());
            eMap.put("reps", re.getReps());
            eMap.put("peso", re.getPeso());
            eMap.put("descanso", re.getDescanso());
            eList.add(eMap);
        }
        return eList;
    }
}
