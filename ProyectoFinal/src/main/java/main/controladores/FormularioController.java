package main.controladores;

import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;

import main.entidades.Formulario;
import main.entidades.Usuario;
import main.entidades.UsuarioEmbebido;

import main.servicios.FormularioServices;
import main.servicios.UsuarioService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormularioController {

    private static final FormularioServices formularioService = FormularioServices.getInstancia();

    private static final UsuarioService usuarioService = UsuarioService.getInstancia();

    public static void vistaPrincipal(Context ctx) {
        String username = ctx.sessionAttribute("username");
        List<Formulario> formularios = formularioService.listarFormulariosPorUsuario(username);
        Map<String, Object> model = new HashMap<>();
        model.put("formulariosSync", formularios);
        ctx.render("templates/formulario/listarFormEnc.html", model);

    }

    public static void mostrarFormulario(Context ctx) {
        Map<String, Object> model = new HashMap<>();
        ctx.render("/templates/formulario/formEncuesta.html", model);
    }

    public static void sincronizarFormularios(Context ctx) {
        String username = ctx.sessionAttribute("username");
        Usuario usuarioReal = UsuarioService.getInstancia().findByUsername(username);
        Formulario[] formularios = ctx.bodyAsClass(Formulario[].class);
        for (Formulario f : formularios) {
            UsuarioEmbebido usuarioEmbebido = new UsuarioEmbebido(usuarioReal);
            f.setUsuario(usuarioEmbebido);
            f.setId(null);
            if (f.getFechaRegistro() == null) {
                f.setFechaRegistro(new java.util.Date());
            }
            formularioService.crearFormulario(f);
        }
        ctx.status(200);
    }

    public static void listarFormularios(Context ctx) {
        List<Formulario> lista = formularioService.listarFormularios();
        Map<String, Object> model = new HashMap<>();
        model.put("formularios", lista);
        ctx.render("/templates/formulario/listarFormularios.html", model);
    }

    public static void verFormulario(Context ctx) {
        String id = ctx.pathParam("id");
        Formulario formulario = formularioService.getFormularioPorId(id);
        if (formulario == null) {
            throw new NotFoundResponse("Formulario no encontrado");
        }
        Map<String, Object> model = new HashMap<>();
        model.put("formulario", formulario);
        ctx.render("/templates/formulario/verFormEnc.html", model);
    }
}