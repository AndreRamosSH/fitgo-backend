package FitGO.utp.edu.pe.controller;

import FitGO.utp.edu.pe.entity.DetalleEntrenamientoRealizado;
import FitGO.utp.edu.pe.entity.EntrenamientoRealizado;
import FitGO.utp.edu.pe.entity.Rutina;
import FitGO.utp.edu.pe.entity.Usuario;
import FitGO.utp.edu.pe.repository.DetalleEntrenamientoRealizadoRepository;
import FitGO.utp.edu.pe.repository.EntrenamientoRealizadoRepository;
import FitGO.utp.edu.pe.repository.RutinaRepository;
import FitGO.utp.edu.pe.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/miembro/entrenamientos")
@CrossOrigin(origins = "http://localhost:4200")
public class EntrenamientoRestController {

    private final EntrenamientoRealizadoRepository entrenamientoRepository;
    private final DetalleEntrenamientoRealizadoRepository detalleRepository;
    private final RutinaRepository rutinaRepository;
    private final UsuarioRepository usuarioRepository;

    public EntrenamientoRestController(EntrenamientoRealizadoRepository entrenamientoRepository,
                                       DetalleEntrenamientoRealizadoRepository detalleRepository,
                                       RutinaRepository rutinaRepository,
                                       UsuarioRepository usuarioRepository) {
        this.entrenamientoRepository = entrenamientoRepository;
        this.detalleRepository = detalleRepository;
        this.rutinaRepository = rutinaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    private Usuario obtenerUsuarioAutenticado(Authentication auth) {
        if (auth == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }
        return usuarioRepository.findByCorreo(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    @PostMapping("/registrar")
    @Transactional
    public ResponseEntity<?> registrarEntrenamiento(Authentication auth, @RequestBody Map<String, Object> payload) {
        Usuario usuario = obtenerUsuarioAutenticado(auth);

        String rutinaNombre = (String) payload.get("rutinaNombre");
        Integer duracionSegundos = Integer.valueOf(payload.get("duracionSegundos").toString());
        Integer ejerciciosCompletados = Integer.valueOf(payload.get("ejerciciosCompletados").toString());
        Integer totalSeriesCompletadas = Integer.valueOf(payload.get("totalSeriesCompletadas").toString());
        Double volumenTotalKg = Double.valueOf(payload.get("volumenTotalKg").toString());

        Rutina rutina = null;
        if (payload.containsKey("rutinaId") && payload.get("rutinaId") != null) {
            try {
                Long rutinaId = Long.valueOf(payload.get("rutinaId").toString());
                rutina = rutinaRepository.findById(rutinaId).orElse(null);
            } catch (NumberFormatException e) {
                // Rutina no numérica (por ejemplo un mock como 'r1'), ignorar
            }
        }

        EntrenamientoRealizado entrenamiento = new EntrenamientoRealizado();
        entrenamiento.setUsuario(usuario);
        entrenamiento.setRutina(rutina);
        entrenamiento.setRutinaNombre(rutinaNombre != null ? rutinaNombre : "Entrenamiento Libre");
        entrenamiento.setFecha(LocalDateTime.now());
        entrenamiento.setDuracionSegundos(duracionSegundos);
        entrenamiento.setEjerciciosCompletados(ejerciciosCompletados);
        entrenamiento.setTotalSeriesCompletadas(totalSeriesCompletadas);
        entrenamiento.setVolumenTotalKg(volumenTotalKg);

        EntrenamientoRealizado guardado = entrenamientoRepository.save(entrenamiento);

        // Registrar detalles
        List<Map<String, Object>> detallesPayload = (List<Map<String, Object>>) payload.get("detalles");
        if (detallesPayload != null) {
            for (Map<String, Object> dp : detallesPayload) {
                DetalleEntrenamientoRealizado der = new DetalleEntrenamientoRealizado();
                der.setEntrenamiento(guardado);
                der.setEjercicioId((String) dp.get("ejercicioId"));
                der.setEjercicioNombre((String) dp.get("ejercicioNombre"));
                der.setSerieNumero(Integer.valueOf(dp.get("serieNumero").toString()));
                der.setReps(Integer.valueOf(dp.get("reps").toString()));
                der.setPeso(Double.valueOf(dp.get("peso").toString()));

                detalleRepository.save(der);
            }
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "id", guardado.getId().toString(),
                "mensaje", "Entrenamiento registrado exitosamente"
        ));
    }

    @GetMapping("/historial")
    public ResponseEntity<?> obtenerHistorial(Authentication auth) {
        Usuario usuario = obtenerUsuarioAutenticado(auth);
        List<EntrenamientoRealizado> historial = entrenamientoRepository.findByUsuarioCorreoOrderByFechaDesc(usuario.getCorreo());

        List<Map<String, Object>> response = new ArrayList<>();
        for (EntrenamientoRealizado er : historial) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", er.getId().toString());
            map.put("rutinaId", er.getRutina() != null ? er.getRutina().getId().toString() : null);
            map.put("rutinaNombre", er.getRutinaNombre());
            map.put("fecha", er.getFecha().toString());
            map.put("duracionSegundos", er.getDuracionSegundos());
            map.put("ejerciciosCompletados", er.getEjerciciosCompletados());
            map.put("totalSeriesCompletadas", er.getTotalSeriesCompletadas());
            map.put("volumenTotalKg", er.getVolumenTotalKg());

            // Detalle (opcional por rendimiento si es larga la lista, pero para FitGo los devolvemos)
            List<DetalleEntrenamientoRealizado> detalles = detalleRepository.findByEntrenamientoId(er.getId());
            List<Map<String, Object>> dList = new ArrayList<>();
            for (DetalleEntrenamientoRealizado der : detalles) {
                Map<String, Object> dMap = new HashMap<>();
                dMap.put("ejercicioId", der.getEjercicioId());
                dMap.put("ejercicioNombre", der.getEjercicioNombre());
                dMap.put("serieNumero", der.getSerieNumero());
                dMap.put("reps", der.getReps());
                dMap.put("peso", der.getPeso());
                dList.add(dMap);
            }
            map.put("detalles", dList);
            response.add(map);
        }

        return ResponseEntity.ok(response);
    }
}
