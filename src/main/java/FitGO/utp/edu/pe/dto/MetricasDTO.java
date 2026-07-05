package FitGO.utp.edu.pe.dto;

public class MetricasDTO {

    private Double peso;
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
