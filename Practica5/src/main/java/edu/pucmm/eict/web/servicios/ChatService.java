package edu.pucmm.eict.web.servicios;

import edu.pucmm.eict.web.entidades.Conversacion;
import edu.pucmm.eict.web.entidades.Mensaje;
import edu.pucmm.eict.web.entidades.Usuario;
import org.eclipse.jetty.websocket.api.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatService {

    private static final ChatService instancia = new ChatService();

    private final Map<String, Conversacion> conversaciones = new ConcurrentHashMap<>();
    private final Map<String, Session> adminsSuscriptos = new ConcurrentHashMap<>();

    public static ChatService getInstancia() {
        return instancia;
    }

    public Conversacion crearConversacion(String nombre, String foto, Usuario usuario, Session sessionUsuario) {
        Conversacion conv = new Conversacion(nombre, foto, usuario);
        conv.setSessionUsuario(sessionUsuario);
        conversaciones.put(conv.getId(), conv);
        notificarAdmins(); // ← nueva conversación aparece en la lista
        return conv;
    }

    public void conectarAdmin(String id, Session sessionAdmin) {
        Conversacion conv = conversaciones.get(id);
        if (conv == null) return;
        conv.setSessionAdmin(sessionAdmin);
        notificarAdmins(); // ← cambia el badge "Atendiendo"
        System.out.println("Admin conectado al chat: " + id);
    }

    public void desconectarAdmin(String id) {
        Conversacion conv = conversaciones.get(id);
        if (conv != null) {
            conv.setSessionAdmin(null);
            notificarAdmins(); // ← se quita el badge "Atendiendo"
            System.out.println("Admin desconectado del chat: " + id);
        }
    }

    public void enviarMensajeUsuario(String id, String texto) {
        Conversacion conv = conversaciones.get(id);
        if (conv == null) return;
        Mensaje msg = new Mensaje(conv.getNombreUsuario(), texto, "usuario");
        conv.agregarMensaje(msg);
        enviar(conv.getSessionAdmin(), msg);
        notificarAdmins(); // ← actualiza el contador de mensajes
    }

    public void enviarMensajeAdmin(String id, String texto) {
        Conversacion conv = conversaciones.get(id);
        if (conv == null) return;
        Mensaje msg = new Mensaje("Admin", texto, "admin");
        conv.agregarMensaje(msg);
        enviar(conv.getSessionUsuario(), msg);
        notificarAdmins(); // ← actualiza el contador de mensajes
    }

    private void enviar(Session session, Mensaje msg) {
        if (session == null || !session.isOpen()) return;
        try {
            String json = "{" +
                    "\"remitente\":\"" + msg.getRemitente() + "\"," +
                    "\"contenido\":\"" + msg.getContenido() + "\"," +
                    "\"tipo\":\"" + msg.getTipo() + "\"" +
                    "}";
            session.getRemote().sendString(json);
        } catch (Exception e) {
            System.out.println("Error enviando mensaje");
        }
    }

    public Conversacion buscarPorId(String id) {
        return conversaciones.get(id);
    }

    public List<Conversacion> listar() {
        return new ArrayList<>(conversaciones.values());
    }

    public void cerrarConversacion(String id) {
        Conversacion conv = conversaciones.remove(id);
        if (conv == null) return;

        notificarAdmins(); // ← se notifica ANTES de cerrar las sesiones

        try {
            if (conv.getSessionUsuario() != null) {
                conv.getSessionUsuario().close();
            }
            if (conv.getSessionAdmin() != null) {
                conv.getSessionAdmin().close();
            }
        } catch (Exception e) {
            System.out.println("Error cerrando chat");
        }
    }

    public void suscribirAdminLista(String key, Session session) {
        adminsSuscriptos.put(key, session);
    }

    public void desuscribirAdminLista(String key) {
        adminsSuscriptos.remove(key);
    }

    private void notificarAdmins() {
        try {
            StringBuilder sb = new StringBuilder("[");
            List<Conversacion> lista = listar();
            for (int i = 0; i < lista.size(); i++) {
                Conversacion c = lista.get(i);
                sb.append("{")
                        .append("\"id\":\"").append(c.getId()).append("\",")
                        .append("\"nombreUsuario\":\"").append(c.getNombreUsuario()).append("\",")
                        .append("\"mensajes\":").append(c.getMensajes().size()).append(",")
                        .append("\"adminConectado\":").append(c.getSessionAdmin() != null && c.getSessionAdmin().isOpen())
                        .append("}");
                if (i < lista.size() - 1) sb.append(",");
            }
            sb.append("]");
            String json = sb.toString();

            for (Session s : adminsSuscriptos.values()) {
                if (s != null && s.isOpen()) {
                    s.getRemote().sendString(json);
                }
            }
        } catch (Exception e) {
            System.out.println("Error notificando admins: " + e.getMessage());
        }
    }
}