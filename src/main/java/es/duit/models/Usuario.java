package es.duit.models;

import java.sql.Timestamp;
import java.util.Objects;

public class Usuario {

    private int idUsuario;
    private String nombre;
    private String apellidos;
    private String username;
    private String email;
    private String password;
    private String telefono;
    private int idRol;
    private boolean activo;
    private Timestamp fechaRegistro;

    public Usuario() {
    }

    public Usuario(int idUsuario, String nombre, String apellidos, String username, String email, String password, String telefono, int idRol, boolean activo, Timestamp fechaRegistro) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.username = username;
        this.email = email;
        this.password = password;
        this.telefono = telefono;
        this.idRol = idRol;
        this.activo = activo;
        this.fechaRegistro = fechaRegistro;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Timestamp getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Timestamp fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return idUsuario == usuario.idUsuario && idRol == usuario.idRol && activo == usuario.activo && Objects.equals(nombre, usuario.nombre) && Objects.equals(apellidos, usuario.apellidos) && Objects.equals(username, usuario.username) && Objects.equals(email, usuario.email) && Objects.equals(password, usuario.password) && Objects.equals(telefono, usuario.telefono) && Objects.equals(fechaRegistro, usuario.fechaRegistro);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario, nombre, apellidos, username, email, password, telefono, idRol, activo, fechaRegistro);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", nombre='" + nombre + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", telefono='" + telefono + '\'' +
                ", idRol=" + idRol +
                ", activo=" + activo +
                ", fechaRegistro=" + fechaRegistro +
                '}';
    }
}
