package FitGO.utp.edu.pe.repository;

import FitGO.utp.edu.pe.entity.EntrenamientoRealizado;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EntrenamientoRealizadoRepository extends JpaRepository<EntrenamientoRealizado, Long> {
    List<EntrenamientoRealizado> findByUsuarioCorreoOrderByFechaDesc(String correo);
    List<EntrenamientoRealizado> findByRutinaId(Long rutinaId);
}
