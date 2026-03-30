package main.controladores;

import io.javalin.http.Context;
import main.entidades.Usuario;
import main.servicios.UsuarioServices;
import main.util.RespuestaDTO;

public class AuthController {

    private static final UsuarioServices usuarioServices = UsuarioServices.getInstancia();

    public static void login(Context ctx) {
        Usuario datos   = ctx.bodyAsClass(Usuario.class);
        Usuario usuario = usuarioServices.getUsuarioByUsername(datos.getUsername());

        if (usuario == null || !usuario.getPassword().equals(datos.getPassword())) {
            ctx.status(401);
            ctx.json(RespuestaDTO.error("Credenciales inválidas"));
            return;
        }

        ctx.sessionAttribute("usuario", usuario);
        usuario.setPassword(null);
        ctx.json(RespuestaDTO.ok(usuario));
    }

    public static void logout(Context ctx) {
        ctx.req().getSession().invalidate();
        ctx.json(RespuestaDTO.ok("Sesión cerrada correctamente"));
    }

    public static void usuarioActual(Context ctx) {
        Usuario usuario = ctx.sessionAttribute("usuario");
        if (usuario == null) {
            ctx.status(401);
            ctx.json(RespuestaDTO.error("No hay sesión activa"));
            return;
        }
        usuario.setPassword(null);
        ctx.json(RespuestaDTO.ok(usuario));
    }
}