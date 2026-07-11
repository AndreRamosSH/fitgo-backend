package FitGO.utp.edu.pe.repository;

import FitGO.utp.edu.pe.entity.EstadoMembresia;
import FitGO.utp.edu.pe.entity.Membresia;
import FitGO.utp.edu.pe.entity.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MembresiaRepository extends JpaRepository<Membresia, Long> {

    List<Membresia> findByUsuarioId(Long usuarioId);

    @Modifying
    @Query("DELETE FROM Membresia m WHERE m.usuario.id = :usuarioId")
    void eliminarPorUsuarioId(@Param("usuarioId") Long usuarioId);

    @Query("SELECT COUNT(m) FROM Membresia m WHERE m.estado = :estado")
    long countByEstado(@Param("estado") EstadoMembresia estado);

    Optional<Membresia> findByUsuarioAndEstado(Usuario usuario, EstadoMembresia estado);

    List<Membresia> findByUsuarioOrderByFechaFinDesc(Usuario usuario);

    @Modifying
    @Query("UPDATE Membresia m SET m.estado = FitGO.utp.edu.pe.entity.EstadoMembresia.VENCIDA WHERE m.fechaFin < :hoy AND m.estado != FitGO.utp.edu.pe.entity.EstadoMembresia.VENCIDA")
    void actualizarVencidas(@Param("hoy") java.time.LocalDate hoy);

    @Modifying
    @Query("UPDATE Membresia m SET m.estado = FitGO.utp.edu.pe.entity.EstadoMembresia.POR_VENCER WHERE m.fechaFin >= :hoy AND m.fechaFin <= :limite AND m.estado != FitGO.utp.edu.pe.entity.EstadoMembresia.POR_VENCER")
    void actualizarPorVencer(@Param("hoy") java.time.LocalDate hoy, @Param("limite") java.time.LocalDate limite);

    @Modifying
    @Query("UPDATE Membresia m SET m.estado = FitGO.utp.edu.pe.entity.EstadoMembresia.ACTIVA WHERE m.fechaFin > :limite AND m.estado != FitGO.utp.edu.pe.entity.EstadoMembresia.ACTIVA")
    void actualizarActivas(@Param("limite") java.time.LocalDate limite);
}
