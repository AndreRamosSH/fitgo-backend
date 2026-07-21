package FitGO.utp.edu.pe.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "rutinas_ejercicios")
public class RutinaEjercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rutina_ejercicio")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "rutina_id", nullable = false)
    private Rutina rutina;

    @Column(name = "ejercicio_id", nullable = false)
    private String ejercicioId; // ID del CDN (Ej. "biceps/barbell-curl")

    @Column(nullable = false)
    private String nombre; // Nombre del ejercicio

    @Column(nullable = false)
    private Integer orden; // Posición/orden en la rutina

    @Column(nullable = false)
    private Integer series;

    @Column(nullable = false)
    private Integer reps;

    @Column(nullable = false)
    private String peso; // Ej. "50kg" o "Corporal"

    @Column(nullable = false)
    private Integer descanso; // Segundos de descanso

    public RutinaEjercicio() {
    }

    public RutinaEjercicio(Long id, Rutina rutina, String ejercicioId, String nombre, Integer orden, Integer series, Integer reps, String peso, Integer descanso) {
        this.id = id;
        this.rutina = rutina;
        this.ejercicioId = ejercicioId;
        this.nombre = nombre;
        this.orden = orden;
        this.series = series;
        this.reps = reps;
        this.peso = peso;
        this.descanso = descanso;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Rutina getRutina() {
        return rutina;
    }

    public void setRutina(Rutina rutina) {
        this.rutina = rutina;
    }

    public String getEjercicioId() {
        return ejercicioId;
    }

    public void setEjercicioId(String ejercicioId) {
        this.ejercicioId = ejercicioId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public Integer getSeries() {
        return series;
    }

    public void setSeries(Integer series) {
        this.series = series;
    }

    public Integer getReps() {
        return reps;
    }

    public void setReps(Integer reps) {
        this.reps = reps;
    }

    public String getPeso() {
        return peso;
    }

    public void setPeso(String peso) {
        this.peso = peso;
    }

    public Integer getDescanso() {
        return descanso;
    }

    public void setDescanso(Integer descanso) {
        this.descanso = descanso;
    }
}
