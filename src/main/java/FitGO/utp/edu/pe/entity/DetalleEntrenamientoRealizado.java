package FitGO.utp.edu.pe.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "detalles_entrenamientos_realizados")
public class DetalleEntrenamientoRealizado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_entrenamiento")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "entrenamiento_id", nullable = false)
    private EntrenamientoRealizado entrenamiento;

    @Column(name = "ejercicio_id", nullable = false)
    private String ejercicioId; // ID del CDN (Ej. "biceps/barbell-curl")

    @Column(name = "ejercicio_nombre", nullable = false)
    private String ejercicioNombre; // Nombre del ejercicio

    @Column(name = "serie_numero", nullable = false)
    private Integer serieNumero; // Nº de la serie (1, 2, 3, etc.)

    @Column(nullable = false)
    private Integer reps;

    @Column(nullable = false)
    private Double peso; // Peso levantado en kg (0 para peso corporal)

    public DetalleEntrenamientoRealizado() {
    }

    public DetalleEntrenamientoRealizado(Long id, EntrenamientoRealizado entrenamiento, String ejercicioId, String ejercicioNombre, Integer serieNumero, Integer reps, Double peso) {
        this.id = id;
        this.entrenamiento = entrenamiento;
        this.ejercicioId = ejercicioId;
        this.ejercicioNombre = ejercicioNombre;
        this.serieNumero = serieNumero;
        this.reps = reps;
        this.peso = peso;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EntrenamientoRealizado getEntrenamiento() {
        return entrenamiento;
    }

    public void setEntrenamiento(EntrenamientoRealizado entrenamiento) {
        this.entrenamiento = entrenamiento;
    }

    public String getEjercicioId() {
        return ejercicioId;
    }

    public void setEjercicioId(String ejercicioId) {
        this.ejercicioId = ejercicioId;
    }

    public String getEjercicioNombre() {
        return ejercicioNombre;
    }

    public void setEjercicioNombre(String ejercicioNombre) {
        this.ejercicioNombre = ejercicioNombre;
    }

    public Integer getSerieNumero() {
        return serieNumero;
    }

    public void setSerieNumero(Integer serieNumero) {
        this.serieNumero = serieNumero;
    }

    public Integer getReps() {
        return reps;
    }

    public void setReps(Integer reps) {
        this.reps = reps;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }
}
