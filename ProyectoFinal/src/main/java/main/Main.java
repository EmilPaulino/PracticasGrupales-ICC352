package main;

import io.javalin.Javalin;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;
import main.controladores.AuthController;
import main.controladores.FormularioController;
import main.controladores.UsuarioController;
import main.util.JwtUtil;

public class Main {

    public static void main(String[] args) {

        Javalin.create(config -> {

            //Filtro JWT
            config.routes.before("/api/*", ctx -> {

                if (ctx.path().equals("/api/login"))
                    return;

                String header = ctx.header("Authorization");

                if (header == null || !header.startsWith("Bearer ")) {

                    throw new UnauthorizedResponse(
                            "Token requerido"
                    );
                }

                String token =
                        header.replace("Bearer ", "");

                try {

                    var claims =
                            JwtUtil.validarToken(token);

                    ctx.attribute(
                            "username",
                            claims.get("username")
                    );

                }
                catch (Exception e) {

                    throw new ForbiddenResponse(
                            "Token inválido"
                    );
                }

            });


            // AUTH
            config.routes.post("/api/login", AuthController::login);
            config.routes.post("/api/logout", AuthController::logout);
            config.routes.get("/api/usuario/actual", AuthController::usuarioActual);

            // USUARIOS
            config.routes.get("/api/usuarios", UsuarioController::listarUsuarios);
            config.routes.get("/api/usuarios/{username}", UsuarioController::getUsuarioByUsername);
            config.routes.post("/api/usuarios", UsuarioController::crearUsuario);
            config.routes.put("/api/usuarios", UsuarioController::actualizarUsuario);
            config.routes.delete("/api/usuarios/{id}", UsuarioController::eliminarUsuario);

            // FORMULARIOS
            config.routes.get("/api/formularios", FormularioController::listarFormularios);
            config.routes.get("/api/formularios/{id}", FormularioController::getFormularioPorId);
            config.routes.get("/api/formularios/usuario/{username}", FormularioController::listarFormulariosPorUsuario);
            config.routes.post("/api/formularios", FormularioController::crearFormulario);
            config.routes.put("/api/formularios", FormularioController::actualizarFormulario);
            config.routes.delete("/api/formularios/{id}", FormularioController::eliminarFormulario);

        }).start(7000);
    }
}