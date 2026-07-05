package FitGO.utp.edu.pe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import FitGO.utp.edu.pe.entity.Plan;

public interface PlanRepository extends JpaRepository<Plan, Long> {

    boolean existsByNombre(String nombre);
}
