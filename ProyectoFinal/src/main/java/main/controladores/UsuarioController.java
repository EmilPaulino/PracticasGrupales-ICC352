package main.controladores;

import io.javalin.http.Context;
import main.entidades.Usuario;
import main.servicios.UsuarioServices;
import main.util.RespuestaDTO;

public class UsuarioController {

    private static final UsuarioServices usuarioServices = UsuarioServices.getInstancia();

    public static void listarUsuarios(Context ctx) {
        ctx.json(RespuestaDTO.ok(usuarioServices.listarUsuarios()));
    }

    public static void getUsuarioByUsername(Context ctx) {
        Usuario u = usuarioServices.getUsuarioByUsername(ctx.pathParam("username"));
        if (u == null) {
            ctx.status(404);
            ctx.json(RespuestaDTO.error("Usuario no encontrado"));
            return;
        }
        ctx.json(RespuestaDTO.ok(u));
    }

    public static void crearUsuario(Context ctx) {
        Usuario creado = usuarioServices.crearUsuario(ctx.bodyAsClass(Usuario.class));
        if (creado == null) {
            ctx.status(400);
            ctx.json(RespuestaDTO.error("El usuario ya existe"));
            return;
        }
        ctx.json(RespuestaDTO.ok(creado));
    }

    public static void actualizarUsuario(Context ctx) {
        Usuario actualizado = usuarioServices.actualizarUsuario(ctx.bodyAsClass(Usuario.class));
        if (actualizado == null) {
            ctx.status(404);
            ctx.json(RespuestaDTO.error("Usuario no encontrado"));
            return;
        }
        ctx.json(RespuestaDTO.ok(actualizado));
    }

    public static void eliminarUsuario(Context ctx) {
        if (!usuarioServices.eliminarUsuario(ctx.pathParam("id"))) {
            ctx.status(404);
            ctx.json(RespuestaDTO.error("Usuario no encontrado"));
            return;
        }
        ctx.json(RespuestaDTO.ok("Usuario eliminado correctamente"));
    }
}