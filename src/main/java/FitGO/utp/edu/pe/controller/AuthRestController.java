package FitGO.utp.edu.pe.controller;

import FitGO.utp.edu.pe.dto.LoginRequest;
import FitGO.utp.edu.pe.dto.LoginResponse;
import FitGO.utp.edu.pe.dto.RegistroRequest;
import FitGO.utp.edu.pe.entity.Rol;
import FitGO.utp.edu.pe.entity.Usuario;
import FitGO.utp.edu.pe.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    private final AuthService authService;

    public AuthRestController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<String> tokenOpt = authService.autenticar(loginRequest);

        if (tokenOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Correo o contraseña incorrectos"));
        }

        Optional<Usuario> usuarioOpt = authService.buscarPorCorreo(loginRequest.getCorreo());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno al recuperar datos del usuario"));
        }

        Usuario usuario = usuarioOpt.get();
        LoginResponse response = new LoginResponse(
                tokenOpt.get(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getCorreo(),
                usuario.getRol(),
                usuario.getTelefono()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registro(@RequestBody RegistroRequest registroRequest) {
        try {
            registroRequest.setRol(Rol.MIEMBRO);
            authService.registrarUsuario(registroRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("mensaje", "Usuario registrado exitosamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
