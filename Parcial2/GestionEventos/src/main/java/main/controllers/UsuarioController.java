package main.controllers;

import io.javalin.http.Context;
import main.models.Usuario;
import main.services.UsuarioService;

import java.util.List;
import java.util.Map;

public class UsuarioController {

    private static UsuarioService usuarioService = UsuarioService.getInstancia();

    public static void listar(Context ctx) {
        List<Usuario> usuarios = usuarioService.findAll();
        ctx.render("templates/usuarios/listarUsuarios.html", Map.of("usuarios", usuarios));
    }
}