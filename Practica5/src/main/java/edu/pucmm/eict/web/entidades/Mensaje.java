package edu.pucmm.eict.web.entidades;

import java.time.LocalDateTime;

public class Mensaje {

    private String remitente;
    private String contenido;
    private String tipo;
    private LocalDateTime fecha;

    public Mensaje(String remitente, String contenido, String tipo) {
        this.remitente = remitente;
        this.contenido = contenido;
        this.tipo = tipo;
        this.fecha = LocalDateTime.now();
    }

    public String getRemitente() { return remitente; }
    public void   setRemitente(String remitente) { this.remitente = remitente; }

    public String getContenido() { return contenido; }
    public void   setContenido(String contenido) { this.contenido = contenido; }

    public String getTipo() { return tipo; }
    public void   setTipo(String tipo) { this.tipo = tipo; }

    public LocalDateTime getFecha() { return fecha; }
}