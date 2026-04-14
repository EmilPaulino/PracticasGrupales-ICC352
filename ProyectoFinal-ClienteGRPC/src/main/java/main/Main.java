package main;

import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;
import main.controladores.AuthController;
import main.controladores.FormularioController;

public class Main {
    public static void main(String[] args) {

        Javalin.create(config -> {
            config.fileRenderer(new JavalinThymeleaf());

            config.routes.before(ctx -> {
                String path = ctx.path();
                if (path.equals("/login")) return;
                String usuario = ctx.sessionAttribute("username");
                if (usuario == null) {
                    ctx.redirect("/login");
                }
            });

            config.routes.get("/formularios", FormularioController::listarFormularios);
            config.routes.get("/formularios/crear", FormularioController::vistaCrearFormulario);
            config.routes.post("/formularios", FormularioController::guardarFormulario);

            config.routes.get("/login", AuthController::mostrarLogin);
            config.routes.post("/login", AuthController::login);
            config.routes.get("/logout", AuthController::logout);
        }).start(7001);

    }
}
