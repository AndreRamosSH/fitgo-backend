package FitGO.utp.edu.pe.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "rutinas")
public class Rutina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rutina")
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String dias; // Ej. "Lun, Mié, Vie" o "Mar, Jue"

    @Column(nullable = false, length = 20)
    private String tipo; // "PROPIA" o "ASIGNADA"

    @ManyToOne
    @JoinColumn(name = "creador_id")
    private Usuario creador; // Entrenador o Miembro que la diseñó

    @ManyToOne
    @JoinColumn(name = "miembro_id", nullable = false)
    private Usuario miembro; // Miembro al que le pertenece/fue asignada

    public Rutina() {
    }

    public Rutina(Long id, String nombre, String dias, String tipo, Usuario creador, Usuario miembro) {
        this.id = id;
        this.nombre = nombre;
        this.dias = dias;
        this.tipo = tipo;
        this.creador = creador;
        this.miembro = miembro;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDias() {
        return dias;
    }

    public void setDias(String dias) {
        this.dias = dias;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Usuario getCreador() {
        return creador;
    }

    public void setCreador(Usuario creador) {
        this.creador = creador;
    }

    public Usuario getMiembro() {
        return miembro;
    }

    public void setMiembro(Usuario miembro) {
        this.miembro = miembro;
    }
}
