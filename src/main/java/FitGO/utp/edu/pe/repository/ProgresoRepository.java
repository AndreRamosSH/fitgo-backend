package FitGO.utp.edu.pe.repository;

import FitGO.utp.edu.pe.entity.Progreso;
import FitGO.utp.edu.pe.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProgresoRepository extends JpaRepository<Progreso, Long> {
   
    Optional<Progreso> findTopByUsuarioOrderByFechaRegistroDesc(Usuario usuario);
    java.util.List<Progreso> findByUsuarioOrderByFechaRegistroAsc(Usuario usuario);
}