package FitGO.utp.edu.pe.controller;

import FitGO.utp.edu.pe.dto.LoginRequest;
import FitGO.utp.edu.pe.entity.Rol;
import FitGO.utp.edu.pe.entity.Usuario;
import FitGO.utp.edu.pe.service.AuthService;
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

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
public class AuthRestControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void testLoginExitoso() throws Exception {
        LoginRequest request = new LoginRequest("juan@fitgo.com", "password123");
        when(authService.autenticar(any(LoginRequest.class))).thenReturn(Optional.of("fake-token"));
        
        Usuario fakeUsuario = new Usuario();
        fakeUsuario.setNombre("Juan");
        fakeUsuario.setApellido("Perez");
        fakeUsuario.setCorreo("juan@fitgo.com");
        fakeUsuario.setRol(Rol.MIEMBRO);
        when(authService.buscarPorCorreo("juan@fitgo.com")).thenReturn(Optional.of(fakeUsuario));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-token"))
                .andExpect(jsonPath("$.nombre").value("Juan"));
    }

    @Test
    public void testLoginInvalido() throws Exception {
        LoginRequest request = new LoginRequest("juan@fitgo.com", ""); // Contraseña vacía para forzar validación
        
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.errores.password").exists());
    }
}
