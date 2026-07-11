package FitGO.utp.edu.pe.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

import FitGO.utp.edu.pe.entity.Asistencia;
import FitGO.utp.edu.pe.entity.Entrenador;
import FitGO.utp.edu.pe.entity.EstadoMembresia;
import FitGO.utp.edu.pe.entity.Membresia;
import FitGO.utp.edu.pe.entity.Plan;
import FitGO.utp.edu.pe.entity.Rol;
import FitGO.utp.edu.pe.entity.Turno;
import FitGO.utp.edu.pe.entity.Usuario;
import FitGO.utp.edu.pe.repository.AsistenciaRepository;
import FitGO.utp.edu.pe.repository.EntrenadorRepository;
import FitGO.utp.edu.pe.repository.MembresiaRepository;
import FitGO.utp.edu.pe.repository.PlanRepository;
import FitGO.utp.edu.pe.repository.UsuarioRepository;

@Profile("dev")
@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PlanRepository planRepository;
    private final MembresiaRepository membresiaRepository;
    private final EntrenadorRepository entrenadorRepository;
    private final AsistenciaRepository asistenciaRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarioRepository,
            PlanRepository planRepository,
            MembresiaRepository membresiaRepository,
            EntrenadorRepository entrenadorRepository,
            AsistenciaRepository asistenciaRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.planRepository = planRepository;
        this.membresiaRepository = membresiaRepository;
        this.entrenadorRepository = entrenadorRepository;
        this.asistenciaRepository = asistenciaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        crearAdminSiNoExiste();
        crearMiembrosSiNoExisten();
        crearEntrenadoresSiNoExisten();
        crearPlanesSiNoExisten();
        crearMembresiasSiNoExisten();
        asignarMiembrosAEntrenadores();
        crearAsistenciasSiNoExisten();
    }

    private void crearAdminSiNoExiste() {
        String correoAdmin = "admin@fitgo.com";

        if (usuarioRepository.existsByCorreo(correoAdmin)) {
            return;
        }

        Usuario admin = new Usuario();
        admin.setNombre("Andre");
        admin.setApellido("Ramos");
        admin.setCorreo(correoAdmin);
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRol(Rol.ADMIN);

        usuarioRepository.save(admin);
    }

    private void crearMiembrosSiNoExisten() {
        String[][] miembros = {
                { "Luis", "Paredes", "miembro1@fitgo.com" },
                { "Sofia", "Gomez", "miembro2@fitgo.com" },
                { "Carlos", "Morales", "miembro3@fitgo.com" },
                { "Valeria", "Lopez", "miembro4@fitgo.com" },
                { "Diego", "Rios", "miembro5@fitgo.com" },
                { "Camila", "Vega", "miembro6@fitgo.com" },
                { "Javier", "Salas", "miembro7@fitgo.com" },
                { "Lucia", "Bravo", "miembro8@fitgo.com" },
                { "Marco", "Castro", "miembro9@fitgo.com" },
                { "Elena", "Flores", "miembro10@fitgo.com" }
        };

        for (String[] miembro : miembros) {
            if (usuarioRepository.existsByCorreo(miembro[2])) {
                continue;
            }

            Usuario usuario = new Usuario();
            usuario.setNombre(miembro[0]);
            usuario.setApellido(miembro[1]);
            usuario.setCorreo(miembro[2]);
            usuario.setPassword(passwordEncoder.encode("miembro123"));
            usuario.setRol(Rol.MIEMBRO);

            usuarioRepository.save(usuario);
        }
    }

    private void crearPlanesSiNoExisten() {
        if (planRepository.count() > 0) {
            return;
        }

        planRepository.save(new Plan(
                "Plan Basico",
                new BigDecimal("50.00"),
                30,
                "Acceso al gimnasio por 1 mes"));

        planRepository.save(new Plan(
                "Plan Estandar",
                new BigDecimal("90.00"),
                60,
                "Acceso al gimnasio por 2 meses"));

        planRepository.save(new Plan(
                "Plan Premium",
                new BigDecimal("140.00"),
                90,
                "Acceso al gimnasio por 3 meses"));
    }

    private void crearMembresiasSiNoExisten() {
        List<Plan> planes = planRepository.findAll();
        if (planes.isEmpty()) {
            return;
        }

        Plan planBasico = planes.get(0);
        Plan planEstandar = planes.size() > 1 ? planes.get(1) : planes.get(0);
        Plan planPremium = planes.size() > 2 ? planes.get(2) : planes.get(0);

        LocalDate hoy = LocalDate.now();

        crearMembresiaParaCorreo("miembro1@fitgo.com", planBasico, hoy.minusDays(10), EstadoMembresia.ACTIVA);
        crearMembresiaParaCorreo("miembro2@fitgo.com", planEstandar, hoy.minusDays(50), EstadoMembresia.ACTIVA);
        crearMembresiaParaCorreo("miembro3@fitgo.com", planPremium, hoy.minusDays(10), EstadoMembresia.ACTIVA);
        crearMembresiaParaCorreo("miembro4@fitgo.com", planBasico, hoy.minusDays(25), EstadoMembresia.POR_VENCER);
        crearMembresiaParaCorreo("miembro5@fitgo.com", planEstandar, hoy.minusDays(65), EstadoMembresia.VENCIDA);
        crearMembresiaParaCorreo("miembro6@fitgo.com", planBasico, hoy.minusDays(31), EstadoMembresia.VENCIDA);
        crearMembresiaParaCorreo("miembro7@fitgo.com", planBasico, hoy.minusDays(29), EstadoMembresia.POR_VENCER);
    }

    private void crearMembresiaParaCorreo(String correo, Plan plan, LocalDate fechaInicio, EstadoMembresia estado) {
        usuarioRepository.findByCorreo(correo).ifPresent(u -> {
            List<Membresia> mems = membresiaRepository.findByUsuarioId(u.getId());
            if (mems.isEmpty()) {
                Membresia membresia = new Membresia();
                membresia.setUsuario(u);
                membresia.setPlan(plan);
                membresia.setFechaInicio(fechaInicio);
                membresia.setFechaFin(fechaInicio.plusDays(plan.getDuracionDias()));
                membresia.setEstado(estado);
                membresiaRepository.save(membresia);
            }
        });
    }

    private void crearEntrenadoresSiNoExisten() {
        String[][] datos = {
                { "Carlos", "Rios", "entrenador1@fitgo.com" },
                { "Ana", "Torres", "entrenador2@fitgo.com" },
                { "Jorge", "Mendoza", "entrenador3@fitgo.com" }
        };

        Turno[] turnos = { Turno.MANANA, Turno.TARDE, Turno.NOCHE };
        int[] experiencias = { 4, 2, 6 };

        for (int i = 0; i < datos.length; i++) {
            Optional<Usuario> userOpt = usuarioRepository.findByCorreo(datos[i][2]);
            Usuario usuario;
            if (userOpt.isPresent()) {
                usuario = userOpt.get();
                if (usuario.getRol() != Rol.ENTRENADOR) {
                    usuario.setRol(Rol.ENTRENADOR);
                    usuario.setPassword(passwordEncoder.encode("entrenador123"));
                    usuarioRepository.save(usuario);
                }
            } else {
                usuario = new Usuario();
                usuario.setNombre(datos[i][0]);
                usuario.setApellido(datos[i][1]);
                usuario.setCorreo(datos[i][2]);
                usuario.setPassword(passwordEncoder.encode("entrenador123"));
                usuario.setRol(Rol.ENTRENADOR);
                usuarioRepository.save(usuario);
            }

            Entrenador entrenador = entrenadorRepository.findByUsuarioId(usuario.getId());
            if (entrenador == null) {
                entrenador = new Entrenador();
                entrenador.setUsuario(usuario);
                entrenador.setTurno(turnos[i]);
                entrenador.setExperienciaAnios(experiencias[i]);
                entrenador.setMaxMiembros(8);
                entrenadorRepository.save(entrenador);
            }
        }
    }

    private void asignarMiembrosAEntrenadores() {
        List<Entrenador> entrenadores = entrenadorRepository.findAll();
        if (entrenadores.isEmpty()) {
            return;
        }

        Entrenador e1 = entrenadores.get(0);
        Entrenador e2 = entrenadores.size() > 1 ? entrenadores.get(1) : e1;
        Entrenador e3 = entrenadores.size() > 2 ? entrenadores.get(2) : e1;

        asignarSiExiste("miembro1@fitgo.com", e1);
        asignarSiExiste("miembro2@fitgo.com", e1);
        asignarSiExiste("miembro3@fitgo.com", e1);

        asignarSiExiste("miembro4@fitgo.com", e2);
        asignarSiExiste("miembro5@fitgo.com", e2);
        asignarSiExiste("miembro6@fitgo.com", e2);

        asignarSiExiste("miembro7@fitgo.com", e3);
        asignarSiExiste("miembro8@fitgo.com", e3);
    }

    private void asignarSiExiste(String correo, Entrenador entrenador) {
        usuarioRepository.findByCorreo(correo).ifPresent(u -> {
            if (u.getEntrenador() == null) {
                List<Membresia> mems = membresiaRepository.findByUsuarioId(u.getId());
                boolean tieneAcceso = mems.stream().anyMatch(Membresia::tieneAcceso);
                if (tieneAcceso) {
                    u.setEntrenador(entrenador);
                    usuarioRepository.save(u);
                }
            }
        });
    }

    private void crearAsistenciasSiNoExisten() {
        if (asistenciaRepository.count() > 0) {
            return;
        }

        List<Membresia> membresias = membresiaRepository.findAll();
        List<Usuario> miembrosActivos = new ArrayList<>();
        for (Membresia m : membresias) {
            if (m.getEstado() == EstadoMembresia.ACTIVA || m.getEstado() == EstadoMembresia.POR_VENCER) {
                miembrosActivos.add(m.getUsuario());
            }
        }

        if (miembrosActivos.isEmpty()) {
            return;
        }

        LocalDate hoy = LocalDate.now();
        for (Usuario miembro : miembrosActivos) {
            long hash = miembro.getId() * 31;
            for (int i = 30; i >= 0; i--) {
                LocalDate dia = hoy.minusDays(i);
                if (dia.getDayOfWeek().getValue() == 7) {
                    continue;
                }
                
                long pseudoRandom = (hash + (long) i * 17) % 10;
                if (pseudoRandom < 6) {
                    Asistencia asistencia = new Asistencia();
                    asistencia.setUsuario(miembro);
                    
                    int hora = 7 + (int) ((pseudoRandom * 3) % 14);
                    int minuto = (int) ((pseudoRandom * 12) % 60);
                    
                    asistencia.setFechaHora(dia.atTime(hora, minuto));
                    asistenciaRepository.save(asistencia);
                }
            }
        }
    }
}
