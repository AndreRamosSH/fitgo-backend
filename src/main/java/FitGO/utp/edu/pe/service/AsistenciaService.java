package FitGO.utp.edu.pe.service;

import FitGO.utp.edu.pe.entity.Asistencia;
import FitGO.utp.edu.pe.entity.Usuario;
import FitGO.utp.edu.pe.repository.AsistenciaRepository;
import FitGO.utp.edu.pe.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AsistenciaService {

    private final AsistenciaRepository asistenciaRepository;
    private final UsuarioRepository usuarioRepository;

    public AsistenciaService(AsistenciaRepository asistenciaRepository, UsuarioRepository usuarioRepository) {
        this.asistenciaRepository = asistenciaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public int calcularRachaActual(String correo) {
        Usuario usuario = usuarioRepository.findByCorreo(correo).orElse(null);
        if (usuario == null) return 0;

        List<Asistencia> asistencias = asistenciaRepository.findByUsuarioOrderByFechaHoraDesc(usuario);
        if (asistencias.isEmpty()) return 0;

        List<LocalDate> fechas = asistencias.stream()
                .map(a -> a.getFechaHora().toLocalDate())
                .distinct()
                .collect(Collectors.toList());

        int racha = 0;
        LocalDate fechaEvaluacion = LocalDate.now();

        if (!fechas.get(0).equals(fechaEvaluacion) && !fechas.get(0).equals(fechaEvaluacion.minusDays(1))) {
            return 0; 
        }

        fechaEvaluacion = fechas.get(0); 

        for (LocalDate fechaAsistencia : fechas) {
            if (fechaAsistencia.equals(fechaEvaluacion)) {
                racha++;
                fechaEvaluacion = fechaEvaluacion.minusDays(1);
            } else {
                break; 
            }
        }
        return racha;
    }
}