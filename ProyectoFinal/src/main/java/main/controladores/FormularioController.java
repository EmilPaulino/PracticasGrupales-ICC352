package main.controladores;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import main.entidades.Formulario;
import main.entidades.Usuario;
import main.entidades.UsuarioEmbebido;
import main.servicios.FormularioServices;
import main.servicios.UsuarioServices;
import main.util.RespuestaDTO;

import java.util.Date;

public class FormularioController {

    private static final FormularioServices formularioServices = FormularioServices.getInstancia();
    private static final ObjectMapper mapper = new ObjectMapper();

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
        String username = ctx.attribute("username");
        var lista = formularioServices.listarFormulariosPorUsuario(username);
        ctx.json(RespuestaDTO.ok(lista));
    }

    public static void crearFormulario(Context ctx) {
        Formulario formulario = ctx.bodyAsClass(Formulario.class);
        String username = ctx.attribute("username");
        Usuario usuario = UsuarioServices.getInstancia().getUsuarioByUsername(username);
        if (usuario == null) {
            throw new UnauthorizedResponse("Usuario no válido");
        }
        validarFormulario(formulario);
        formulario.setUsuario(new UsuarioEmbebido(usuario));
        formulario.setFechaRegistro(new Date());
        Formulario creado = formularioServices.crearFormulario(formulario);
        ctx.status(201).json(RespuestaDTO.ok(creado));
    }

    private static void validarFormulario(Formulario f) {
        if (esVacio(f.getNombre())) throw new BadRequestResponse("Nombre requerido");
        if (esVacio(f.getSector())) throw new BadRequestResponse("Sector requerido");
        if (f.getNivelEscolar() == null) throw new BadRequestResponse("Nivel escolar requerido");
        if (f.getUbicacion() == null) throw new BadRequestResponse("Ubicación requerida");
        if (esUbicacionInvalida(f)) throw new BadRequestResponse("Ubicación inválida");
        if (esVacio(f.getFotoBase64())) throw new BadRequestResponse("Foto requerida");
    }

    private static boolean esVacio(String s) {
        return s == null || s.isBlank();
    }

    private static boolean esUbicacionInvalida(Formulario f) {
        return f.getUbicacion().getLatitud() == 0 && f.getUbicacion().getLongitud() == 0;
    }

    public static void actualizarFormulario(Context ctx) {
        String id = ctx.bodyAsClass(Formulario.class).getId().toString();
        Formulario existente = formularioServices.getFormularioPorId(id);
        if (existente == null) {
            ctx.status(404);
            ctx.json(RespuestaDTO.error("Formulario no encontrado"));
            return;
        }
        Formulario datos = ctx.bodyAsClass(Formulario.class);
        existente.setNombre(datos.getNombre());
        existente.setSector(datos.getSector());
        existente.setNivelEscolar(datos.getNivelEscolar());
        existente.setUbicacion(datos.getUbicacion());
        existente.setFotoBase64(datos.getFotoBase64());
        Formulario actualizado = formularioServices.actualizarFormulario(existente);
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


    public static void procesarSync(String json) {
        try {
            Formulario formulario = mapper.readValue(json, Formulario.class);
            validarFormulario(formulario);
            formulario.setFechaRegistro(new Date());
            FormularioServices.getInstancia().crearFormulario(formulario);
        } catch (Exception e) {
            throw new RuntimeException("Error procesando sincronización", e);
        }

    }
}