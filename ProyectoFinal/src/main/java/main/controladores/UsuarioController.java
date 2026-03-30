package main.controladores;

import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.NotFoundResponse;

import main.entidades.Rol;
import main.entidades.Usuario;
import main.servicios.UsuarioServices;
import main.util.RespuestaDTO;

public class UsuarioController {

    private static final UsuarioServices usuarioServices = UsuarioServices.getInstancia();

    private static Usuario validarAdmin(Context ctx) {
        String username = ctx.attribute("username");
        Usuario usuario = usuarioServices.getUsuarioByUsername(username);
        if (usuario == null || usuario.getRol() != Rol.ADMIN) {
            throw new ForbiddenResponse("Acceso denegado");
        }
        return usuario;
    }

    public static void listarUsuarios(Context ctx) {
        validarAdmin(ctx);
        ctx.json(RespuestaDTO.ok(usuarioServices.listarUsuarios()));
    }

    public static void getUsuarioByUsername(Context ctx) {
        String username = ctx.attribute("username");
        Usuario usuario = usuarioServices.getUsuarioByUsername(username);
        if (usuario == null){
            throw new NotFoundResponse("Usuario no encontrado");
        }
        ctx.json(RespuestaDTO.ok(usuario));
    }

    public static void crearUsuario(Context ctx) {
        validarAdmin(ctx);
        Usuario creado = usuarioServices.crearUsuario(ctx.bodyAsClass(Usuario.class));
        if (creado == null){
            throw new ForbiddenResponse("El usuario ya existe");
        }
        ctx.status(201).json(RespuestaDTO.ok(creado));
    }

    public static void actualizarUsuario(Context ctx) {
        validarAdmin(ctx);
        Usuario actualizado = usuarioServices.actualizarUsuario(ctx.bodyAsClass(Usuario.class));
        ctx.json(RespuestaDTO.ok(actualizado));
    }

    public static void eliminarUsuario(Context ctx) {
        validarAdmin(ctx);
        if (!usuarioServices.eliminarUsuario(ctx.pathParam("id"))) {
            throw new NotFoundResponse("Usuario no encontrado");
        }
        ctx.json(RespuestaDTO.ok("Usuario eliminado correctamente"));
    }
}