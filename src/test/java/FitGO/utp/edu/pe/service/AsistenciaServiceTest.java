package FitGO.utp.edu.pe.service;

import FitGO.utp.edu.pe.entity.Asistencia;
import FitGO.utp.edu.pe.entity.Usuario;
import FitGO.utp.edu.pe.repository.AsistenciaRepository;
import FitGO.utp.edu.pe.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AsistenciaServiceTest {

    @Mock
    private AsistenciaRepository asistenciaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private AsistenciaService asistenciaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void calcularRachaActual_cero_sin_asistencias() {
        String correo = "miembro@fitgo.com";
        Usuario usuario = new Usuario();
        usuario.setCorreo(correo);

        when(usuarioRepository.findByCorreo(correo)).thenReturn(Optional.of(usuario));
        when(asistenciaRepository.findByUsuarioOrderByFechaHoraDesc(usuario)).thenReturn(new ArrayList<>());

        int result = asistenciaService.calcularRachaActual(correo);

        assertEquals(0, result);
    }

    @Test
    void calcularRachaActual_racha_de_tres_dias() {
        String correo = "miembro@fitgo.com";
        Usuario usuario = new Usuario();
        usuario.setCorreo(correo);

        LocalDateTime hoy = LocalDateTime.now();
        LocalDateTime ayer = hoy.minusDays(1);
        LocalDateTime anteayer = hoy.minusDays(2);

        Asistencia a1 = new Asistencia();
        a1.setFechaHora(hoy);
        Asistencia a2 = new Asistencia();
        a2.setFechaHora(ayer);
        Asistencia a3 = new Asistencia();
        a3.setFechaHora(anteayer);

        List<Asistencia> asistencias = List.of(a1, a2, a3);

        when(usuarioRepository.findByCorreo(correo)).thenReturn(Optional.of(usuario));
        when(asistenciaRepository.findByUsuarioOrderByFechaHoraDesc(usuario)).thenReturn(asistencias);

        int result = asistenciaService.calcularRachaActual(correo);

        assertEquals(3, result);
    }

    @Test
    void calcularRachaActual_usuario_no_encontrado() {
        String correo = "unknown@fitgo.com";
        when(usuarioRepository.findByCorreo(correo)).thenReturn(Optional.empty());

        int result = asistenciaService.calcularRachaActual(correo);

        assertEquals(0, result);
    }
}
