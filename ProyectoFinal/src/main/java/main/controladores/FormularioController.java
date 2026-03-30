package main.controladores;

import io.javalin.http.Context;
import main.entidades.Formulario;
import main.servicios.FormularioServices;
import main.util.RespuestaDTO;

public class FormularioController {

    private static final FormularioServices formularioServices = FormularioServices.getInstancia();

    public static void listarFormularios(Context ctx) {
        ctx.json(RespuestaDTO.ok(formularioServices.listarFormularios()));
    }

    public static void getFormularioPorId(Context ctx) {
        Formulario f = formularioServices.getFormularioPorId(ctx.pathParam("id"));
        if (f == null) {
            ctx.status(404);
            ctx.json(RespuestaDTO.error("Formulario no encontrado"));
            return;
        }
        ctx.json(RespuestaDTO.ok(f));
    }

    public static void listarFormulariosPorUsuario(Context ctx) {
        ctx.json(RespuestaDTO.ok(formularioServices.listarFormulariosPorUsuario(ctx.pathParam("username"))));
    }

    public static void crearFormulario(Context ctx) {
        ctx.json(RespuestaDTO.ok(formularioServices.crearFormulario(ctx.bodyAsClass(Formulario.class))));
    }

    public static void actualizarFormulario(Context ctx) {
        Formulario actualizado = formularioServices.actualizarFormulario(ctx.bodyAsClass(Formulario.class));
        if (actualizado == null) {
            ctx.status(404);
            ctx.json(RespuestaDTO.error("Formulario no encontrado"));
            return;
        }
        ctx.json(RespuestaDTO.ok(actualizado));
    }

    public static void eliminarFormulario(Context ctx) {
        if (!formularioServices.eliminarFormulario(ctx.pathParam("id"))) {
            ctx.status(404);
            ctx.json(RespuestaDTO.error("Formulario no encontrado"));
            return;
        }
        ctx.json(RespuestaDTO.ok("Formulario eliminado correctamente"));
    }
}