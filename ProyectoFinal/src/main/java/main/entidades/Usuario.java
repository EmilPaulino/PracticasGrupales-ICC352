package main.entidades;

import dev.morphia.annotations.*;
import org.bson.types.ObjectId;

@Entity("usuarios")
public class Usuario {

    @Id
    private ObjectId id;

    @Property("username")
    private String username;

    @Property("password")
    private String password;   // almacenar con hash (BCrypt)

    @Property("rol")
    private Rol rol;

    public Usuario() {
    }

    public Usuario(String username, String password, Rol rol) {
        this.username = username;
        this.password = password;
        this.rol = rol;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String u) {
        this.username = u;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String p) {
        this.password = p;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
}