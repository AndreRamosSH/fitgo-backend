package FitGO.utp.edu.pe.dto;

import java.util.List;

public class RutinaRequest {
    private String nombre;
    private String dias;
    private List<RutinaEjercicioRequest> ejercicios;

    public RutinaRequest() {
    }

    public RutinaRequest(String nombre, String dias, List<RutinaEjercicioRequest> ejercicios) {
        this.nombre = nombre;
        this.dias = dias;
        this.ejercicios = ejercicios;
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

    public List<RutinaEjercicioRequest> getEjercicios() {
        return ejercicios;
    }

    public void setEjercicios(List<RutinaEjercicioRequest> ejercicios) {
        this.ejercicios = ejercicios;
    }
}
