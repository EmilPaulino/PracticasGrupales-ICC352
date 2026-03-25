package edu.pucmm.eict.web.entidades;

import org.eclipse.jetty.websocket.api.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Conversacion {

    private String id;
    private String nombreUsuario;
    private String foto;

    private Session sessionUsuario;
    private Session sessionAdmin;

    private List<Mensaje> mensajes;

    public Conversacion(String nombreUsuario, String foto, Usuario usuario) {

        this.id = UUID.randomUUID().toString();
        this.nombreUsuario = nombreUsuario;
        this.foto = foto;

        this.mensajes = new ArrayList<>();
    }

    public void agregarMensaje(Mensaje mensaje) {
        mensajes.add(mensaje);
    }

    public boolean isAdminConectado() {
        return sessionAdmin != null && sessionAdmin.isOpen();
    }

    public String getId() {
        return id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getFoto() {
        return foto;
    }

    public Session getSessionUsuario() {
        return sessionUsuario;
    }

    public void setSessionUsuario(Session sessionUsuario) {
        this.sessionUsuario = sessionUsuario;
    }

    public Session getSessionAdmin() {
        return sessionAdmin;
    }

    public void setSessionAdmin(Session sessionAdmin) {
        this.sessionAdmin = sessionAdmin;
    }

    public List<Mensaje> getMensajes() {
        return mensajes;
    }
}