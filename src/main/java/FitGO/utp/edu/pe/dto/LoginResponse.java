package FitGO.utp.edu.pe.dto;

import FitGO.utp.edu.pe.entity.Rol;

public class LoginResponse {
    private String token;
    private String nombre;
    private String apellido;
    private String correo;
    private Rol rol;
    private String telefono;

    public LoginResponse() {
    }

    public LoginResponse(String token, String nombre, String apellido, String correo, Rol rol, String telefono) {
        this.token = token;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.rol = rol;
        this.telefono = telefono;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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
}
