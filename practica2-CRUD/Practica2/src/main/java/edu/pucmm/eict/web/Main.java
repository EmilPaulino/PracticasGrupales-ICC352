package edu.pucmm.eict.web;

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

        app.get("/", ctx -> ctx.render("templates/index.html"));
        app.start(7000);
    }
}
