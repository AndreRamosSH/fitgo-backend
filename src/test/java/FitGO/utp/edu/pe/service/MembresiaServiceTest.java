package FitGO.utp.edu.pe.service;

import FitGO.utp.edu.pe.dto.MembresiaRequest;
import FitGO.utp.edu.pe.dto.PlanRequest;
import FitGO.utp.edu.pe.entity.Membresia;
import FitGO.utp.edu.pe.entity.Plan;
import FitGO.utp.edu.pe.entity.Usuario;
import FitGO.utp.edu.pe.repository.MembresiaRepository;
import FitGO.utp.edu.pe.repository.PlanRepository;
import FitGO.utp.edu.pe.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MembresiaServiceTest {

    @Mock
    private PlanRepository planRepository;

    @Mock
    private MembresiaRepository membresiaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private MembresiaService membresiaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void guardarPlan_exitoso() {
        PlanRequest request = new PlanRequest();
        request.setNombre("Plan Premium");
        request.setPrecio(new BigDecimal("150.0"));
        request.setDuracionDias(30);
        request.setDescripcion("Acceso total al gimnasio");

        assertDoesNotThrow(() -> membresiaService.guardarPlan(request));
        verify(planRepository, times(1)).save(any(Plan.class));
    }

    @Test
    void guardarPlan_fallido_precio_invalido() {
        PlanRequest request = new PlanRequest();
        request.setNombre("Plan Premium");
        request.setPrecio(new BigDecimal("-10.0"));
        request.setDuracionDias(30);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            membresiaService.guardarPlan(request);
        });

        assertEquals("El precio debe ser mayor a cero.", exception.getMessage());
        verify(planRepository, never()).save(any(Plan.class));
    }

    @Test
    void asignarMembresia_exitoso() {
        MembresiaRequest request = new MembresiaRequest();
        request.setUsuarioId(1L);
        request.setPlanId(1L);

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setCorreo("juan@fitgo.com");

        Plan plan = new Plan("Plan Mensual", new BigDecimal("100.0"), 30, "Desc");
        plan.setId(1L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));

        assertDoesNotThrow(() -> membresiaService.asignarMembresia(request));
        verify(membresiaRepository, times(1)).save(any(Membresia.class));
    }

    @Test
    void asignarMembresia_fallido_usuario_inexistente() {
        MembresiaRequest request = new MembresiaRequest();
        request.setUsuarioId(99L);
        request.setPlanId(1L);

        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        membresiaService.asignarMembresia(request);

        verify(membresiaRepository, never()).save(any(Membresia.class));
    }
}
