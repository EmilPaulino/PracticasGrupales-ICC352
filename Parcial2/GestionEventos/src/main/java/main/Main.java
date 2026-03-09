package main;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
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
            /*
            //Para proteger las rutas
            config.routes.before("/home*", ctx -> {

                Usuario usuario = ctx.sessionAttribute("usuario");

                if(usuario == null){
                    ctx.redirect("/login");
                }

            });
            */
        });

        BootStrapServices.getInstancia().init();
        app.start(7000);
    }
}
