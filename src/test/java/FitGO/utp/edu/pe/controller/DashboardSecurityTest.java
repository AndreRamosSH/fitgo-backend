package FitGO.utp.edu.pe.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import jakarta.servlet.http.Cookie;
import FitGO.utp.edu.pe.security.JwtUtil;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class DashboardSecurityTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void testAccesoAnonimoAdminDenegado() throws Exception {
        mockMvc.perform(get("/dashboard/admin"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testAccesoMiembroAdminDenegado() throws Exception {
        String token = jwtUtil.generarToken("miembro@test.com", "MIEMBRO");
        Cookie cookie = new Cookie("fitgo_token", token);

        mockMvc.perform(get("/dashboard/admin").cookie(cookie))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testAccesoAdminAdminPermitido() throws Exception {
        String token = jwtUtil.generarToken("admin@test.com", "ADMIN");
        Cookie cookie = new Cookie("fitgo_token", token);

        mockMvc.perform(get("/dashboard/admin").cookie(cookie))
                .andExpect(status().isOk());
    }
}
