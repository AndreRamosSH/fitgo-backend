package FitGO.utp.edu.pe.dto;

import FitGO.utp.edu.pe.entity.Rol;

public class RegistroRequest {
    private String nombre;
    private String apellido;
    private String correo;
    private String password;
    private Rol rol;
    private String telefono;
    private FitGO.utp.edu.pe.entity.Turno turno;
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
