package edu.pucmm.eict.web.contoladores;

import edu.pucmm.eict.web.entidades.Usuario;
import edu.pucmm.eict.web.servicios.UsuarioService;
import edu.pucmm.eict.web.util.EncryptUtil;
import edu.pucmm.eict.web.util.LoginLogger;
import io.javalin.http.Context;

public class LoginController {

    private UsuarioService usuarioService = UsuarioService.getInstancia();

    public void mostrarLogin(Context ctx) {
        ctx.redirect("/html/login.html");
    }

    public void procesarLogin(Context ctx) {
        String username = ctx.formParam("username");
        String password = ctx.formParam("password");
        boolean remember = ctx.formParam("remember") != null;

        Usuario user = usuarioService.validarLogin(username, password);

        if (user != null) {

            ctx.sessionAttribute("user", user);

            if (remember) {
                String encrypted = EncryptUtil.encrypt(user.getUsername());
                ctx.cookie("rememberMe", encrypted, 7 * 24 * 60 * 60); // 1 semana
            }

            // Registrar login en Cockroach (punto 7)
            LoginLogger.log(user.getUsername());

            ctx.redirect("/");

        } else {
            ctx.result("Credenciales incorrectas");
        }
    }

    public void logout(Context ctx) {
        ctx.req().getSession().invalidate();
        ctx.removeCookie("rememberMe"); // invalida cookie

        ctx.redirect("/login");
    }
}