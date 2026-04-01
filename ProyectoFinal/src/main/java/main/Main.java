package main;

import io.javalin.Javalin;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;

import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinThymeleaf;
import main.controladores.AuthController;
import main.controladores.FormularioController;
import main.controladores.UsuarioController;
import main.util.JwtUtil;

public class Main {

    public static void main(String[] args) {

        Javalin.create(config -> {
            config.fileRenderer(new JavalinThymeleaf());

            config.staticFiles.add(staticFileConfig -> {
                staticFileConfig.hostedPath = "/";
                staticFileConfig.directory = "/public";
                staticFileConfig.location = Location.CLASSPATH;
                staticFileConfig.aliasCheck=null;
            });

            config.routes.before("/api/*", ctx -> {

                if (ctx.path().equals("/api/login") || ctx.path().equals("/api/logout")) {
                    return;
                }
                String header = ctx.header("Authorization");
                if (header == null || !header.startsWith("Bearer ")) {
                    throw new UnauthorizedResponse("Token requerido");
                }
                String token = header.replace("Bearer ", "");
                try {
                    var claims = JwtUtil.validarToken(token);
                    ctx.attribute("username", claims.get("username"));
                } catch (Exception e) {
                    throw new ForbiddenResponse("Token inválido");
                }
            });

            //Auth
            config.routes.post("/api/login", AuthController::login);
            config.routes.post("/api/logout", AuthController::logout);
            config.routes.get("/api/usuario/actual", AuthController::usuarioActual);
            config.routes.get("/login", ctx -> {
                ctx.render("/templates/login/login.html");
            });

            // Usuarios
            config.routes.get("/api/usuarios", UsuarioController::listarUsuarios);
            config.routes.get("/api/usuarios/{username}", UsuarioController::getUsuarioByUsername);
            config.routes.post("/api/usuarios", UsuarioController::crearUsuario);
            config.routes.put("/api/usuarios", UsuarioController::actualizarUsuario);
            config.routes.delete("/api/usuarios/{id}", UsuarioController::eliminarUsuario);


            // Formularios
            config.routes.get("/api/formularios", FormularioController::listarFormularios);
            config.routes.get("/api/formularios/usuario", FormularioController::listarFormulariosPorUsuario);
            config.routes.get("/api/formularios/{id}", FormularioController::getFormularioPorId);
            config.routes.post("/api/formularios", FormularioController::crearFormulario);
            config.routes.put("/api/formularios", FormularioController::actualizarFormulario);
            config.routes.delete("/api/formularios/{id}", FormularioController::eliminarFormulario);



            // WebSocket para sincronización
            config.routes.ws("/sync", ws -> {

                ws.onConnect(ctx -> {
                    System.out.println("Cliente conectado a /sync");
                });

                ws.onMessage(ctx -> {
                    String json = ctx.message();
                    System.out.println("Mensaje recibido:");
                    System.out.println(json);

                    try {
                        FormularioController.procesarSync(json);
                        System.out.println("Formulario sincronizado");
                        ctx.send("OK");
                    } catch (Exception e) {
                        System.out.println("Error en sincronización");
                        e.printStackTrace();
                        ctx.send("ERROR");
                    }
                });

                ws.onClose(ctx -> {
                    System.out.println("Cliente desconectado");
                });

            });

        }).start(7000);
    }
}