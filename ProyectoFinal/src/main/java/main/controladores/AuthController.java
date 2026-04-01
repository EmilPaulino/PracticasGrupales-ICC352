package main.controladores;

import io.javalin.http.Context;
import io.javalin.http.BadRequestResponse;

import main.entidades.Usuario;
import main.servicios.UsuarioServices;
import main.util.JwtUtil;
import main.util.RespuestaDTO;

import java.util.Map;

public class AuthController {

    private static final UsuarioServices usuarioServices = UsuarioServices.getInstancia();

    public static void login(Context ctx) {
        Usuario datos = ctx.bodyAsClass(Usuario.class);
        if (datos == null || datos.getUsername() == null || datos.getPassword() == null){
            throw new BadRequestResponse("Username y password requeridos");
        }

        Usuario usuario = usuarioServices.getUsuarioByUsername(datos.getUsername());

        if (usuario == null || !datos.getPassword().equals(usuario.getPassword())) {
            ctx.status(401);
            ctx.json(RespuestaDTO.error("Credenciales inválidas"));
            return;
        }

        String token = JwtUtil.generarToken(usuario.getUsername());
        usuario.setPassword(null);
        ctx.json(RespuestaDTO.ok(Map.of("usuario", usuario, "token", token)));
    }

    public static void logout(Context ctx) {
        ctx.json(RespuestaDTO.ok("Logout exitoso"));
    }

    public static void usuarioActual(Context ctx) {

        String username = ctx.attribute("username");

        Usuario usuario = usuarioServices.getUsuarioByUsername(username);

        if (usuario == null) {

            ctx.status(401);
            ctx.json(RespuestaDTO.error("Usuario no encontrado"));
            return;
        }

        usuario.setPassword(null);

        ctx.json(RespuestaDTO.ok(usuario));
    }
}