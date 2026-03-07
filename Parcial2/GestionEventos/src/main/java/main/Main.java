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

            //Los usuarios se crean como PARTICIPANTES, el ADMIN decide si convertirlos en ORGANIZADOR
            config.routes.post("/registrar", ctx -> {

                String username = ctx.formParam("username");
                String email = ctx.formParam("email");
                String password = ctx.formParam("password");

                UsuarioService usuarioService = UsuarioService.getInstancia();
                GestionDb<Rol> rolDb = new GestionDb<>(Rol.class);

                Rol rolParticipante = null;

                for(Rol r : rolDb.findAll()){
                    if(r.getRol().equals("Participante")){
                        rolParticipante = r;
                        break;
                    }
                }

                Usuario usuario = new Usuario(
                        username,
                        email,
                        password,
                        true,
                        rolParticipante
                );

                usuarioService.crear(usuario);

                ctx.redirect("/login");
            });

        });

        BootStrapServices.getInstancia().init();

        app.start(7000);

    }
}
