package FitGO.utp.edu.pe.dto;

import jakarta.validation.constraints.*;

public class MetricasDTO {
    @NotNull(message = "El peso es obligatorio")
    @DecimalMin(value = "10.0", message = "El peso debe ser mayor o igual a 10 kg")
    @DecimalMax(value = "500.0", message = "El peso debe ser menor o igual a 500 kg")
    private Double peso;

    @NotNull(message = "La altura es obligatoria")
    @DecimalMin(value = "50.0", message = "La altura debe ser mayor o igual a 50 cm")
    @DecimalMax(value = "300.0", message = "La altura debe ser menor o igual a 300 cm")
    private Double altura;

    private Double imc;

    public MetricasDTO() {
    }

    public MetricasDTO(Double peso, Double altura, Double imc) {
        this.peso = peso;
        this.altura = altura;
        this.imc = imc;
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
}
