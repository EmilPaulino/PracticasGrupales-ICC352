package main;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinThymeleaf;

import main.controladores.AuthController;
import main.controladores.FormularioController;
import main.controladores.UsuarioController;
import main.entidades.Usuario;
import main.servicios.UsuarioService;
import main.util.EncryptUtil;

public class Main {

    public static void main(String[] args) {

        Javalin.create(config -> {

            config.fileRenderer(new JavalinThymeleaf());

            config.staticFiles.add(staticFileConfig -> {
                staticFileConfig.hostedPath = "/";
                staticFileConfig.directory = "/public";
                staticFileConfig.location = Location.CLASSPATH;
                staticFileConfig.aliasCheck = null;
            });

            //Cookie RememberMe
            config.routes.before(ctx -> {

                String username = ctx.sessionAttribute("username");

                if (username == null) {

                    String cookie = ctx.cookie("rememberMe");

                    if (cookie != null) {
                        try {

                            String usernameDecrypted = EncryptUtil.decrypt(cookie);

                            Usuario usuario = UsuarioService.getInstancia().findByUsername(usernameDecrypted);

                            if (usuario != null) {
                                ctx.sessionAttribute("username", usuario.getUsername());
                                ctx.sessionAttribute("nombre", usuario.getNombre());
                                username = usuario.getUsername();
                            }

                        } catch (Exception e) {
                            ctx.removeCookie("rememberMe");
                        }
                    }
                }

            });

            //Filtro Admin
            config.routes.before("/admin/{*}", ctx -> {
                String username = ctx.sessionAttribute("username");
                if (username == null) {
                    ctx.redirect("/login");
                }
            });

            //Endpoints Auth
            config.routes.get("/login", AuthController::mostrarLogin);
            config.routes.post("/login", AuthController::login);
            config.routes.get("/logout", AuthController::logout);

            // ===============================
            // HOME
            // ===============================

            config.routes.get("/", ctx -> {
                ctx.render("templates/formulario/listarFormEnc.html");
            });

            //Endpoint Panel
            config.routes.get("/admin/panel", ctx -> {
                ctx.render("templates/panel/panel.html");
            });

            //Endpoints Usuarios
            config.routes.get("/admin/usuarios", UsuarioController::listarUsuarios);
            config.routes.get("/admin/usuarios/formulario/crear", UsuarioController::mostrarFormularioCrear);
            config.routes.get("/admin/usuarios/formulario/editar/{id}", UsuarioController::mostrarFormularioEditar);
            config.routes.post("/admin/usuarios/crear", UsuarioController::crearUsuario);
            config.routes.post("/admin/usuarios/actualizar", UsuarioController::actualizarUsuario);
            config.routes.get("/admin/usuarios/eliminar/{id}", UsuarioController::eliminarUsuario);

            //Endpoints Formularios
            config.routes.get("/formularios", FormularioController::listarFormularios);
            config.routes.get("/formularios/usuario", FormularioController::listarFormulariosPorUsuario);
            config.routes.get("/formularios/{id}", FormularioController::getFormularioPorId);
            config.routes.post("/formularios/crear", FormularioController::crearFormulario);
            config.routes.post("/formularios/actualizar", FormularioController::actualizarFormulario);
            config.routes.get("/formularios/eliminar/{id}", FormularioController::eliminarFormulario);

        }).start(7000);

    }

}