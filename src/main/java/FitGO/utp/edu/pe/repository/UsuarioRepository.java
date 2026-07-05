package FitGO.utp.edu.pe.repository;

import FitGO.utp.edu.pe.entity.Rol;
import FitGO.utp.edu.pe.entity.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);
    List<Usuario> findByRol(Rol rol);

    List<Usuario> findByRolAndEntrenadorId(Rol rol, Long entrenadorId);
    
    @Query("SELECT u FROM Usuario u WHERE u.rol = FitGO.utp.edu.pe.entity.Rol.MIEMBRO AND NOT EXISTS (SELECT m FROM Membresia m WHERE m.usuario = u AND (m.estado = FitGO.utp.edu.pe.entity.EstadoMembresia.ACTIVA OR m.estado = FitGO.utp.edu.pe.entity.EstadoMembresia.POR_VENCER))")
    List<Usuario> findMiembrosSinMembresiaActiva();

    @Modifying
    @Query("UPDATE Usuario u SET u.entrenador = null WHERE u.entrenador.id = :entrenadorId")
    void desasignarEntrenador(@Param("entrenadorId") Long entrenadorId);

    @Modifying
    @Query("DELETE FROM Usuario u WHERE u.id = :id")
    void eliminarPorId(@Param("id") Long id);
}
