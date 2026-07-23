package FitGO.utp.edu.pe.dto;

import jakarta.validation.constraints.*;

public class MetricasDTO {
    @NotNull(message = "El peso es obligatorio")
    @DecimalMin(value = "10.0", message = "El peso debe ser mayor o igual a 10 kg")
    @DecimalMax(value = "500.0", message = "El peso debe ser menor o igual a 500 kg")
    private Double peso;

    @NotNull(message = "La altura es obligatoria")
    @DecimalMin(value = "0.50", message = "La altura debe ser mayor o igual a 0.50 metros")
    @DecimalMax(value = "2.80", message = "La altura debe ser menor o igual a 2.80 metros")
    private Double altura;

    private Double imc;

    private java.time.LocalDate fechaNacimiento;
    private String sexo;
    private Double pesoObjetivo;
    private Double grasaObjetivo;

    public MetricasDTO() {
    }

    public MetricasDTO(Double peso, Double altura, Double imc) {
        this.peso = peso;
        this.altura = altura;
        this.imc = imc;
    }

    public MetricasDTO(Double peso, Double altura, Double imc, java.time.LocalDate fechaNacimiento, String sexo) {
        this.peso = peso;
        this.altura = altura;
        this.imc = imc;
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
    }

    public MetricasDTO(Double peso, Double altura, Double imc, java.time.LocalDate fechaNacimiento, String sexo, Double pesoObjetivo, Double grasaObjetivo) {
        this.peso = peso;
        this.altura = altura;
        this.imc = imc;
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
        this.pesoObjetivo = pesoObjetivo;
        this.grasaObjetivo = grasaObjetivo;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }

    public Double getAltura() {
        return altura;
    }

    public void setAltura(Double altura) {
        this.altura = altura;
    }

    public Double getImc() {
        return imc;
    }

    public void setImc(Double imc) {
        this.imc = imc;
    }

    public java.time.LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(java.time.LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public Double getPesoObjetivo() {
        return pesoObjetivo;
    }

    public void setPesoObjetivo(Double pesoObjetivo) {
        this.pesoObjetivo = pesoObjetivo;
    }

    public Double getGrasaObjetivo() {
        return grasaObjetivo;
    }

    public void setGrasaObjetivo(Double grasaObjetivo) {
        this.grasaObjetivo = grasaObjetivo;
    }
}
