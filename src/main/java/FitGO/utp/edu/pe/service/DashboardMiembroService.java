package FitGO.utp.edu.pe.service;

import FitGO.utp.edu.pe.entity.EstadoMembresia;
import FitGO.utp.edu.pe.entity.Membresia;
import FitGO.utp.edu.pe.entity.Progreso;
import FitGO.utp.edu.pe.entity.Usuario;
import FitGO.utp.edu.pe.repository.MembresiaRepository;
import FitGO.utp.edu.pe.repository.ProgresoRepository;
import FitGO.utp.edu.pe.repository.UsuarioRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardMiembroService {

    private final ProgresoRepository progresoRepository;
    private final MembresiaRepository membresiaRepository;
    private final UsuarioRepository usuarioRepository;
    private final MembresiaService membresiaService;

    public DashboardMiembroService(ProgresoRepository progresoRepository,
                                   MembresiaRepository membresiaRepository,
                                   UsuarioRepository usuarioRepository,
                                   @Lazy MembresiaService membresiaService) {
        this.progresoRepository = progresoRepository;
        this.membresiaRepository = membresiaRepository;
        this.usuarioRepository = usuarioRepository;
        this.membresiaService = membresiaService;
    }

    public Double obtenerUltimoPeso(String correo) {
        Usuario usuario = usuarioRepository.findByCorreo(correo).orElse(null);
        if (usuario == null) {
            return 0.0;
        }
        return progresoRepository.findTopByUsuarioOrderByFechaRegistroDesc(usuario)
                .map(Progreso::getPeso)
                .orElse(0.0);
    }

    public Membresia obtenerMembresiaActiva(String correo) {
        Usuario usuario = usuarioRepository.findByCorreo(correo).orElse(null);
        if (usuario == null) {
            return null;
        }
        return membresiaRepository.findByUsuarioAndEstado(usuario, EstadoMembresia.ACTIVA).orElse(null);
    }

    public Membresia obtenerMembresiaReciente(String correo) {
        membresiaService.actualizarEstados();
        Usuario usuario = usuarioRepository.findByCorreo(correo).orElse(null);
        if (usuario == null) {
            return null;
        }
        List<Membresia> mems = membresiaRepository.findByUsuarioOrderByFechaFinDesc(usuario);
        return mems.isEmpty() ? null : mems.get(0);
    }
}
