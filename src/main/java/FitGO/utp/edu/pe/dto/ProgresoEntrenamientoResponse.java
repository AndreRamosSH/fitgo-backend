package FitGO.utp.edu.pe.dto;

import java.util.List;

public class ProgresoEntrenamientoResponse {
    private Double pesoActual;
    private long entrenosTotales;
    private long entrenosEsteMes;
    private long tiempoTotalHoras;
    private long tiempoPromedioMinutos;
    private long seriesTotales;
    private double seriesPorcentajeVariacion;
    private long rachaActual;
    private List<Integer> semanas;
    private List<DiaCalendarioDTO> calendario;

    public ProgresoEntrenamientoResponse() {
    }

    public ProgresoEntrenamientoResponse(Double pesoActual, long entrenosTotales, long entrenosEsteMes, long tiempoTotalHoras, long tiempoPromedioMinutos, long seriesTotales, double seriesPorcentajeVariacion, long rachaActual, List<Integer> semanas, List<DiaCalendarioDTO> calendario) {
        this.pesoActual = pesoActual;
        this.entrenosTotales = entrenosTotales;
        this.entrenosEsteMes = entrenosEsteMes;
        this.tiempoTotalHoras = tiempoTotalHoras;
        this.tiempoPromedioMinutos = tiempoPromedioMinutos;
        this.seriesTotales = seriesTotales;
        this.seriesPorcentajeVariacion = seriesPorcentajeVariacion;
        this.rachaActual = rachaActual;
        this.semanas = semanas;
        this.calendario = calendario;
    }

    public Double getPesoActual() {
        return pesoActual;
    }

    public void setPesoActual(Double pesoActual) {
        this.pesoActual = pesoActual;
    }

    public long getEntrenosTotales() {
        return entrenosTotales;
    }

    public void setEntrenosTotales(long entrenosTotales) {
        this.entrenosTotales = entrenosTotales;
    }

    public long getEntrenosEsteMes() {
        return entrenosEsteMes;
    }

    public void setEntrenosEsteMes(long entrenosEsteMes) {
        this.entrenosEsteMes = entrenosEsteMes;
    }

    public long getTiempoTotalHoras() {
        return tiempoTotalHoras;
    }

    public void setTiempoTotalHoras(long tiempoTotalHoras) {
        this.tiempoTotalHoras = tiempoTotalHoras;
    }

    public long getTiempoPromedioMinutos() {
        return tiempoPromedioMinutos;
    }

    public void setTiempoPromedioMinutos(long tiempoPromedioMinutos) {
        this.tiempoPromedioMinutos = tiempoPromedioMinutos;
    }

    public long getSeriesTotales() {
        return seriesTotales;
    }

    public void setSeriesTotales(long seriesTotales) {
        this.seriesTotales = seriesTotales;
    }

    public double getSeriesPorcentajeVariacion() {
        return seriesPorcentajeVariacion;
    }

    public void setSeriesPorcentajeVariacion(double seriesPorcentajeVariacion) {
        this.seriesPorcentajeVariacion = seriesPorcentajeVariacion;
    }

    public long getRachaActual() {
        return rachaActual;
    }

    public void setRachaActual(long rachaActual) {
        this.rachaActual = rachaActual;
    }

    public List<Integer> getSemanas() {
        return semanas;
    }

    public void setSemanas(List<Integer> semanas) {
        this.semanas = semanas;
    }

    public List<DiaCalendarioDTO> getCalendario() {
        return calendario;
    }

    public void setCalendario(List<DiaCalendarioDTO> calendario) {
        this.calendario = calendario;
    }
}
