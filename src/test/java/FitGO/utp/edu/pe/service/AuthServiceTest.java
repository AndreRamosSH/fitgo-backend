package FitGO.utp.edu.pe.service;

import FitGO.utp.edu.pe.dto.LoginRequest;
import FitGO.utp.edu.pe.dto.RegistroRequest;
import FitGO.utp.edu.pe.entity.Rol;
import FitGO.utp.edu.pe.entity.Usuario;
import FitGO.utp.edu.pe.repository.AsistenciaRepository;
import FitGO.utp.edu.pe.repository.EntrenadorRepository;
import FitGO.utp.edu.pe.repository.MembresiaRepository;
import FitGO.utp.edu.pe.repository.UsuarioRepository;
import FitGO.utp.edu.pe.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AsistenciaRepository asistenciaRepository;

    @Mock
    private EntrenadorRepository entrenadorRepository;

    @Mock
    private MembresiaRepository membresiaRepository;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void autenticar_exitoso() {
        LoginRequest request = new LoginRequest("test@fitgo.com", "password123");
        Usuario usuario = new Usuario();
        usuario.setCorreo("test@fitgo.com");
        usuario.setPassword("encodedPassword");
        usuario.setRol(Rol.MIEMBRO);

        when(usuarioRepository.findByCorreo("test@fitgo.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generarToken("test@fitgo.com", "MIEMBRO")).thenReturn("mockedToken");

        Optional<String> result = authService.autenticar(request);

        assertTrue(result.isPresent());
        assertEquals("mockedToken", result.get());
        verify(usuarioRepository, times(1)).findByCorreo("test@fitgo.com");
    }

    @Test
    void autenticar_fallido_password_incorrecto() {
        LoginRequest request = new LoginRequest("test@fitgo.com", "wrongPassword");
        Usuario usuario = new Usuario();
        usuario.setCorreo("test@fitgo.com");
        usuario.setPassword("encodedPassword");
        usuario.setRol(Rol.MIEMBRO);

        when(usuarioRepository.findByCorreo("test@fitgo.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        Optional<String> result = authService.autenticar(request);

        assertFalse(result.isPresent());
    }

    @Test
    void autenticar_fallido_usuario_inexistente() {
        LoginRequest request = new LoginRequest("nonexistent@fitgo.com", "password123");

        when(usuarioRepository.findByCorreo("nonexistent@fitgo.com")).thenReturn(Optional.empty());

        Optional<String> result = authService.autenticar(request);

        assertFalse(result.isPresent());
    }

    @Test
    void registrarUsuario_exitoso() {
        RegistroRequest request = new RegistroRequest("Juan", "Perez", "juan.perez@fitgo.com", "password123", Rol.MIEMBRO);

        when(usuarioRepository.existsByCorreo("juan.perez@fitgo.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        assertDoesNotThrow(() -> authService.registrarUsuario(request));
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void registrarUsuario_fallido_nombre_con_numeros() {
        RegistroRequest request = new RegistroRequest("Juan123", "Perez", "juan.perez@fitgo.com", "password123", Rol.MIEMBRO);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.registrarUsuario(request);
        });

        assertEquals("El nombre no debe contener numeros.", exception.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
}
