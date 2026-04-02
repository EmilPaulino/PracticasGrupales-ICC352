package main.entidades;

import dev.morphia.annotations.Embedded;

@Embedded
public class UsuarioEmbebido {

    private String nombre;
    private String username;
    private Rol rol;

    public UsuarioEmbebido() {
    }

    public UsuarioEmbebido(Usuario usuario) {
        this.nombre = usuario.getNombre();
        this.username = usuario.getUsername();
        this.rol = usuario.getRol();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String u) {
        this.username = u;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
}