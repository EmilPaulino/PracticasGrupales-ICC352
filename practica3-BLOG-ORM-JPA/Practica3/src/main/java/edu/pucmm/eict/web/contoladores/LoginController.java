package edu.pucmm.eict.web.contoladores;

import edu.pucmm.eict.web.entidades.Usuario;
import edu.pucmm.eict.web.servicios.UsuarioService;
import io.javalin.http.Context;

public class LoginController {

    private UsuarioService usuarioService = UsuarioService.getInstancia();

    public void mostrarLogin(Context ctx) {
        ctx.redirect("/html/login.html");
    }

    public void procesarLogin(Context ctx) {
        String username = ctx.formParam("username");
        String password = ctx.formParam("password");

        Usuario user = usuarioService.validarLogin(username, password);

        if (user != null) {
            ctx.sessionAttribute("user", user);
            ctx.redirect("/");
            return;
        }

        ctx.req().getSession(true);
        ctx.sessionAttribute("loginError", "Usuario o contraseña incorrectos");
        ctx.redirect("/login");
    }

    public void logout(Context ctx) {
        ctx.req().getSession().invalidate();
        ctx.redirect("/");
    }
}