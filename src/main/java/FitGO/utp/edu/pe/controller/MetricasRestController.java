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
@CrossOrigin(origins = {"http://localhost:4200", "https://gimnasiointegralfitgo.com", "https://www.gimnasiointegralfitgo.com"})
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
                    return ResponseEntity.ok(new MetricasDTO(p.getPeso(), p.getAltura(), imc));
                })
                .orElse(ResponseEntity.ok(new MetricasDTO(0.0, 0.0, 0.0)));
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

        progresoRepository.save(nuevoProgreso);
        return ResponseEntity.ok().body("{\"message\": \"Métricas actualizadas con éxito\"}");
    }
}
