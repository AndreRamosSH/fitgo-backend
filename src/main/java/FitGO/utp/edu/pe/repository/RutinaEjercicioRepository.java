package FitGO.utp.edu.pe.repository;

import FitGO.utp.edu.pe.entity.RutinaEjercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RutinaEjercicioRepository extends JpaRepository<RutinaEjercicio, Long> {
    List<RutinaEjercicio> findByRutinaIdOrderByOrdenAsc(Long rutinaId);
    void deleteByRutinaId(Long rutinaId);
}
