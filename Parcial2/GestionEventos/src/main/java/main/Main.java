package main;

import io.javalin.Javalin;
import main.services.BootStrapServices;


public class Main {
    public static void main(String[] args){
        Javalin app = Javalin.create(config ->{
            //Nueva manera de implementar rutas
            config.routes.get("/", ctx -> {
                ctx.result("Hello World!");
            });
        });

        BootStrapServices.getInstancia().init();

        app.start(7000);

    }
}
