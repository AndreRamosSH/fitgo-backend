package FitGO.utp.edu.pe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import FitGO.utp.edu.pe.entity.Entrenador;

public interface EntrenadorRepository extends JpaRepository<Entrenador, Long> {

    Entrenador findByUsuarioId(Long usuarioId);

    @Modifying
    @Query("DELETE FROM Entrenador e WHERE e.id = :id")
    void eliminarPorId(@Param("id") Long id);
}
