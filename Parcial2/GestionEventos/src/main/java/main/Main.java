package main;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import main.controllers.UsuarioController;
import main.models.Usuario;
import main.services.BootStrapServices;
import io.javalin.rendering.template.JavalinThymeleaf;
import main.services.UsuarioService;
import main.util.EncryptUtil;

public class Main {
    public static void main(String[] args){
        Javalin app = Javalin.create(config ->{
            //Archivos estáticos
            config.staticFiles.add("/public", Location.CLASSPATH);

            config.fileRenderer(new JavalinThymeleaf());

            config.routes.before(ctx -> {

                Usuario usuario = ctx.sessionAttribute("usuario");

                if (usuario == null) {

                    String cookie = ctx.cookie("rememberMe");

                    if (cookie != null) {
                        try {

                            String email = EncryptUtil.decrypt(cookie);

                            Usuario user = UsuarioService.getInstancia()
                                    .buscarPorEmail(email);

                            if (user != null) {
                                ctx.sessionAttribute("usuario", user);
                                usuario = user;
                            }

                        } catch (Exception e) {
                            ctx.removeCookie("rememberMe");
                        }
                    }
                }

                if (usuario != null) {
                    ctx.attribute("usuario", usuario);
                }

            });

            //Nueva manera de implementar rutas
            config.routes.get("/", ctx -> {
                ctx.render("/templates/prueba/prueba.html");
            });
            config.routes.get("/panel", ctx -> {
                ctx.render("templates/panel/panel.html");
            });

            //Rutas para USUARIO
            config.routes.get("/usuarios", UsuarioController::listar);
            config.routes.post("/usuarios/crear", UsuarioController::crear);
            config.routes.get("/usuarios/nuevo", UsuarioController::formNuevo);
            config.routes.post("/usuarios/editar/{id}", UsuarioController::editar);
            config.routes.get("/usuarios/editar/{id}", UsuarioController::formEditar);
            config.routes.post("usuarios/eliminar/{id}", UsuarioController::eliminar);
            config.routes.post("/usuarios/bloquear/{id}", UsuarioController::bloquear);
            config.routes.post("/usuarios/desbloquear/{id}", UsuarioController::desbloquear);

            //Rutas para Login y Registro
            config.routes.get("/login", UsuarioController::loginForm);
            config.routes.post("/procesarLogin", UsuarioController::procesarLogin);
            config.routes.get("/logout", UsuarioController::logout);
            config.routes.get("/registrarse", UsuarioController::registroForm);
            config.routes.post("/registrarse", UsuarioController::registroEstudiante);
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
