package FitGO.utp.edu.pe.repository;

import FitGO.utp.edu.pe.entity.Asistencia;
import FitGO.utp.edu.pe.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("DELETE FROM Asistencia a WHERE a.usuario.id = :usuarioId")
    void eliminarPorUsuarioId(@org.springframework.data.repository.query.Param("usuarioId") Long usuarioId);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(a) FROM Asistencia a WHERE a.fechaHora >= :inicio AND a.fechaHora < :fin")
    long countAsistenciasEntre(@org.springframework.data.repository.query.Param("inicio") java.time.LocalDateTime inicio, @org.springframework.data.repository.query.Param("fin") java.time.LocalDateTime fin);
    List<Asistencia> findByUsuarioOrderByFechaHoraDesc(Usuario usuario);
    List<Asistencia> findByFechaHoraBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);
    List<Asistencia> findByUsuarioIdAndFechaHoraBetween(Long usuarioId, java.time.LocalDateTime start, java.time.LocalDateTime end);
}
