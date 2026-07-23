package FitGO.utp.edu.pe.controller;

import FitGO.utp.edu.pe.dto.MetricasDTO;
import FitGO.utp.edu.pe.entity.Progreso;
import FitGO.utp.edu.pe.entity.Usuario;
import FitGO.utp.edu.pe.repository.ProgresoRepository;
import FitGO.utp.edu.pe.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/miembro/metricas")
@CrossOrigin(origins = "http://localhost:4200") 
public class MetricasRestController {

    private final UsuarioRepository usuarioRepository;
    private final ProgresoRepository progresoRepository;

    public MetricasRestController(UsuarioRepository usuarioRepository, ProgresoRepository progresoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.progresoRepository = progresoRepository;
    }

    @GetMapping("/ultimo")
    public ResponseEntity<MetricasDTO> obtenerUltimasMetricas(Authentication auth) {
        String correo = auth.getName();
        Usuario usuario = usuarioRepository.findByCorreo(correo).orElse(null);

        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }

        return progresoRepository.findTopByUsuarioOrderByFechaRegistroDesc(usuario)
                .map(p -> {
                    Double imc = (p.getAltura() != null && p.getAltura() > 0)
                            ? p.getPeso() / (p.getAltura() * p.getAltura()) : 0.0;
                    return ResponseEntity.ok(new MetricasDTO(p.getPeso(), p.getAltura(), imc, usuario.getFechaNacimiento(), usuario.getSexo(), usuario.getPesoObjetivo(), usuario.getGrasaObjetivo()));
                })
                .orElse(ResponseEntity.ok(new MetricasDTO(0.0, 0.0, 0.0, usuario.getFechaNacimiento(), usuario.getSexo(), usuario.getPesoObjetivo(), usuario.getGrasaObjetivo())));
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarMetricas(Authentication auth, @Valid @RequestBody MetricasDTO dto) {
        String correo = auth.getName();
        Usuario usuario = usuarioRepository.findByCorreo(correo).orElse(null);

        if (usuario == null) {
            return ResponseEntity.badRequest().body("Usuario no encontrado");
        }

        Progreso nuevoProgreso = new Progreso();
        nuevoProgreso.setUsuario(usuario);
        nuevoProgreso.setPeso(dto.getPeso());
        nuevoProgreso.setAltura(dto.getAltura());
        nuevoProgreso.setFechaRegistro(LocalDateTime.now());

        if (dto.getFechaNacimiento() != null) {
            usuario.setFechaNacimiento(dto.getFechaNacimiento());
        }
        if (dto.getSexo() != null) {
            usuario.setSexo(dto.getSexo());
        }
        if (dto.getPesoObjetivo() != null) {
            usuario.setPesoObjetivo(dto.getPesoObjetivo());
        }
        if (dto.getGrasaObjetivo() != null) {
            usuario.setGrasaObjetivo(dto.getGrasaObjetivo());
        }
        usuarioRepository.save(usuario);

        progresoRepository.save(nuevoProgreso);
        return ResponseEntity.ok().body("{\"message\": \"Métricas actualizadas con éxito\"}");
    }

    @GetMapping("/historial")
    public ResponseEntity<?> obtenerHistorialMetricas(Authentication auth) {
        String correo = auth.getName();
        Usuario usuario = usuarioRepository.findByCorreo(correo).orElse(null);

        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }

        java.util.List<Progreso> historial = progresoRepository.findByUsuarioOrderByFechaRegistroAsc(usuario);
        java.util.List<java.util.Map<String, Object>> res = new java.util.ArrayList<>();

        for (Progreso p : historial) {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("fechaRegistro", p.getFechaRegistro());
            map.put("peso", p.getPeso());
            map.put("altura", p.getAltura());
            Double imc = (p.getAltura() != null && p.getAltura() > 0)
                    ? p.getPeso() / (p.getAltura() * p.getAltura()) : 0.0;
            map.put("imc", imc);
            res.add(map);
        }

        return ResponseEntity.ok(res);
    }

    @PostMapping("/metas")
    public ResponseEntity<?> guardarMetas(Authentication auth, @RequestBody java.util.Map<String, Double> payload) {
        String correo = auth.getName();
        Usuario usuario = usuarioRepository.findByCorreo(correo).orElse(null);

        if (usuario == null) {
            return ResponseEntity.badRequest().body("Usuario no encontrado");
        }

        if (payload.containsKey("pesoObjetivo")) {
            usuario.setPesoObjetivo(payload.get("pesoObjetivo"));
        }
        if (payload.containsKey("grasaObjetivo")) {
            usuario.setGrasaObjetivo(payload.get("grasaObjetivo"));
        }
        usuarioRepository.save(usuario);

        return ResponseEntity.ok().body("{\"message\": \"Metas actualizadas con éxito\"}");
    }
}
