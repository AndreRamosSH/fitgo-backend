package FitGO.utp.edu.pe.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_entrenamientos")
public class EntrenamientoRealizado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_entrenamiento")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario; // El miembro que realiza el entrenamiento

    @ManyToOne
    @JoinColumn(name = "rutina_id", nullable = true)
    private Rutina rutina; // Rutina de referencia (opcional si es entreno libre)

    @Column(name = "rutina_nombre", nullable = false)
    private String rutinaNombre; // Nombre de la rutina realizada

    @Column(nullable = false)
    private LocalDateTime fecha; // Fecha y hora de finalización

    @Column(name = "duracion_segundos", nullable = false)
    private Integer duracionSegundos;

    @Column(name = "ejercicios_completados", nullable = false)
    private Integer ejerciciosCompletados;

    @Column(name = "total_series_completadas", nullable = false)
    private Integer totalSeriesCompletadas;

    @Column(name = "volumen_total_kg", nullable = false)
    private Double volumenTotalKg; // Volumen total levantado (reps * peso)

    public EntrenamientoRealizado() {
    }

    public EntrenamientoRealizado(Long id, Usuario usuario, Rutina rutina, String rutinaNombre, LocalDateTime fecha, Integer duracionSegundos, Integer ejerciciosCompletados, Integer totalSeriesCompletadas, Double volumenTotalKg) {
        this.id = id;
        this.usuario = usuario;
        this.rutina = rutina;
        this.rutinaNombre = rutinaNombre;
        this.fecha = fecha;
        this.duracionSegundos = duracionSegundos;
        this.ejerciciosCompletados = ejerciciosCompletados;
        this.totalSeriesCompletadas = totalSeriesCompletadas;
        this.volumenTotalKg = volumenTotalKg;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Rutina getRutina() {
        return rutina;
    }

    public void setRutina(Rutina rutina) {
        this.rutina = rutina;
    }

    public String getRutinaNombre() {
        return rutinaNombre;
    }

    public void setRutinaNombre(String rutinaNombre) {
        this.rutinaNombre = rutinaNombre;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Integer getDuracionSegundos() {
        return duracionSegundos;
    }

    public void setDuracionSegundos(Integer duracionSegundos) {
        this.duracionSegundos = duracionSegundos;
    }

    public Integer getEjerciciosCompletados() {
        return ejerciciosCompletados;
    }

    public void setEjerciciosCompletados(Integer ejerciciosCompletados) {
        this.ejerciciosCompletados = ejerciciosCompletados;
    }

    public Integer getTotalSeriesCompletadas() {
        return totalSeriesCompletadas;
    }

    public void setTotalSeriesCompletadas(Integer totalSeriesCompletadas) {
        this.totalSeriesCompletadas = totalSeriesCompletadas;
    }

    public Double getVolumenTotalKg() {
        return volumenTotalKg;
    }

    public void setVolumenTotalKg(Double volumenTotalKg) {
        this.volumenTotalKg = volumenTotalKg;
    }
}
