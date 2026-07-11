package FitGO.utp.edu.pe.controller;

import FitGO.utp.edu.pe.dto.AsistenciaReportDTO;
import FitGO.utp.edu.pe.dto.MembresiaReportDTO;
import FitGO.utp.edu.pe.dto.EntrenadorReportDTO;
import FitGO.utp.edu.pe.service.ReporteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/reportes")
@CrossOrigin(origins = "http://localhost:4200")
public class ReportesRestController {

    private final ReporteService reporteService;

    public ReportesRestController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/asistencias")
    public ResponseEntity<?> obtenerReporteAsistencias(
            Authentication auth,
            @RequestParam(required = false, defaultValue = "") String mes,
            @RequestParam(required = false, defaultValue = "todos") String usuarioId) {
        if (auth == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));
        }
        AsistenciaReportDTO dto = reporteService.obtenerReporteAsistencias(mes, usuarioId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/membresias")
    public ResponseEntity<?> obtenerReporteMembresias(Authentication auth) {
        if (auth == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));
        }
        MembresiaReportDTO dto = reporteService.obtenerReporteMembresias();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/entrenadores")
    public ResponseEntity<?> obtenerReporteEntrenadores(Authentication auth) {
        if (auth == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));
        }
        EntrenadorReportDTO dto = reporteService.obtenerReporteEntrenadores();
        return ResponseEntity.ok(dto);
    }
}
