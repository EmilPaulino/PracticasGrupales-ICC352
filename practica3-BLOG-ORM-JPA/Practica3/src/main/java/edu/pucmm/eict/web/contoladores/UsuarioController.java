package edu.pucmm.eict.web.contoladores;

import edu.pucmm.eict.web.entidades.Usuario;
import edu.pucmm.eict.web.servicios.UsuarioService;

import java.util.ArrayList;
import io.javalin.http.Context;

public class UsuarioController {

    private UsuarioService usuarioService = UsuarioService.getInstancia();

    /*
     * Lista todos los usuarios
     */
    public void listar(Context ctx){
        ArrayList<Usuario> usuarios = usuarioService.listarUsuarios();
        ctx.attribute("usuarios", usuarios);
        ctx.render("/templates/usuarios/listarUsuarios.html");
    }

    /*
     * Muestra el formulario para crear usuario
     */
    public void formularioCrear(Context ctx){
        ctx.render("templates/usuarios/formularioUsuario.html");
    }

    /*
     * Procesa la creación de usuario
     */
    public void crear(Context ctx){
        String username = ctx.formParam("username");
        String name = ctx.formParam("nombre");
        String password = ctx.formParam("password");

        boolean administrator = ctx.formParam("administrator") != null;
        boolean autor = ctx.formParam("autor") != null;

        boolean creado = usuarioService.registrarUsuario(
                username, name, password, administrator, autor
        );

        if(!creado){
            ctx.attribute("error", "El nombre de usuario ya existe");
            ctx.render("templates/usuarios/formularioUsuario.html");
            return;
        }

        ctx.redirect("/usuarios");
    }

    /*
     * Muestra el formulario para editar usuario
     */
    public void formularioEditar(Context ctx){
        long id = Long.parseLong(ctx.pathParam("id"));
        Usuario usuario = usuarioService.buscarUsuarioPorID(id);
        if(usuario == null){
            ctx.status(404);
            return;
        }
        ctx.attribute("usuario", usuario);
        ctx.render("templates/usuarios/formularioUsuario.html");
    }

    /*
     * Procesa la edición de usuario
     */
    public void editar(Context ctx){
        long id = Long.parseLong(ctx.pathParam("id"));

        String username = ctx.formParam("username");
        String name = ctx.formParam("nombre");
        String password = ctx.formParam("password");

        boolean administrator = ctx.formParam("administrator") != null;
        boolean autor = ctx.formParam("autor") != null;

        boolean actualizado = usuarioService.actualizarUsuario(
                id, username, name, password, administrator, autor
        );

        if(!actualizado){
            ctx.attribute("error", "El nombre de usuario ya existe");
            ctx.attribute("usuario", usuarioService.buscarUsuarioPorID(id));
            ctx.render("templates/usuarios/formularioUsuario.html");
            return;
        }

        ctx.redirect("/usuarios");
    }

    /*
     * Elimina un usuario
     */
    public void eliminar(Context ctx){
        long id = Long.parseLong(ctx.pathParam("id"));
        usuarioService.eliminarUsuario(id);
        ctx.redirect("/usuarios");
    }
}
