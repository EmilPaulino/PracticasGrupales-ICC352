package main.controladores;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void vistaPrincipal(Context ctx) {
        String username = ctx.sessionAttribute("username");
        List<Formulario> formularios = formularioService.listarFormulariosPorUsuario(username);
        Map<String, Object> model = new HashMap<>();
        model.put("formulariosSync", formularios);
        model.put("username",  username);
        ctx.render("templates/formulario/listarFormEnc.html", model);
    }

    public static void mostrarFormulario(Context ctx) {
        ctx.render("/templates/formulario/formEncuesta.html", new HashMap<>());
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

    public static String sincronizarFormulariosWS(String json) {
        try {
            JsonNode root = mapper.readTree(json);

            // Extrae username del payload
            JsonNode usernameNode = root.get("username");
            if (usernameNode == null || usernameNode.isNull()) {
                System.err.println("[WS] Payload sin campo 'username'");
                return "ERROR";
            }
            String username = usernameNode.asText();

            // Busca el usuario real en la base de datos
            Usuario usuarioReal = UsuarioService.getInstancia().findByUsername(username);
            if (usuarioReal == null) {
                System.err.println("Usuario no encontrado: " + username);
                return "ERROR";
            }

            // Obtiene la lista de formularios
            JsonNode formulariosNode = root.get("formularios");
            if (formulariosNode == null || !formulariosNode.isArray()) {
                System.err.println("Payload sin campo 'formularios' o no es array");
                return "ERROR";
            }

            List<Formulario> lista = mapper.convertValue(
                    formulariosNode,
                    new TypeReference<List<Formulario>>() {}
            );

            if (lista.isEmpty()) {
                System.out.println("Lista de formularios vacía, nada que guardar");
                return "OK";
            }

            // Persistencia en cada formulario
            int guardados = 0;
            for (Formulario f : lista) {
                try {
                    f.setUsuario(new UsuarioEmbebido(usuarioReal));
                    f.setId(null); // MongoDB genera el ID
                    if (f.getFechaRegistro() == null) {
                        f.setFechaRegistro(new java.util.Date());
                    }
                    formularioService.crearFormulario(f);
                    guardados++;
                } catch (Exception e) {
                    System.err.println("Error guardando formulario: " + e.getMessage());
                }
            }

            System.out.println("Formularios guardados: " + guardados + "/" + lista.size() + " — usuario: " + username);
            return "OK";

        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }
    }
}