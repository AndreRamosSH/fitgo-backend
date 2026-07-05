package FitGO.utp.edu.pe.dto;

public class AsistenciaRequest {
    private String correo;

    public AsistenciaRequest() {
    }

    public AsistenciaRequest(String correo) {
        this.correo = correo;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
