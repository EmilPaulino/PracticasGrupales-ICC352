package main.util;

import io.javalin.http.Context;
import main.models.Usuario;

public class Filters {

    public static void usuarioLogueado(Context ctx) {

        Usuario usuario = ctx.sessionAttribute("usuario");

        if (usuario == null) {
            ctx.redirect("/login");
        }

    }

    public static void organizadorOAdmin(Context ctx) {

        Usuario usuario = ctx.sessionAttribute("usuario");

        if (usuario == null) {
            ctx.redirect("/login");
            return;
        }

        String rol = usuario.getRol().getRol();

        if (!rol.equals("Admin") && !rol.equals("Organizador")) {
            ctx.redirect("/html/403.html");
        }

    }

    public static void soloAdmin(Context ctx) {

        Usuario usuario = ctx.sessionAttribute("usuario");

        if (usuario == null) {
            ctx.redirect("/login");
            return;
        }

        if (!usuario.getRol().getRol().equals("Admin")) {
            ctx.redirect("/html/403.html");
        }

    }
}