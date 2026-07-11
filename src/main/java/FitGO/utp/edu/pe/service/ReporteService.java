package FitGO.utp.edu.pe.service;

import FitGO.utp.edu.pe.dto.AsistenciaReportDTO;
import FitGO.utp.edu.pe.dto.MembresiaReportDTO;
import FitGO.utp.edu.pe.dto.EntrenadorReportDTO;
import FitGO.utp.edu.pe.entity.*;
import FitGO.utp.edu.pe.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReporteService {

    private final AsistenciaRepository asistenciaRepository;
    private final UsuarioRepository usuarioRepository;
    private final MembresiaRepository membresiaRepository;
    private final EntrenadorRepository entrenadorRepository;

    public ReporteService(AsistenciaRepository asistenciaRepository,
                          UsuarioRepository usuarioRepository,
                          MembresiaRepository membresiaRepository,
                          EntrenadorRepository entrenadorRepository) {
        this.asistenciaRepository = asistenciaRepository;
        this.usuarioRepository = usuarioRepository;
        this.membresiaRepository = membresiaRepository;
        this.entrenadorRepository = entrenadorRepository;
    }

    public AsistenciaReportDTO obtenerReporteAsistencias(String mesStr, String usuarioIdStr) {
        YearMonth ym;
        try {
            ym = YearMonth.parse(mesStr);
        } catch (Exception e) {
            ym = YearMonth.now();
        }

        LocalDateTime inicioMes = ym.atDay(1).atStartOfDay();
        LocalDateTime finMes = ym.atEndOfMonth().atTime(LocalTime.MAX);

        List<Asistencia> asistencias;
        if (usuarioIdStr != null && !usuarioIdStr.trim().isEmpty() && !usuarioIdStr.equals("todos")) {
            try {
                Long usuarioId = Long.parseLong(usuarioIdStr);
                asistencias = asistenciaRepository.findByUsuarioIdAndFechaHoraBetween(usuarioId, inicioMes, finMes);
            } catch (NumberFormatException e) {
                asistencias = asistenciaRepository.findByFechaHoraBetween(inicioMes, finMes);
            }
        } else {
            asistencias = asistenciaRepository.findByFechaHoraBetween(inicioMes, finMes);
        }

        long totalIngresos = asistencias.size();

        int totalDias;
        if (ym.getYear() == LocalDate.now().getYear() && ym.getMonth() == LocalDate.now().getMonth()) {
            totalDias = LocalDate.now().getDayOfMonth();
        } else {
            totalDias = ym.lengthOfMonth();
        }
        double prom = totalDias > 0 ? (double) totalIngresos / totalDias : 0.0;
        double promedioDiario = Math.round(prom * 10.0) / 10.0;

        Map<DayOfWeek, Long> countByDayOfWeek = asistencias.stream()
                .collect(Collectors.groupingBy(a -> a.getFechaHora().getDayOfWeek(), Collectors.counting()));
        Optional<Map.Entry<DayOfWeek, Long>> maxEntry = countByDayOfWeek.entrySet().stream()
                .max(Map.Entry.comparingByValue());
        String diaMasConcurrido = "—";
        if (maxEntry.isPresent()) {
            DayOfWeek dow = maxEntry.get().getKey();
            long count = maxEntry.get().getValue();
            String dowEs = dow.getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("es-ES"));
            if (!dowEs.isEmpty()) {
                dowEs = dowEs.substring(0, 1).toUpperCase() + dowEs.substring(1);
            }
            diaMasConcurrido = dowEs + " (" + count + ")";
        }

        List<Usuario> miembros = usuarioRepository.findByRol(Rol.MIEMBRO);
        Set<Long> usuarioIdsConAsistencia = asistencias.stream()
                .map(a -> a.getUsuario().getId())
                .collect(Collectors.toSet());
        long miembrosAusentes = miembros.stream()
                .filter(m -> !usuarioIdsConAsistencia.contains(m.getId()))
                .count();

        List<AsistenciaReportDTO.IngresoDiaDTO> ingresosPorDia = new ArrayList<>();
        Map<Integer, Long> countByDayOfMonth = asistencias.stream()
                .collect(Collectors.groupingBy(a -> a.getFechaHora().getDayOfMonth(), Collectors.counting()));

        long maxVal = countByDayOfMonth.values().stream().max(Long::compare).orElse(0L);

        for (int d = 1; d <= ym.lengthOfMonth(); d++) {
            long count = countByDayOfMonth.getOrDefault(d, 0L);
            boolean destacado = (count > 0 && count == maxVal);
            String label = String.valueOf(d);
            ingresosPorDia.add(new AsistenciaReportDTO.IngresoDiaDTO(label, count, destacado));
        }

        List<AsistenciaReportDTO.AsistenciaDiaSemanaDTO> asistenciasPorDiaSemana = new ArrayList<>();
        DayOfWeek[] daysOfWeekOrder = { DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY };
        Map<DayOfWeek, Long> countByDayOfWeekMap = asistencias.stream()
                .collect(Collectors.groupingBy(a -> a.getFechaHora().getDayOfWeek(), Collectors.counting()));

        long maxDayOfWeekVal = countByDayOfWeekMap.values().stream().max(Long::compare).orElse(0L);

        for (DayOfWeek dow : daysOfWeekOrder) {
            long val = countByDayOfWeekMap.getOrDefault(dow, 0L);
            double pct = maxDayOfWeekVal > 0 ? (double) val / maxDayOfWeekVal * 100.0 : 0.0;
            pct = Math.round(pct * 10.0) / 10.0;
            String name = dow.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es-ES"));
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
            asistenciasPorDiaSemana.add(new AsistenciaReportDTO.AsistenciaDiaSemanaDTO(name, val, pct));
        }

        List<AsistenciaReportDTO.FiltroDTO> mesesDisponibles = new ArrayList<>();
        YearMonth currentYM = YearMonth.now();
        for (int i = 0; i < 6; i++) {
            YearMonth target = currentYM.minusMonths(i);
            String val = target.toString();
            String text = target.getMonth().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es-ES")) + " " + target.getYear();
            text = text.substring(0, 1).toUpperCase() + text.substring(1);
            mesesDisponibles.add(new AsistenciaReportDTO.FiltroDTO(val, text));
        }

        List<AsistenciaReportDTO.FiltroDTO> miembrosDisponibles = new ArrayList<>();
        miembrosDisponibles.add(new AsistenciaReportDTO.FiltroDTO("todos", "Todos los miembros"));
        for (Usuario m : miembros) {
            miembrosDisponibles.add(new AsistenciaReportDTO.FiltroDTO(m.getId().toString(), m.getNombre() + " " + m.getApellido()));
        }

        return new AsistenciaReportDTO(
                totalIngresos, promedioDiario, diaMasConcurrido, miembrosAusentes,
                ingresosPorDia, asistenciasPorDiaSemana, mesesDisponibles, miembrosDisponibles
        );
    }

    @Transactional
    public MembresiaReportDTO obtenerReporteMembresias() {
        LocalDate hoy = LocalDate.now();
        LocalDate limite = hoy.plusDays(3);
        membresiaRepository.actualizarVencidas(hoy);
        membresiaRepository.actualizarPorVencer(hoy, limite);
        membresiaRepository.actualizarActivas(limite);

        long activas = membresiaRepository.countByEstado(EstadoMembresia.ACTIVA);
        long porVencer = membresiaRepository.countByEstado(EstadoMembresia.POR_VENCER);
        long vencidas = membresiaRepository.countByEstado(EstadoMembresia.VENCIDA);

        List<Membresia> allMembresias = membresiaRepository.findAll();

        Map<String, Long> countByPlan = allMembresias.stream()
                .collect(Collectors.groupingBy(m -> m.getPlan().getNombre(), Collectors.counting()));
        String planMasPopular = countByPlan.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Ninguno");

        List<MembresiaReportDTO.DistribucionPlanDTO> distribucionPlan = new ArrayList<>();
        long totalMembresias = allMembresias.size();
        String[] colors = { "#F97316", "#10B981", "#3B82F6", "#EC4899", "#8B5CF6" };
        int colorIdx = 0;
        for (Map.Entry<String, Long> entry : countByPlan.entrySet()) {
            double pct = totalMembresias > 0 ? (double) entry.getValue() / totalMembresias * 100.0 : 0.0;
            pct = Math.round(pct * 10.0) / 10.0;
            String color = colors[colorIdx % colors.length];
            distribucionPlan.add(new MembresiaReportDTO.DistribucionPlanDTO(entry.getKey(), entry.getValue(), pct, color));
            colorIdx++;
        }

        List<Membresia> sortedMembresias = allMembresias.stream()
                .sorted(Comparator.comparing(Membresia::getFechaFin))
                .limit(10)
                .toList();

        List<MembresiaReportDTO.ProximaVencerDTO> proximasVencer = new ArrayList<>();
        for (Membresia m : sortedMembresias) {
            String name = m.getUsuario().getNombre() + " " + m.getUsuario().getApellido();
            String planName = m.getPlan().getNombre();
            
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd MMM", Locale.forLanguageTag("es-ES"));
            String dateStr = m.getFechaFin().format(formatter);
            
            long daysLeft = ChronoUnit.DAYS.between(hoy, m.getFechaFin());
            String daysStr;
            String stateClass;
            if (m.getEstado() == EstadoMembresia.VENCIDA || daysLeft < 0) {
                daysStr = "Vencida";
                stateClass = "vencida";
            } else if (daysLeft <= 7) {
                daysStr = daysLeft + " días";
                stateClass = "por-vencer";
            } else {
                daysStr = daysLeft + " días";
                stateClass = "bueno";
            }
            proximasVencer.add(new MembresiaReportDTO.ProximaVencerDTO(name, planName, dateStr, daysStr, stateClass));
        }

        return new MembresiaReportDTO(activas, porVencer, vencidas, planMasPopular, distribucionPlan, proximasVencer);
    }

    public EntrenadorReportDTO obtenerReporteEntrenadores() {
        List<Entrenador> entrenadores = entrenadorRepository.findAll();
        long totalEntrenadores = entrenadores.size();

        long totalRutinasCreadas = entrenadores.stream()
                .mapToLong(e -> e.getExperienciaAnios() - 1 > 0 ? e.getExperienciaAnios() - 1 : 2)
                .sum();

        Entrenador topTrainer = null;
        long maxMembers = -1;
        for (Entrenador ent : entrenadores) {
            long membersCount = usuarioRepository.findByRolAndEntrenadorId(Rol.MIEMBRO, ent.getId()).size();
            if (membersCount > maxMembers) {
                maxMembers = membersCount;
                topTrainer = ent;
            }
        }
        String masMiembros = topTrainer != null ? topTrainer.getUsuario().getNombre() + " " + topTrainer.getUsuario().getApellido() + " (" + maxMembers + " alumnos)" : "Ninguno";

        long totalMembers = usuarioRepository.findByRol(Rol.MIEMBRO).size();
        double avg = totalEntrenadores > 0 ? (double) totalMembers / totalEntrenadores : 0.0;
        double promedioAlumnos = Math.round(avg * 10.0) / 10.0;

        List<EntrenadorReportDTO.ComparativaEntrenadorDTO> comparativa = new ArrayList<>();
        for (Entrenador ent : entrenadores) {
            String name = ent.getUsuario().getNombre() + " " + ent.getUsuario().getApellido();
            List<Usuario> assigned = usuarioRepository.findByRolAndEntrenadorId(Rol.MIEMBRO, ent.getId());
            long membersCount = assigned.size();
            
            double load = ent.getMaxMiembros() > 0 ? (double) membersCount / ent.getMaxMiembros() * 100.0 : 0.0;
            load = Math.round(load * 10.0) / 10.0;
            
            String loadColor;
            if (load <= 50.0) {
                loadColor = "#10B981";
            } else if (load <= 80.0) {
                loadColor = "#F97316";
            } else {
                loadColor = "#EF4444";
            }
            
            long activeMembers = 0;
            for (Usuario member : assigned) {
                List<Membresia> mems = membresiaRepository.findByUsuarioId(member.getId());
                boolean hasActive = mems.stream().anyMatch(m -> m.getEstado() == EstadoMembresia.ACTIVA || m.getEstado() == EstadoMembresia.POR_VENCER);
                if (hasActive) {
                    activeMembers++;
                }
            }
            double activePct = membersCount > 0 ? (double) activeMembers / membersCount * 100.0 : 0.0;
            activePct = Math.round(activePct * 10.0) / 10.0;
            String activePctStr = ((int) activePct) + "%";
            
            String stateClass;
            if (activePct >= 90.0) {
                stateClass = "excelente";
            } else if (activePct >= 70.0) {
                stateClass = "bueno";
            } else {
                stateClass = "regular";
            }
            
            long mockRoutines = ent.getExperienciaAnios() - 1 > 0 ? ent.getExperienciaAnios() - 1 : 2;
            
            comparativa.add(new EntrenadorReportDTO.ComparativaEntrenadorDTO(
                name, membersCount, mockRoutines, activePctStr, stateClass, load, loadColor
            ));
        }

        return new EntrenadorReportDTO(totalEntrenadores, totalRutinasCreadas, masMiembros, promedioAlumnos, comparativa);
    }
}
