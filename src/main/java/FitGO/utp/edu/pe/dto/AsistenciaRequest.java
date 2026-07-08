package FitGO.utp.edu.pe.dto;

import jakarta.validation.constraints.*;

public class AsistenciaRequest {
    @NotBlank(message = "El correo del miembro es obligatorio")
    @Email(message = "El formato de correo es inválido")
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
