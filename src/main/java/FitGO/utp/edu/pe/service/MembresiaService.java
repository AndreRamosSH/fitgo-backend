package FitGO.utp.edu.pe.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import FitGO.utp.edu.pe.dto.MembresiaRequest;
import FitGO.utp.edu.pe.dto.PlanRequest;
import FitGO.utp.edu.pe.entity.EstadoMembresia;
import FitGO.utp.edu.pe.entity.Membresia;
import FitGO.utp.edu.pe.entity.Plan;
import FitGO.utp.edu.pe.entity.Usuario;
import FitGO.utp.edu.pe.repository.MembresiaRepository;
import FitGO.utp.edu.pe.repository.PlanRepository;
import FitGO.utp.edu.pe.repository.UsuarioRepository;

import java.util.Map;
import java.util.HashMap;

@Service
@Transactional
public class MembresiaService {

    private final PlanRepository planRepository;
    private final MembresiaRepository membresiaRepository;
    private final UsuarioRepository usuarioRepository;

    public MembresiaService(PlanRepository planRepository,
                            MembresiaRepository membresiaRepository,
                            UsuarioRepository usuarioRepository) {
        this.planRepository = planRepository;
        this.membresiaRepository = membresiaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Plan> listarPlanes() {
        return planRepository.findAll();
    }

    public Optional<Plan> buscarPlanPorId(Long id) {
        return planRepository.findById(id);
    }

    private void validarPlanRequest(PlanRequest request) {
        if (request.getNombre() == null || request.getNombre().trim().isEmpty() ||
            !request.getNombre().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$")) {
            throw new IllegalArgumentException("El nombre del plan no es valido.");
        }
        if (request.getPrecio() == null || request.getPrecio().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a cero.");
        }
        if (request.getDuracionDias() == null || request.getDuracionDias() <= 0) {
            throw new IllegalArgumentException("La duracion debe ser mayor a cero.");
        }
    }

    public void guardarPlan(PlanRequest request) {
        validarPlanRequest(request);

        Plan plan = new Plan(
                request.getNombre(),
                request.getPrecio(),
                request.getDuracionDias(),
                request.getDescripcion()
        );
        planRepository.save(plan);
    }

    public void actualizarPlan(Long id, PlanRequest request) {
        validarPlanRequest(request);

        Optional<Plan> planOpt = planRepository.findById(id);
        if (planOpt.isEmpty()) {
            return;
        }
        Plan plan = planOpt.get();
        plan.setNombre(request.getNombre());
        plan.setPrecio(request.getPrecio());
        plan.setDuracionDias(request.getDuracionDias());
        plan.setDescripcion(request.getDescripcion());
        planRepository.save(plan);
    }

    public void eliminarPlan(Long id) {
        planRepository.deleteById(id);
    }

    public void asignarMembresia(MembresiaRequest request) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(request.getUsuarioId());
        Optional<Plan> planOpt = planRepository.findById(request.getPlanId());

        if (usuarioOpt.isEmpty() || planOpt.isEmpty()) {
            return;
        }

        Usuario usuario = usuarioOpt.get();

        List<Membresia> membresiasUsuario = membresiaRepository.findByUsuarioId(usuario.getId());
        for (Membresia m : membresiasUsuario) {
            if (m.getEstado() == EstadoMembresia.ACTIVA || m.getEstado() == EstadoMembresia.POR_VENCER) {
                return;
            }
        }

        Plan plan = planOpt.get();
        LocalDate hoy = LocalDate.now();
        LocalDate fechaFin = hoy.plusDays(plan.getDuracionDias());

        Membresia membresia = new Membresia();
        membresia.setUsuario(usuario);
        membresia.setPlan(plan);
        membresia.setFechaInicio(hoy);
        membresia.setFechaFin(fechaFin);
        membresia.setEstado(EstadoMembresia.ACTIVA);

        membresiaRepository.save(membresia);
    }

    public List<Usuario> obtenerUsuariosElegiblesParaMembresia() {
        actualizarEstados();
        return usuarioRepository.findMiembrosSinMembresiaActiva();
    }

    public Map<String, Integer> obtenerResumenMiembros() {
        actualizarEstados();
        Map<String, Integer> resumen = new HashMap<>();
        long total = usuarioRepository.findByRol(FitGO.utp.edu.pe.entity.Rol.MIEMBRO).size();
        long activos = membresiaRepository.countByEstado(EstadoMembresia.ACTIVA);
        long porVencer = membresiaRepository.countByEstado(EstadoMembresia.POR_VENCER);
        long sinMembresia = total - (activos + porVencer);

        resumen.put("total", (int) total);
        resumen.put("activos", (int) activos);
        resumen.put("porVencer", (int) porVencer);
        resumen.put("sinMembresia", (int) sinMembresia);
        return resumen;
    }

    public List<Membresia> listarMembresias() {
        actualizarEstados();
        return membresiaRepository.findAll();
    }

    public void actualizarEstados() {
        LocalDate hoy = LocalDate.now();
        LocalDate limite = hoy.plusDays(3);
        membresiaRepository.actualizarVencidas(hoy);
        membresiaRepository.actualizarPorVencer(hoy, limite);
        membresiaRepository.actualizarActivas(limite);
        
        LocalDate limiteGracia = hoy.minusDays(5);
        usuarioRepository.desasignarEntrenadoresDeMiembrosExcedidosDeGracia(limiteGracia);
    }
}
