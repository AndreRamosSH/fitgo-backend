package FitGO.utp.edu.pe.repository;

import FitGO.utp.edu.pe.entity.DetalleEntrenamientoRealizado;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DetalleEntrenamientoRealizadoRepository extends JpaRepository<DetalleEntrenamientoRealizado, Long> {
    List<DetalleEntrenamientoRealizado> findByEntrenamientoId(Long entrenamientoId);
}
