package FitGO.utp.edu.pe.service;

import FitGO.utp.edu.pe.dto.DiaCalendarioDTO;
import FitGO.utp.edu.pe.dto.ProgresoEntrenamientoResponse;
import FitGO.utp.edu.pe.entity.EstadoMembresia;
import FitGO.utp.edu.pe.entity.Membresia;
import FitGO.utp.edu.pe.entity.Progreso;
import FitGO.utp.edu.pe.entity.Usuario;
import FitGO.utp.edu.pe.entity.EntrenamientoRealizado;
import FitGO.utp.edu.pe.repository.MembresiaRepository;
import FitGO.utp.edu.pe.repository.ProgresoRepository;
import FitGO.utp.edu.pe.repository.UsuarioRepository;
import FitGO.utp.edu.pe.repository.EntrenamientoRealizadoRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardMiembroService {

    private final ProgresoRepository progresoRepository;
    private final MembresiaRepository membresiaRepository;
    private final UsuarioRepository usuarioRepository;
    private final MembresiaService membresiaService;
    private final EntrenamientoRealizadoRepository entrenamientoRealizadoRepository;

    public DashboardMiembroService(ProgresoRepository progresoRepository,
                                   MembresiaRepository membresiaRepository,
                                   UsuarioRepository usuarioRepository,
                                   @Lazy MembresiaService membresiaService,
                                   EntrenamientoRealizadoRepository entrenamientoRealizadoRepository) {
        this.progresoRepository = progresoRepository;
        this.membresiaRepository = membresiaRepository;
        this.usuarioRepository = usuarioRepository;
        this.membresiaService = membresiaService;
        this.entrenamientoRealizadoRepository = entrenamientoRealizadoRepository;
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

    public ProgresoEntrenamientoResponse obtenerProgresoCompleto(String correo, String mesParam) {
        Double peso = obtenerUltimoPeso(correo);

        List<EntrenamientoRealizado> historial = entrenamientoRealizadoRepository.findByUsuarioCorreoOrderByFechaDesc(correo);

        long entrenosTotales = historial.size();
        long totalSegundos = historial.stream().mapToLong(EntrenamientoRealizado::getDuracionSegundos).sum();
        long tiempoTotalHoras = totalSegundos / 3600;
        long tiempoPromedioMinutos = entrenosTotales == 0 ? 0 : Math.round(((double) totalSegundos / 60.0) / entrenosTotales);
        long seriesTotales = historial.stream().mapToLong(EntrenamientoRealizado::getTotalSeriesCompletadas).sum();

        // Determinar mes objetivo
        YearMonth targetMonth;
        if (mesParam != null && !mesParam.trim().isEmpty()) {
            try {
                targetMonth = YearMonth.parse(mesParam.trim());
            } catch (DateTimeParseException e) {
                targetMonth = YearMonth.now();
            }
        } else {
            targetMonth = YearMonth.now();
        }

        // Entrenamientos del mes seleccionado
        final YearMonth finalTargetMonth = targetMonth;
        List<EntrenamientoRealizado> entrenosTarget = historial.stream()
                .filter(e -> YearMonth.from(e.getFecha()).equals(finalTargetMonth))
                .toList();

        long entrenosEsteMes = entrenosTarget.size();

        // Comparación de series del mes seleccionado contra el mes anterior
        YearMonth prevMonth = targetMonth.minusMonths(1);
        List<EntrenamientoRealizado> entrenosPrev = historial.stream()
                .filter(e -> YearMonth.from(e.getFecha()).equals(prevMonth))
                .toList();

        long seriesEsteMes = entrenosTarget.stream().mapToLong(EntrenamientoRealizado::getTotalSeriesCompletadas).sum();
        long seriesMesAnterior = entrenosPrev.stream().mapToLong(EntrenamientoRealizado::getTotalSeriesCompletadas).sum();

        double seriesPorcentajeVariacion = 0.0;
        if (seriesMesAnterior > 0) {
            seriesPorcentajeVariacion = ((double) (seriesEsteMes - seriesMesAnterior) / seriesMesAnterior) * 100.0;
        } else if (seriesEsteMes > 0) {
            seriesPorcentajeVariacion = 100.0;
        }

        // Racha actual de días seguidos entrenando
        Set<LocalDate> fechasEntreno = historial.stream()
                .map(e -> e.getFecha().toLocalDate())
                .collect(Collectors.toSet());

        long rachaActual = 0;
        LocalDate cursor = LocalDate.now();
        if (!fechasEntreno.contains(cursor)) {
            cursor = cursor.minusDays(1);
        }
        while (fechasEntreno.contains(cursor)) {
            rachaActual++;
            cursor = cursor.minusDays(1);
        }

        // Agrupación por semanas de entrenos del mes seleccionado
        int w1 = 0, w2 = 0, w3 = 0, w4 = 0;
        for (EntrenamientoRealizado e : entrenosTarget) {
            int dia = e.getFecha().getDayOfMonth();
            if (dia <= 7) {
                w1++;
            } else if (dia <= 14) {
                w2++;
            } else if (dia <= 21) {
                w3++;
            } else {
                w4++;
            }
        }
        List<Integer> semanas = List.of(w1, w2, w3, w4);

        // Generar lista de días del calendario para el mes seleccionado
        List<DiaCalendarioDTO> calendario = new ArrayList<>();
        int lengthOfMonth = targetMonth.lengthOfMonth();
        LocalDate hoy = LocalDate.now();

        for (int d = 1; d <= lengthOfMonth; d++) {
            LocalDate date = targetMonth.atDay(d);
            boolean entreno = fechasEntreno.contains(date);
            boolean esHoy = date.equals(hoy);
            calendario.add(new DiaCalendarioDTO(d, entreno, esHoy));
        }

        return new ProgresoEntrenamientoResponse(
                peso,
                entrenosTotales,
                entrenosEsteMes,
                tiempoTotalHoras,
                tiempoPromedioMinutos,
                seriesTotales,
                seriesPorcentajeVariacion,
                rachaActual,
                semanas,
                calendario
        );
    }
}
