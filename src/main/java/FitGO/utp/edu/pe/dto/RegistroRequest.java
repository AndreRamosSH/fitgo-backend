package FitGO.utp.edu.pe.dto;

import FitGO.utp.edu.pe.entity.Rol;
import jakarta.validation.constraints.*;

public class RegistroRequest {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, message = "El nombre debe tener al menos 2 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, message = "El apellido debe tener al menos 2 caracteres")
    private String apellido;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El formato de correo es inválido")
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @NotNull(message = "El rol es obligatorio")
    private Rol rol;

    @Pattern(regexp = "^[+0-9\\s]{7,15}$", message = "El número telefónico es inválido")
    private String telefono;

    private FitGO.utp.edu.pe.entity.Turno turno;

    @Min(value = 0, message = "La experiencia no puede ser menor a 0")
    @Max(value = 50, message = "La experiencia no puede ser mayor a 50")
    private Integer experienciaAnios;

    public RegistroRequest() {
    }

    public RegistroRequest(String nombre, String apellido, String correo, String password, Rol rol) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.password = password;
        this.rol = rol;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public FitGO.utp.edu.pe.entity.Turno getTurno() {
        return turno;
    }

    public void setTurno(FitGO.utp.edu.pe.entity.Turno turno) {
        this.turno = turno;
    }

    public Integer getExperienciaAnios() {
        return experienciaAnios;
    }

    public void setExperienciaAnios(Integer experienciaAnios) {
        this.experienciaAnios = experienciaAnios;
    }
}
