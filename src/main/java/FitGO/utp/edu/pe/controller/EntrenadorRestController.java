package FitGO.utp.edu.pe.controller;

import FitGO.utp.edu.pe.dto.AsistenciaRequest;
import FitGO.utp.edu.pe.entity.*;
import FitGO.utp.edu.pe.repository.*;
import FitGO.utp.edu.pe.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/entrenador")
public class EntrenadorRestController {

    private final AuthService authService;
    private final EntrenadorRepository entrenadorRepository;
    private final AsistenciaRepository asistenciaRepository;
    private final RutinaRepository rutinaRepository;
    private final RutinaEjercicioRepository rutinaEjercicioRepository;
    private final MembresiaRepository membresiaRepository;
    private final ProgresoRepository progresoRepository;
    private final UsuarioRepository usuarioRepository;

    public EntrenadorRestController(AuthService authService,
                                   EntrenadorRepository entrenadorRepository,
                                   AsistenciaRepository asistenciaRepository,
                                   RutinaRepository rutinaRepository,
                                   RutinaEjercicioRepository rutinaEjercicioRepository,
                                   MembresiaRepository membresiaRepository,
                                   ProgresoRepository progresoRepository,
                                   UsuarioRepository usuarioRepository) {
        this.authService = authService;
        this.entrenadorRepository = entrenadorRepository;
        this.asistenciaRepository = asistenciaRepository;
        this.rutinaRepository = rutinaRepository;
        this.rutinaEjercicioRepository = rutinaEjercicioRepository;
        this.membresiaRepository = membresiaRepository;
        this.progresoRepository = progresoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    private Usuario obtenerUsuarioAutenticado(Authentication auth) {
        if (auth == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }
        return authService.buscarPorCorreo(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    private String obtenerIniciales(String nombre, String apellido) {
        String n = (nombre != null && !nombre.isEmpty()) ? nombre.substring(0, 1).toUpperCase() : "U";
        String a = (apellido != null && !apellido.isEmpty()) ? apellido.substring(0, 1).toUpperCase() : "";
        return n + a;
    }

    @GetMapping("/resumen")
    public ResponseEntity<?> obtenerResumen(Authentication auth) {
        Usuario usuario = obtenerUsuarioAutenticado(auth);
        List<Usuario> miembros = authService.listarMiembrosPorEntrenador(auth.getName());

        LocalDateTime inicioHoy = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime finHoy = LocalDateTime.now().with(LocalTime.MAX);
        List<Asistencia> asistenciasHoy = asistenciaRepository.findByFechaHoraBetween(inicioHoy, finHoy);

        Map<Long, String> horasIngresoMiembros = new HashMap<>();
        for (Asistencia a : asistenciasHoy) {
            if (a.getUsuario() != null) {
                horasIngresoMiembros.put(a.getUsuario().getId(), a.getFechaHora().format(DateTimeFormatter.ofPattern("HH:mm")));
            }
        }

        List<Map<String, Object>> miembrosAsistenciaHoy = new ArrayList<>();
        int asistieronCount = 0;

        for (Usuario m : miembros) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", m.getId());
            map.put("nombre", m.getNombre());
            map.put("apellido", m.getApellido());
            map.put("correo", m.getCorreo());
            map.put("initials", obtenerIniciales(m.getNombre(), m.getApellido()));

            boolean asistio = horasIngresoMiembros.containsKey(m.getId());
            map.put("asistioHoy", asistio);
            map.put("horaIngreso", asistio ? horasIngresoMiembros.get(m.getId()) : null);

            if (asistio) asistieronCount++;
            miembrosAsistenciaHoy.add(map);
        }

        List<Rutina> rutinasCreadas = rutinaRepository.findByCreadorId(usuario.getId());
        long rutinasAsignadasCount = rutinasCreadas.stream().filter(r -> r.getMiembro() != null).count();

        Map<String, Object> response = new HashMap<>();
        response.put("totalMiembros", miembros.size());
        response.put("asistieronHoyCount", asistieronCount);
        response.put("totalRutinasCreadas", rutinasCreadas.size());
        response.put("rutinasAsignadasCount", rutinasAsignadasCount);
        response.put("miembros", miembrosAsistenciaHoy);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/miembros")
    public ResponseEntity<?> obtenerMiembros(Authentication auth) {
        Usuario usuario = obtenerUsuarioAutenticado(auth);
        List<Usuario> miembros = authService.listarMiembrosPorEntrenador(auth.getName());
        LocalDateTime inicioMes = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime finMes = LocalDate.now().atTime(LocalTime.MAX);

        List<Map<String, Object>> listaEnriquecida = new ArrayList<>();

        for (Usuario m : miembros) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", m.getId());
            map.put("nombre", m.getNombre());
            map.put("apellido", m.getApellido());
            map.put("correo", m.getCorreo());
            map.put("initials", obtenerIniciales(m.getNombre(), m.getApellido()));

            // Membresía
            List<Membresia> mems = membresiaRepository.findByUsuarioOrderByFechaFinDesc(m);
            if (!mems.isEmpty()) {
                Membresia mem = mems.get(0);
                map.put("estadoMembresia", mem.getEstado() != null ? mem.getEstado().name() : "ACTIVA");
                String fechaFinStr = mem.getFechaFin() != null ? mem.getFechaFin().format(DateTimeFormatter.ofPattern("d MMM")) : "N/A";
                map.put("membresiaTexto", "Vence " + fechaFinStr);
            } else {
                map.put("estadoMembresia", "ACTIVA");
                map.put("membresiaTexto", "Sin membresía");
            }

            // Rutina
            List<Rutina> rutinas = rutinaRepository.findByMiembroId(m.getId());
            if (!rutinas.isEmpty()) {
                map.put("rutinaAsignada", rutinas.get(0).getNombre());
            } else {
                map.put("rutinaAsignada", "Sin asignar");
            }

            // Asistencias este mes
            List<Asistencia> asistencias = asistenciaRepository.findByUsuarioIdAndFechaHoraBetween(m.getId(), inicioMes, finMes);
            map.put("asistenciasEsteMes", asistencias.size());

            // Último peso
            Optional<Progreso> progOpt = progresoRepository.findTopByUsuarioOrderByFechaRegistroDesc(m);
            if (progOpt.isPresent() && progOpt.get().getPeso() != null) {
                map.put("ultimoPeso", Math.round(progOpt.get().getPeso()));
            } else {
                map.put("ultimoPeso", 70);
            }

            listaEnriquecida.add(map);
        }

        return ResponseEntity.ok(Map.of("miembros", listaEnriquecida, "totalMiembros", miembros.size()));
    }

    @GetMapping("/miembros/{id}/detalle")
    public ResponseEntity<?> obtenerDetalleMiembro(Authentication auth, @PathVariable Long id) {
        Usuario usuarioEntrenador = obtenerUsuarioAutenticado(auth);
        Usuario miembro = usuarioRepository.findById(id).orElse(null);

        if (miembro == null) {
            return ResponseEntity.notFound().build();
        }

        LocalDate hoy = LocalDate.now();
        YearMonth mesActual = YearMonth.from(hoy);
        LocalDateTime inicioMes = mesActual.atDay(1).atStartOfDay();
        LocalDateTime finMes = mesActual.atEndOfMonth().atTime(LocalTime.MAX);

        // Membresía
        List<Membresia> mems = membresiaRepository.findByUsuarioOrderByFechaFinDesc(miembro);
        String planNombre = "Plan Básico";
        String estadoMembresia = "ACTIVO";
        String venceTexto = "vence " + hoy.plusMonths(1).format(DateTimeFormatter.ofPattern("d MMM"));

        if (!mems.isEmpty()) {
            Membresia mem = mems.get(0);
            if (mem.getPlan() != null) planNombre = mem.getPlan().getNombre();
            if (mem.getEstado() != null) estadoMembresia = mem.getEstado().name();
            if (mem.getFechaFin() != null) venceTexto = "vence " + mem.getFechaFin().format(DateTimeFormatter.ofPattern("d MMM"));
        }

        // Asistencias
        List<Asistencia> asistencias = asistenciaRepository.findByUsuarioIdAndFechaHoraBetween(miembro.getId(), inicioMes, finMes);
        Set<Integer> diasAsistidos = asistencias.stream()
                .map(a -> a.getFechaHora().getDayOfMonth())
                .collect(Collectors.toSet());

        // Grid del calendario de asistencias del mes
        List<Map<String, Object>> calendarioAsistencia = new ArrayList<>();
        int diasEnMes = mesActual.lengthOfMonth();
        for (int i = 1; i <= diasEnMes; i++) {
            Map<String, Object> d = new HashMap<>();
            d.put("dia", i);
            d.put("asistio", diasAsistidos.contains(i));
            calendarioAsistencia.add(d);
        }

        // Entrenos por semana
        int[] entrenosPorSemana = new int[4];
        for (Asistencia a : asistencias) {
            int dia = a.getFechaHora().getDayOfMonth();
            int semanaIdx = Math.min(3, (dia - 1) / 7);
            entrenosPorSemana[semanaIdx]++;
        }

        // Métricas físicas
        List<Progreso> historialProgreso = progresoRepository.findByUsuarioOrderByFechaRegistroAsc(miembro);
        double pesoActual = 70.0;
        double pesoInicial = 70.0;
        double imc = 22.0;

        if (!historialProgreso.isEmpty()) {
            pesoInicial = historialProgreso.get(0).getPeso() != null ? historialProgreso.get(0).getPeso() : 70.0;
            Progreso ultimo = historialProgreso.get(historialProgreso.size() - 1);
            if (ultimo.getPeso() != null) pesoActual = ultimo.getPeso();

            if (ultimo.getAltura() != null && ultimo.getAltura() > 0) {
                imc = pesoActual / (ultimo.getAltura() * ultimo.getAltura());
            }
        }

        double cambioPeso = pesoActual - pesoInicial;
        String clasificacionImc = "Normal";
        if (imc < 18.5) clasificacionImc = "Bajo peso";
        else if (imc >= 25 && imc < 30) clasificacionImc = "Sobrepeso";
        else if (imc >= 30) clasificacionImc = "Obesidad";

        // Rutina asignada
        List<Rutina> rutinas = rutinaRepository.findByMiembroId(miembro.getId());
        Map<String, Object> rutinaInfo = new HashMap<>();
        if (!rutinas.isEmpty()) {
            Rutina r = rutinas.get(0);
            rutinaInfo.put("id", r.getId());
            rutinaInfo.put("nombre", r.getNombre());
            rutinaInfo.put("dias", r.getDias() != null ? r.getDias() : "Lun · Mié · Vie");
            int countEjer = rutinaEjercicioRepository.findByRutinaIdOrderByOrdenAsc(r.getId()).size();
            rutinaInfo.put("ejerciciosCount", countEjer);
            rutinaInfo.put("estado", "Activa");
        } else {
            rutinaInfo.put("id", null);
            rutinaInfo.put("nombre", "Sin rutina asignada");
            rutinaInfo.put("dias", "N/A");
            rutinaInfo.put("ejerciciosCount", 0);
            rutinaInfo.put("estado", "Sin asignar");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("id", miembro.getId());
        response.put("nombre", miembro.getNombre());
        response.put("apellido", miembro.getApellido());
        response.put("correo", miembro.getCorreo());
        response.put("initials", obtenerIniciales(miembro.getNombre(), miembro.getApellido()));

        response.put("planNombre", planNombre);
        response.put("estadoMembresia", estadoMembresia);
        response.put("venceTexto", venceTexto);

        response.put("asistenciasEsteMes", asistencias.size());
        response.put("pesoActual", Math.round(pesoActual * 10.0) / 10.0);
        response.put("cambioPeso", Math.round(cambioPeso * 10.0) / 10.0);
        response.put("imcActual", Math.round(imc * 10.0) / 10.0);
        response.put("imcClasificacion", clasificacionImc);

        response.put("calendarioAsistencia", calendarioAsistencia);
        response.put("entrenosPorSemana", entrenosPorSemana);

        Map<String, Object> metricasFisicas = new HashMap<>();
        metricasFisicas.put("peso", Math.round(pesoActual * 10.0) / 10.0);
        metricasFisicas.put("cambioPeso", Math.round(cambioPeso * 10.0) / 10.0);
        metricasFisicas.put("masaMuscular", 36.8);
        metricasFisicas.put("cambioMasaMuscular", 2.1);
        metricasFisicas.put("grasaCorporal", 18.2);
        metricasFisicas.put("cambioGrasaCorporal", -3.4);
        metricasFisicas.put("imc", Math.round(imc * 10.0) / 10.0);
        metricasFisicas.put("imcClasificacion", clasificacionImc);
        response.put("metricasFisicas", metricasFisicas);

        response.put("rutinaAsignada", rutinaInfo);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/horario")
    public ResponseEntity<?> obtenerHorario(Authentication auth) {
        Usuario usuario = obtenerUsuarioAutenticado(auth);
        Entrenador entrenador = entrenadorRepository.findByUsuarioId(usuario.getId());
        if (entrenador == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Entrenador no encontrado"));
        }

        return ResponseEntity.ok(entrenador);
    }

    @GetMapping("/perfil")
    public ResponseEntity<?> obtenerPerfil(Authentication auth) {
        Usuario usuario = obtenerUsuarioAutenticado(auth);
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
    public ResponseEntity<?> registrarAsistencia(Authentication auth, @Valid @RequestBody AsistenciaRequest request) {
        if (auth == null) return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));

        if (request.getCorreo() == null || request.getCorreo().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El correo del miembro es obligatorio"));
        }

        authService.registrarAsistencia(request);
        return ResponseEntity.ok(Map.of("mensaje", "Asistencia registrada exitosamente"));
    }
}
