package main;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import main.controllers.UsuarioController;
import main.services.BootStrapServices;
import io.javalin.rendering.template.JavalinThymeleaf;

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

            config.routes.get("/usuarios", UsuarioController::listar);
        });

        BootStrapServices.getInstancia().init();

        app.start(7000);

    }
}
