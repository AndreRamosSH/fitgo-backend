package FitGO.utp.edu.pe.repository;

import FitGO.utp.edu.pe.entity.Rutina;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RutinaRepository extends JpaRepository<Rutina, Long> {
    List<Rutina> findByMiembroId(Long miembroId);
    List<Rutina> findByMiembroCorreo(String correo);
    List<Rutina> findByCreadorId(Long creadorId);
    List<Rutina> findByCreadorIdAndTipo(Long creadorId, String tipo);
    Optional<Rutina> findByCreadorIdAndMiembroIdAndNombre(Long creadorId, Long miembroId, String nombre);
    void deleteByCreadorIdAndMiembroIdAndNombre(Long creadorId, Long miembroId, String nombre);
}
