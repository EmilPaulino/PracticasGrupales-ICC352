package main.controladores;

import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.NotFoundResponse;

import main.entidades.Rol;
import main.entidades.Usuario;
import main.servicios.UsuarioService;

import java.util.HashMap;
import java.util.Map;

public class UsuarioController {

    private static final UsuarioService usuarioService = UsuarioService.getInstancia();

    private static Usuario validarAdmin(Context ctx) {
        String username = ctx.sessionAttribute("username");
        if (username == null) {
            throw new ForbiddenResponse("Acceso denegado");
        }
        Usuario usuario = usuarioService.findByUsername(username);
        if (usuario == null || usuario.getRol() != Rol.ADMIN) {
            throw new ForbiddenResponse("Acceso denegado");
        }
        return usuario;
    }

    public static void listarUsuarios(Context ctx) {
        validarAdmin(ctx);
        var usuarios = usuarioService.findAll();
        Map<String, Object> model = new HashMap<>();
        model.put("usuarios", usuarios);
        ctx.render("/templates/usuario/listarUsuarios.html", model);
    }

    public static void crearUsuario(Context ctx) {
        validarAdmin(ctx);
        Usuario usuario = new Usuario();
        usuario.setNombre(ctx.formParam("nombre"));
        usuario.setUsername(ctx.formParam("username"));
        usuario.setPassword(ctx.formParam("password"));
        usuario.setRol(Rol.valueOf(ctx.formParam("rol")));
        Usuario creado = usuarioService.crear(usuario);
        if (creado == null) {
            ctx.redirect("/admin/usuarios/formulario/crear?error=existe");
            return;
        }
        ctx.redirect("/admin/usuarios");
    }

    public static void actualizarUsuario(Context ctx) {
        validarAdmin(ctx);
        String idString = ctx.formParam("id");
        if (idString == null) {
            throw new NotFoundResponse("ID inválido");
        }
        Usuario usuario = new Usuario();
        usuario.setId(new org.bson.types.ObjectId(idString));
        usuario.setNombre(ctx.formParam("nombre"));
        usuario.setRol(Rol.valueOf(ctx.formParam("rol")));
        String password = ctx.formParam("password");
        if (password != null && !password.isBlank()) {
            usuario.setPassword(password);
        }
        Usuario actualizado = usuarioService.editar(usuario);
        if (actualizado == null) {
            throw new NotFoundResponse("No se pudo actualizar usuario");
        }
        ctx.redirect("/admin/usuarios");
    }

    public static void eliminarUsuario(Context ctx) {
        validarAdmin(ctx);
        String id = ctx.pathParam("id");
        if (!usuarioService.eliminar(id)) {
            throw new NotFoundResponse("Usuario no encontrado");
        }
        ctx.redirect("/admin/usuarios");
    }

    public static void mostrarFormularioCrear(Context ctx) {
        validarAdmin(ctx);
        Map<String, Object> model = new HashMap<>();
        model.put("modo", "crear");
        model.put("error", ctx.queryParam("error"));
        ctx.render("/templates/usuario/formUsuario.html", model);
    }

    public static void mostrarFormularioEditar(Context ctx) {
        validarAdmin(ctx);
        String id = ctx.pathParam("id");
        Usuario usuario = usuarioService.findById(id);
        if (usuario == null) {
            throw new NotFoundResponse("Usuario no encontrado");
        }
        Map<String, Object> model = new HashMap<>();
        model.put("usuario", usuario);
        model.put("modo", "editar");
        ctx.render("/templates/usuario/formUsuario.html", model);
    }
}