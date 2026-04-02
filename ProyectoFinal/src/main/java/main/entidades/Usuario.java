package main.entidades;

import dev.morphia.annotations.*;
import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity("usuarios")
public class Usuario {

    @Id
    private ObjectId id;

    @Property("nombre")
    private String nombre;

    @Property("username")
    private String username;

    @Property("password")
    private String password;

    @Property("rol")
    private Rol rol;

    public Usuario() {
    }

    public Usuario(String username, String nombre, String password, Rol rol) {
        this.nombre = nombre;
        this.username = username;
        this.password = password;
        this.rol = rol;
    }


    @JsonProperty("id")
    public String getIdString() {

        if(id == null)
            return null;

        return id.toString();

    }


    @JsonIgnore // evita duplicar el id
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
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