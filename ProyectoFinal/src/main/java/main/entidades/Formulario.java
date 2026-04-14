package main.entidades;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dev.morphia.annotations.*;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity("formularios")
public class Formulario {

    @Id
    private ObjectId id;

    @Property("nombre")
    private String nombre;

    @Property("sector")
    private String sector;

    @Property("nivelEscolar")
    private NivelEscolar nivelEscolar;

    @Property("usuario")
    private UsuarioEmbebido usuario;

    private Ubicacion ubicacion;

    @Property("fotoBase64")
    private String fotoBase64;

    @Property("fechaRegistro")
    private java.util.Date fechaRegistro;

    public Formulario() {
    }

    public Formulario(String nombre, String sector, NivelEscolar nivelEscolar,
                      UsuarioEmbebido usuario, Ubicacion ubicacion, String fotoBase64) {
        this.nombre = nombre;
        this.sector = sector;
        this.nivelEscolar = nivelEscolar;
        this.usuario = usuario;
        this.ubicacion = ubicacion;
        this.fotoBase64 = fotoBase64;
        this.fechaRegistro = new java.util.Date();
    }

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

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public NivelEscolar getNivelEscolar() {
        return nivelEscolar;
    }

    public void setNivelEscolar(NivelEscolar n) {
        this.nivelEscolar = n;
    }

    public UsuarioEmbebido getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioEmbebido usuario) {
        this.usuario = usuario;
    }

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getFotoBase64() {
        return fotoBase64;
    }

    public void setFotoBase64(String fotoBase64) {
        this.fotoBase64 = fotoBase64;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date f) {
        this.fechaRegistro = f;
    }
}