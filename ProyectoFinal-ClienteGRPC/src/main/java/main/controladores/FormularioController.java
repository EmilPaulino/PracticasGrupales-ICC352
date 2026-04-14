package main.controladores;

import io.javalin.http.Context;

import formulariogrpc.Formulario;
import main.grpc.GrpcClient;

import java.util.HashMap;
import java.util.Map;

public class FormularioController {

    public static void listarFormularios(Context ctx) {

        String usuario = ctx.sessionAttribute("username");

        if (usuario == null) usuario = "admin";

        int page = ctx.queryParam("page") != null ? Integer.parseInt(ctx.queryParam("page")) : 1;

        int size = 10;

        Formulario.ListaFormulario lista = GrpcClient.getInstancia().listarFormularios(usuario, page, size);

        long total = lista.getTotal();

        int totalPaginas = (int) Math.ceil((double) total / size);

        Map<String, Object> model = new HashMap<>();

        model.put("formularios", lista.getFormularioList());
        model.put("total", total);
        model.put("paginaActual", page);
        model.put("totalPaginas", totalPaginas);
        model.put("size", size);

        ctx.render("templates/listadoFormularios.html", model);
    }

    public static void vistaCrearFormulario(Context ctx) {

        ctx.render("templates/crearFormulario.html");

    }

    public static void guardarFormulario(Context ctx) {

        String usuario = ctx.sessionAttribute("username");

        if (usuario == null) usuario = "admin";

        String nombre = ctx.formParam("nombre");

        String sector = ctx.formParam("sector");

        String nivel = ctx.formParam("nivelEscolar");

        String foto = ctx.formParam("fotoBase64");

        double lat = Double.parseDouble(ctx.formParam("latitud"));

        double lng = Double.parseDouble(ctx.formParam("longitud"));

        String fecha = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());

        boolean ok = GrpcClient.getInstancia().crearFormulario(nombre, sector, nivel, lat, lng, foto, usuario, fecha);

        if (ok) {

            Map<String, Object> model = new HashMap<>();

            model.put("mensaje", "Formulario guardado correctamente");

            ctx.render("templates/crearFormulario.html", model);

        } else {

            ctx.result("Error creando formulario");

        }

    }

}