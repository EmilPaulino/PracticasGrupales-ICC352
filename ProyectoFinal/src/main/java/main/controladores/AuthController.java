package main.controladores;

import io.javalin.http.Context;

import main.entidades.Usuario;
import main.entidades.Rol;
import main.servicios.UsuarioService;
import main.util.EncryptUtil;

public class AuthController {

    private static final UsuarioService usuarioService = UsuarioService.getInstancia();

    public static void login(Context ctx) {
        String username = ctx.formParam("username");
        String password = ctx.formParam("password");

        if (username == null || password == null) {
            ctx.attribute("error", true);
            ctx.render("/templates/login/login.html");
            return;
        }

        Usuario usuario = usuarioService.findByUsername(username);

        if (usuario == null || !password.equals(usuario.getPassword())) {
            ctx.attribute("error", true);
            ctx.render("/templates/login/login.html");
            return;
        }

        ctx.sessionAttribute("username", usuario.getUsername());
        ctx.sessionAttribute("nombre", usuario.getNombre());
        boolean remember = ctx.formParam("remember") != null;
        if (remember) {
            String encrypted = EncryptUtil.encrypt(usuario.getUsername());
            ctx.cookie("rememberMe", encrypted, 7 * 24 * 60 * 60);
        }

        if (usuario.getRol() == Rol.ADMIN) {
            ctx.redirect("/admin/panel");
        } else {
            ctx.redirect("/");
        }
    }

    public static void logout(Context ctx) {
        ctx.req().getSession().invalidate();
        ctx.removeCookie("rememberMe");
        ctx.redirect("/login");
    }

    public static void mostrarLogin(Context ctx) {
        String username = ctx.sessionAttribute("username");
        if (username != null) {
            Usuario usuario = usuarioService.findByUsername(username);
            if (usuario.getRol() == Rol.ADMIN) {
                ctx.redirect("/admin/panel");
            } else {
                ctx.redirect("/");
            }
            return;
        }
        ctx.render("/templates/login/login.html");
    }

}