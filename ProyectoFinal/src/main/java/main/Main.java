package main;

import io.javalin.Javalin;
import io.javalin.http.*;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinThymeleaf;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import main.controladores.AuthController;
import main.controladores.FormularioController;
import main.controladores.RestController;
import main.controladores.UsuarioController;
import main.entidades.Rol;
import main.entidades.Usuario;
import main.servicios.UsuarioService;
import main.util.EncryptUtil;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        Javalin.create(config -> {

            config.http.maxRequestSize = 50_000_000;

            config.jetty.modifyWebSocketServletFactory(factory -> {
                factory.setMaxTextMessageSize(50 * 1024 * 1024); // 50 MB
                factory.setIdleTimeout(java.time.Duration.ofSeconds(60));
            });

            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(it -> {
                    it.anyHost();
                });
            });

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
                Rol rol = ctx.sessionAttribute("rol");
                if (username == null) {
                    ctx.redirect("/login");
                    return;
                }

                if (rol == null || rol != Rol.ADMIN) {
                    ctx.redirect("/formularios");
                    return;
                }
            });

            // Filtro para rutas sin login
            config.routes.before(ctx -> {
                String path = ctx.path();
                if (path.equals("/login") || path.startsWith("/api") || path.startsWith("/css") || path.startsWith("/js") || path.startsWith("/imgs") || path.startsWith("/js")) {
                    return;
                }
                if (ctx.sessionAttribute("username") == null) {
                    ctx.redirect("/login");
                    return;
                }
            });

            // Filtro API
            config.routes.before("/api/formularios", RestController::filtroJwt);

            //Endpoint /
            config.routes.get("/", ctx -> {
                ctx.redirect("/formularios");
            });

            //Endpoints Auth
            config.routes.get("/login", AuthController::mostrarLogin);
            config.routes.post("/login", AuthController::login);
            config.routes.get("/logout", AuthController::logout);

            //API Servicio Rest Auth
            config.routes.post("/api/login", RestController::loginApi);

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

            config.routes.get("/admin/formularios", FormularioController::listarFormulariosAdmin);
            config.routes.get("/admin/formularios/ver/{id}", FormularioController::verFormularioAdmin);

            config.routes.get("/formularios", FormularioController::vistaPrincipal);
            config.routes.get("/formularios/crear", FormularioController::mostrarFormulario);
            config.routes.get("/formularios/ver/{id}", FormularioController::verFormulario);
            config.routes.get("/formularios/editar", FormularioController::mostrarFormulario);

            // API Servicio Rest Formularios
            config.routes.get("/api/formularios", RestController::listarFormulariosApi);
            config.routes.post("/api/formularios", RestController::crearFormularioApi);


            config.routes.ws("/ws/formularios", wsConfig -> {

                wsConfig.onConnect(session -> {
                    System.out.println("Cliente conectado");
                });

                wsConfig.onMessage(session -> {
                    try {
                        System.out.println("Mensaje recibido, tamaño: " + session.message().length());

                        // El controlador procesa y devuelve "OK" o "ERROR"
                        String resultado = FormularioController.sincronizarFormulariosWS(session.message());

                        session.send(resultado);

                    } catch (Exception e) {
                        e.printStackTrace();
                        try {
                            session.send("ERROR");
                        } catch (Exception ignored) {
                        }
                    }
                });

                wsConfig.onClose(session -> {
                    System.out.println("Cliente desconectado");
                });

                wsConfig.onError(session -> {
                    System.err.println("Error en sesión");
                });

            });

        }).start(7000);

    }

}