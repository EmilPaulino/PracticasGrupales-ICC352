package main.controladores;

import main.entidades.Formulario;
import main.entidades.Usuario;
import main.servicios.FormularioServices;
import main.servicios.UsuarioServices;
import main.util.BaseController;
import io.javalin.config.JavalinConfig;

public class ApiController extends BaseController {

    private UsuarioServices usuarioServices = UsuarioServices.getInstancia();
    private FormularioServices formularioServices = FormularioServices.getInstancia();

    public ApiController(JavalinConfig config){ super(config); }

    @Override
    public void aplicarRutas(){

        /* USUARIOS */

        config.routes.get("/api/usuarios", ctx -> ctx.json(usuarioServices.listarUsuarios()));

        config.routes.get("/api/usuarios/{username}", ctx -> {
            Usuario u = usuarioServices.getUsuarioByUsername(ctx.pathParam("username"));
            if(u == null){ ctx.status(404); return; }
            ctx.json(u);
        });

        config.routes.post("/api/usuarios", ctx -> {
            Usuario tmp = ctx.bodyAsClass(Usuario.class);
            Usuario creado = usuarioServices.crearUsuario(tmp);
            if(creado == null){ ctx.status(400); ctx.json("Usuario ya existe"); return; }
            ctx.json(creado);
        });

        config.routes.put("/api/usuarios", ctx ->
                ctx.json(usuarioServices.actualizarUsuario(ctx.bodyAsClass(Usuario.class)))
        );

        config.routes.delete("/api/usuarios/{id}", ctx -> {
            if(!usuarioServices.eliminarUsuario(ctx.pathParam("id"))){ ctx.status(404); return; }
            ctx.status(204);
        });

        /* LOGIN */

        config.routes.post("/api/login", ctx -> {
            Usuario datos = ctx.bodyAsClass(Usuario.class);
            Usuario usuario = usuarioServices.getUsuarioByUsername(datos.getUsername());

            if(usuario == null || !usuario.getPassword().equals(datos.getPassword())){
                ctx.status(401);
                ctx.json("Credenciales inválidas");
                return;
            }

            ctx.sessionAttribute("usuario", usuario);

            usuario.setPassword(null);
            ctx.json(usuario);
        });

        config.routes.post("/api/logout", ctx -> {
            ctx.req().getSession().invalidate();
            ctx.status(204);
        });

        config.routes.get("/api/usuario/actual", ctx -> {

            Usuario usuario = ctx.sessionAttribute("usuario");

            if(usuario == null){
                ctx.status(401);
                return;
            }

            usuario.setPassword(null);
            ctx.json(usuario);
        });

        //FORMULARIOS

        config.routes.post("/api/formularios", ctx ->
                ctx.json(formularioServices.crearFormulario(ctx.bodyAsClass(Formulario.class)))
        );

        config.routes.get("/api/formularios", ctx ->
                ctx.json(formularioServices.listarFormularios())
        );

        config.routes.get("/api/formularios/{id}", ctx -> {
            Formulario f = formularioServices.getFormularioPorId(ctx.pathParam("id"));
            if(f == null){ ctx.status(404); return; }
            ctx.json(f);
        });

        config.routes.get("/api/formularios/usuario/{username}", ctx ->
                ctx.json(formularioServices.listarFormulariosPorUsuario(ctx.pathParam("username")))
        );

        config.routes.put("/api/formularios", ctx ->
                ctx.json(formularioServices.actualizarFormulario(ctx.bodyAsClass(Formulario.class)))
        );

        config.routes.delete("/api/formularios/{id}", ctx -> {
            if(!formularioServices.eliminarFormulario(ctx.pathParam("id"))){ ctx.status(404); return; }
            ctx.status(204);
        });
    }
}