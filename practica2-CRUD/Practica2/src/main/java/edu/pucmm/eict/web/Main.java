package edu.pucmm.eict.web;

import edu.pucmm.eict.web.contoladores.LoginController;
import edu.pucmm.eict.web.contoladores.UsuarioController;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinThymeleaf;

public class Main {
    public static void main(String[] args) {
        var app = Javalin.create(config -> {
            //Archivos estáticos
            config.staticFiles.add(staticFileConfig -> {
                staticFileConfig.hostedPath = "/";
                staticFileConfig.directory = "/public";
                staticFileConfig.location = Location.CLASSPATH;
                staticFileConfig.precompress = false;
                staticFileConfig.aliasCheck = null;
            });

            //Configuracion de thymeleaf
            config.fileRenderer(new JavalinThymeleaf());
        });

        //Endpoint que va al index
        app.get("/", ctx -> {
            ctx.render("templates/index.html");
        });

        //Endpoints para Login
        LoginController loginController = new LoginController();
        app.get("/login", loginController::mostrarLogin);
        app.before("/login", ctx -> {
            if (ctx.sessionAttribute("user") != null) {
                ctx.redirect("/");
            }
        });
        app.get("/logout", loginController::logout);
        app.post("/procesarLogin", loginController::procesarLogin);

        //Endpoints para Usuarios
        UsuarioController usuarioController = new UsuarioController();
        app.get("/usuarios", usuarioController::listar);
        app.get("/usuarios/crear", usuarioController::formularioCrear);
        app.post("/usuarios/crear", usuarioController::crear);
        app.get("/usuarios/editar/{id}", usuarioController::formularioEditar);
        app.post("/usuarios/editar/{id}", usuarioController::editar);
        app.get("/usuarios/eliminar/{id}", usuarioController::eliminar);

        app.start(7000);
    }
}
