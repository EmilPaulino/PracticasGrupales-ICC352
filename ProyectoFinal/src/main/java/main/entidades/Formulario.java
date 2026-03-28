package main.entidades;

import java.time.LocalDateTime;

public class Formulario {

    private String id;
    private String nombre;
    private String sector;
    private NivelEscolar nivelEscolar;
    private Usuario usuario;
    private Ubicacion ubicacion;
    private String fotoBase64;
    private LocalDateTime fechaRegistro;

    public Formulario(){

    }

    public Formulario(String nombre, String sector, NivelEscolar nivelEscolar, Usuario usuario, Ubicacion ubicacion, String fotoBase64){
        this.nombre = nombre;
        this.sector = sector;
        this.nivelEscolar = nivelEscolar;
        this.usuario = usuario;
        this.ubicacion = ubicacion;
        this.fotoBase64 = fotoBase64;
        this.fechaRegistro = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public void setNivelEscolar(NivelEscolar nivelEscolar) {
        this.nivelEscolar = nivelEscolar;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
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

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
