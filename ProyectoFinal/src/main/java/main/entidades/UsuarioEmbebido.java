package main.entidades;

import dev.morphia.annotations.Embedded;

@Embedded
public class UsuarioEmbebido {

    private String username;
    private Rol rol;

    public UsuarioEmbebido() {
    }

    public UsuarioEmbebido(Usuario usuario) {
        this.username = usuario.getUsername();
        this.rol = usuario.getRol();
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