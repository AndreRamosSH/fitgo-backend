package FitGO.utp.edu.pe.dto;

public class RutinaEjercicioRequest {
    private String ejercicioId;
    private String nombre;
    private int series;
    private int reps;
    private String peso;
    private int descanso;

    public RutinaEjercicioRequest() {
    }

    public RutinaEjercicioRequest(String ejercicioId, String nombre, int series, int reps, String peso, int descanso) {
        this.ejercicioId = ejercicioId;
        this.nombre = nombre;
        this.series = series;
        this.reps = reps;
        this.peso = peso;
        this.descanso = descanso;
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

    public int getSeries() {
        return series;
    }

    public void setSeries(int series) {
        this.series = series;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public String getPeso() {
        return peso;
    }

    public void setPeso(String peso) {
        this.peso = peso;
    }

    public int getDescanso() {
        return descanso;
    }

    public void setDescanso(int descanso) {
        this.descanso = descanso;
    }
}
