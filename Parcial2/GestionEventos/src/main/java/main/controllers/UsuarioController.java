package main.controllers;

import io.javalin.http.Context;
import main.models.Rol;
import main.models.Usuario;
import main.services.GestionDb;
import main.services.UsuarioService;

import java.util.List;
import java.util.Map;

public class UsuarioController {

    private static UsuarioService usuarioService = UsuarioService.getInstancia();

    public static void listar(Context ctx) {
        List<Usuario> usuarios = usuarioService.findAll();
        ctx.render("templates/usuarios/listarUsuarios.html", Map.of("usuarios", usuarios));
    }

    public static void crear(Context ctx){
        String username = ctx.formParam("username");
        String nombre = ctx.formParam("nombre");
        String password = ctx.formParam("password");

        UsuarioService usuarioService = UsuarioService.getInstancia();
        GestionDb<Rol> rolDb = new GestionDb<>(Rol.class);

        Rol rolParticipante = null;

        for(Rol r : rolDb.findAll()){
            if(r.getRol().equals("Participante")){
                rolParticipante = r;
                break;
            }
        }

        Usuario usuario = new Usuario(
                nombre,
                username,
                password,
                true,
                rolParticipante
        );

        usuarioService.crear(usuario);

        ctx.redirect("/usuarios");
    }

    public static void formNuevo(Context ctx){
        ctx.render("templates/usuarios/formularioUsuarios.html");
    }

    public static void loginForm(Context ctx){
        ctx.render("templates/login.html");
    }

    public static void login(Context ctx){

        String username = ctx.formParam("username");
        String password = ctx.formParam("password");

        UsuarioService usuarioService = UsuarioService.getInstancia();

        Usuario usuario = usuarioService.login(username, password);

        if(usuario != null){

            //guardando en sesión
            ctx.sessionAttribute("usuario", usuario);

            ctx.redirect("/panel");

        }else{
            ctx.attribute("error", true);
            ctx.render("templates/login.html");
        }

    }

    public static void logout(Context ctx){

        ctx.req().getSession().invalidate();

        ctx.redirect("/login");
    }
}