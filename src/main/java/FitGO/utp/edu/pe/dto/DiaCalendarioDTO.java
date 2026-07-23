package FitGO.utp.edu.pe.dto;

public class DiaCalendarioDTO {
    private int dia;
    private boolean entreno;
    private boolean esHoy;

    public DiaCalendarioDTO() {
    }

    public DiaCalendarioDTO(int dia, boolean entreno, boolean esHoy) {
        this.dia = dia;
        this.entreno = entreno;
        this.esHoy = esHoy;
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public boolean isEntreno() {
        return entreno;
    }

    public void setEntreno(boolean entreno) {
        this.entreno = entreno;
    }

    public boolean isEsHoy() {
        return esHoy;
    }

    public void setEsHoy(boolean esHoy) {
        this.esHoy = esHoy;
    }
}
