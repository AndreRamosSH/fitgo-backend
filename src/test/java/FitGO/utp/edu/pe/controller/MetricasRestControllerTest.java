package FitGO.utp.edu.pe.controller;

import FitGO.utp.edu.pe.dto.MetricasDTO;
import FitGO.utp.edu.pe.entity.Rol;
import FitGO.utp.edu.pe.entity.Usuario;
import FitGO.utp.edu.pe.repository.ProgresoRepository;
import FitGO.utp.edu.pe.repository.UsuarioRepository;
import FitGO.utp.edu.pe.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import jakarta.servlet.http.Cookie;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
public class MetricasRestControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @MockitoBean
    private ProgresoRepository progresoRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String token;
    private Cookie cookie;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        this.token = jwtUtil.generarToken("miembro@test.com", "MIEMBRO");
        this.cookie = new Cookie("fitgo_token", this.token);
    }

    @Test
    public void testRegistrarMetricasExitoso() throws Exception {
        MetricasDTO dto = new MetricasDTO(75.5, 175.0, 24.6);
        Usuario usuario = new Usuario();
        usuario.setCorreo("miembro@test.com");
        usuario.setRol(Rol.MIEMBRO);

        when(usuarioRepository.findByCorreo("miembro@test.com")).thenReturn(Optional.of(usuario));

        mockMvc.perform(post("/api/miembro/metricas/registrar")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Métricas actualizadas con éxito"));
    }

    @Test
    public void testRegistrarMetricasInvalido() throws Exception {
        // Peso -5.0 es menor que el DecimalMin(10.0) configurado
        MetricasDTO dto = new MetricasDTO(-5.0, 175.0, 0.0);

        mockMvc.perform(post("/api/miembro/metricas/registrar")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.errores.peso").exists());
    }
}
