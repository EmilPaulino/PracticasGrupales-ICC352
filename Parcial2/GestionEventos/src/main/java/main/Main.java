package main;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import main.controllers.EventoController;
import main.controllers.UsuarioController;
import main.models.Rol;
import main.models.Usuario;
import main.services.BootStrapServices;
import io.javalin.rendering.template.JavalinThymeleaf;
import main.services.GestionDb;
import main.services.UsuarioService;
import io.javalin.http.Context;

public class Main {
    public static void main(String[] args){
        Javalin app = Javalin.create(config ->{
            //Archivos estáticos
            config.staticFiles.add("/public", Location.CLASSPATH);

            config.fileRenderer(new JavalinThymeleaf());

            //Nueva manera de implementar rutas
            config.routes.get("/", ctx -> {
                ctx.result("Hello World!");
            });
            config.routes.get("/panel", ctx -> {
                ctx.render("templates/panel/panel.html");
            });

            //Rutas para USUARIO
            config.routes.get("/usuarios", UsuarioController::listar);
            config.routes.post("/usuarios/crear", UsuarioController::crear);
            config.routes.get("/usuarios/nuevo", UsuarioController::formNuevo);

            //Rutas para LOGIN
            config.routes.get("/login", UsuarioController::loginForm);
            config.routes.post("/login", UsuarioController::login);
            config.routes.get("/logout", UsuarioController::logout);

            //Rutas para EVENTOS
            config.routes.get("/eventos", EventoController::listar);
            config.routes.get("/eventos/nuevo", EventoController::formNuevo);
            config.routes.post("/eventos/crear", EventoController::crear);

            config.routes.get("/eventos/editar/{id}", EventoController::formEditar);
            config.routes.post("/eventos/editar/{id}", EventoController::editar);

            config.routes.get("/eventos/cancelar/{id}", EventoController::cancelar);
            config.routes.get("/eventos/publicar/{id}", EventoController::publicar);
            config.routes.get("/eventos/despublicar/{id}", EventoController::desPublicar);
            /*
            //Para proteger las rutas
            config.routes.before("/home*", ctx -> {

                Usuario usuario = ctx.sessionAttribute("usuario");

                if(usuario == null){
                    ctx.redirect("/login");
                }

            });
            */
            //Validación para eventos
            config.routes.before("/eventos/*", ctx -> {

                Usuario usuario = ctx.sessionAttribute("usuario");

                if(usuario == null){
                    ctx.redirect("/login");
                    return;
                }

                if(!usuario.getRol().equals("ORGANIZADOR") &&
                        !usuario.getRol().equals("ADMIN")){
                    ctx.status(403);
                    ctx.result("No autorizado");
                }

            });
        });

        BootStrapServices.getInstancia().init();
        app.start(7000);
    }
}
