package FitGO.utp.edu.pe.service;

import FitGO.utp.edu.pe.dto.AsistenciaRequest;
import FitGO.utp.edu.pe.dto.LoginRequest;
import FitGO.utp.edu.pe.dto.RegistroRequest;
import FitGO.utp.edu.pe.entity.Asistencia;
import FitGO.utp.edu.pe.entity.Entrenador;
import FitGO.utp.edu.pe.entity.Rol;
import FitGO.utp.edu.pe.entity.Usuario;
import FitGO.utp.edu.pe.repository.AsistenciaRepository;
import FitGO.utp.edu.pe.repository.EntrenadorRepository;
import FitGO.utp.edu.pe.repository.UsuarioRepository;
import FitGO.utp.edu.pe.repository.MembresiaRepository;
import FitGO.utp.edu.pe.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AuthService {

        private final JwtUtil jwtUtil;
        private final PasswordEncoder passwordEncoder;
        private final UsuarioRepository usuarioRepository;
        private final AsistenciaRepository asistenciaRepository;
        private final EntrenadorRepository entrenadorRepository;
        private final MembresiaRepository membresiaRepository;

        public AuthService(
                        JwtUtil jwtUtil,
                        PasswordEncoder passwordEncoder,
                        UsuarioRepository usuarioRepository,
                        AsistenciaRepository asistenciaRepository,
                        EntrenadorRepository entrenadorRepository,
                        MembresiaRepository membresiaRepository) {
                this.jwtUtil = jwtUtil;
                this.passwordEncoder = passwordEncoder;
                this.usuarioRepository = usuarioRepository;
                this.asistenciaRepository = asistenciaRepository;
                this.entrenadorRepository = entrenadorRepository;
                this.membresiaRepository = membresiaRepository;
        }

        public Optional<String> autenticar(LoginRequest request) {
                Optional<Usuario> usuario = usuarioRepository.findByCorreo(request.getCorreo());

                if (usuario.isEmpty()) {
                        return Optional.empty();
                }

                Usuario u = usuario.get();
                if (!passwordEncoder.matches(request.getPassword(), u.getPassword())) {
                        return Optional.empty();
                }

                String token = jwtUtil.generarToken(u.getCorreo(), u.getRol().name());
                return Optional.of(token);
        }

        public Optional<Usuario> buscarPorCorreo(String correo) {
                return usuarioRepository.findByCorreo(correo);
        }

        public List<Usuario> listarUsuarios() {
                return usuarioRepository.findAll();
        }

        public List<Usuario> listarMiembros() {
                return usuarioRepository.findByRol(Rol.MIEMBRO);
        }

        public List<Usuario> listarMiembrosPorEntrenador(String correoEntrenador) {
                Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correoEntrenador);
                if (usuarioOpt.isEmpty()) {
                        return List.of();
                }
                Entrenador entrenador = entrenadorRepository.findByUsuarioId(usuarioOpt.get().getId());
                if (entrenador == null) {
                        return List.of();
                }

                return usuarioRepository.findByRolAndEntrenadorId(Rol.MIEMBRO, entrenador.getId());
        }

        public List<Usuario> buscarPorRol(Rol rol) {
                return usuarioRepository.findByRol(rol);
        }

        @Transactional
        public void registrarUsuario(RegistroRequest request) {
                if (request.getNombre() == null || request.getNombre().trim().isEmpty() ||
                                request.getNombre().matches(".*\\d.*")) {
                        throw new IllegalArgumentException("El nombre no debe contener numeros.");
                }
                if (request.getApellido() == null || request.getApellido().trim().isEmpty() ||
                                request.getApellido().matches(".*\\d.*")) {
                        throw new IllegalArgumentException("El apellido no debe contener numeros.");
                }
                if (request.getCorreo() == null || request.getCorreo().trim().isEmpty()) {
                        throw new IllegalArgumentException("El correo electronico es obligatorio.");
                }
                if (usuarioRepository.findByCorreo(request.getCorreo().trim()).isPresent()) {
                        throw new IllegalArgumentException("El correo electronico ya esta registrado.");
                }
                if (request.getPassword() == null || request.getPassword().length() < 6) {
                        throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.");
                }
                if (request.getRol() == Rol.ENTRENADOR) {
                        if (request.getExperienciaAnios() != null && request.getExperienciaAnios() < 0) {
                                throw new IllegalArgumentException("Los años de experiencia no pueden ser negativos.");
                        }
                }

                Usuario usuario = new Usuario(
                                null,
                                request.getNombre(),
                                request.getApellido(),
                                request.getCorreo(),
                                passwordEncoder.encode(request.getPassword()),
                                request.getRol(),
                                request.getTelefono());

                Usuario usuarioGuardado = usuarioRepository.save(usuario);

                if (request.getRol() == Rol.ENTRENADOR) {
                        Entrenador entrenador = new Entrenador();
                        entrenador.setUsuario(usuarioGuardado);
                        entrenador.setTurno(request.getTurno() != null ? request.getTurno()
                                        : FitGO.utp.edu.pe.entity.Turno.MANANA);
                        entrenador.setExperienciaAnios(
                                        request.getExperienciaAnios() != null ? request.getExperienciaAnios() : 0);
                        entrenador.setMaxMiembros(8);
                        entrenadorRepository.save(entrenador);
                }
        }

        public void registrarAsistencia(AsistenciaRequest request) {
                Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(request.getCorreo());

                if (usuarioOpt.isEmpty()) {
                        return;
                }

                Asistencia asistencia = new Asistencia();
                asistencia.setUsuario(usuarioOpt.get());
                asistencia.setFechaHora(LocalDateTime.now());
                asistenciaRepository.save(asistencia);
        }

        public List<Asistencia> listarAsistencias() {
                return asistenciaRepository.findAll();
        }

        public long contarAsistenciasHoy() {
                java.time.LocalDate hoy = java.time.LocalDate.now();
                java.time.LocalDateTime inicio = hoy.atStartOfDay();
                java.time.LocalDateTime fin = hoy.plusDays(1).atStartOfDay();
                return asistenciaRepository.countAsistenciasEntre(inicio, fin);
        }

        @Transactional
        public void eliminarEntrenador(Long id) {
                Optional<Entrenador> entOpt = entrenadorRepository.findById(id);
                if (entOpt.isPresent()) {
                        Entrenador e = entOpt.get();
                        usuarioRepository.desasignarEntrenador(id);
                        entrenadorRepository.eliminarPorId(id);
                        usuarioRepository.eliminarPorId(e.getUsuario().getId());
                }
        }

        @Transactional
        public void eliminarMiembro(Long id) {
                membresiaRepository.eliminarPorUsuarioId(id);
                asistenciaRepository.eliminarPorUsuarioId(id);
                usuarioRepository.eliminarPorId(id);
        }
        public void guardarUsuario(Usuario usuario) {
                usuarioRepository.save(usuario);
        }

        public Page<Usuario> listarUsuariosPaginado(int page, int size) {
                return usuarioRepository.findAll(PageRequest.of(page, size));
        }

        public Page<Usuario> listarMiembrosPaginado(int page, int size) {
                return usuarioRepository.findByRol(Rol.MIEMBRO, PageRequest.of(page, size));
        }
}
