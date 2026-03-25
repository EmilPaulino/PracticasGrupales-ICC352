package edu.pucmm.eict.web.contoladores;

import edu.pucmm.eict.web.entidades.Usuario;
import edu.pucmm.eict.web.servicios.ChatService;
import io.javalin.http.Context;

public class ChatController {

    private final ChatService chatService = ChatService.getInstancia();

    // ─── Helper de autenticación ──────────────────────────────────────────────

    /**
     * Verifica que el usuario tenga sesión activa y sea admin o autor.
     */
    private boolean verificarAcceso(Context ctx) {
        Usuario user = ctx.sessionAttribute("user");

        if (user == null) {
            ctx.redirect("/login");
            return false;
        }

        if (!user.isAdministrator() && !user.isAutor()) {
            ctx.redirect("/");
            return false;
        }

        return true;
    }

    public void listarChats(Context ctx) {
        if (!verificarAcceso(ctx)){
            return;
        }
        ctx.attribute("conversaciones", chatService.listar());
        ctx.render("templates/chats/listarChats.html");
    }

    public void verChat(Context ctx) {
        if (!verificarAcceso(ctx)){
            return;
        }

        String id = ctx.pathParam("id");
        ctx.attribute("conversacion", chatService.buscarPorId(id));
        ctx.render("templates/chats/verChat.html");
    }

    public void cerrarChat(Context ctx) {
        if (!verificarAcceso(ctx)){
            return;
        }

        String id = ctx.pathParam("id");
        chatService.cerrarConversacion(id);
        ctx.redirect("/chats");
    }
}