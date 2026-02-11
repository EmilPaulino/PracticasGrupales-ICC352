package edu.pucmm.eict.web.contoladores;

import edu.pucmm.eict.web.entidades.Usuario;
import edu.pucmm.eict.web.servicios.UsuarioService;
import io.javalin.http.Context;

public class LoginController {

    private UsuarioService usuarioService = UsuarioService.getInstancia();

    /**
     * Procesa el login enviado desde el formulario.
     * Si las credenciales son correctas, guarda el usuario en sesión y redirige a "/".
     * Si fallan, redirige al login con un mensaje de error.
     */
    public void procesarLogin(Context ctx) {
        String username = ctx.formParam("username");
        String password = ctx.formParam("password");

        // Llamamos al servicio de usuarios para validar login
        Usuario user = usuarioService.validarLogin(username, password);

        if (user != null) {
            // En caso de que el login sea exitoso, guardamos en sesión
            ctx.sessionAttribute("user", user);
            ctx.redirect("/");
        } else {
            // Si el login falla guardamos mensaje de error en sesión
            ctx.sessionAttribute("loginError", "Usuario o contraseña incorrectos");
            ctx.redirect("/login");
        }
    }

    /**
     * Cierra la sesión del usuario actual
     */
    public void logout(Context ctx) {
        ctx.req().getSession().invalidate();
        ctx.redirect("/");
    }

    /**
     * Muestra la página de login (puede pasar mensajes de error)
     */
    public void mostrarLogin(Context ctx) {
        ctx.redirect("/html/login.html");
    }

}
