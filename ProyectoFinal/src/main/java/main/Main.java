package main;

import io.javalin.Javalin;
import main.controladores.ApiController;
import main.servicios.MongoDBConexion;

public class Main {
    public static void main(String[] args) {
        var app = Javalin.create(config -> {

            new ApiController(config)
                    .aplicarRutas();

        }).start(7000);
    }
}
