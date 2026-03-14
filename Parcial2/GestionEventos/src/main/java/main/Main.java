package main;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinThymeleaf;

import main.controllers.AsistenciaController;
import main.controllers.EventoController;
import main.controllers.InscripcionController;
import main.controllers.UsuarioController;

import main.models.Usuario;

import main.services.BootStrapServices;
import main.services.UsuarioService;

import main.util.EncryptUtil;

public class Main {

    public static void main(String[] args){

        Javalin app = Javalin.create(config -> {

            //Archivos estáticos
            config.staticFiles.add("/public", Location.CLASSPATH);

            config.fileRenderer(new JavalinThymeleaf());

            //Middleware para sesión y rememberMe
            config.routes.before(ctx -> {

                Usuario usuario = ctx.sessionAttribute("usuario");

                if (usuario == null) {

                    String cookie = ctx.cookie("rememberMe");

                    if (cookie != null) {
                        try {

                            String email = EncryptUtil.decrypt(cookie);

                            Usuario user = UsuarioService
                                    .getInstancia()
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

            //Ruta principal
            config.routes.get("/", EventoController::listarPublicados);

            config.routes.get("/panel", ctx -> {
                ctx.render("templates/panel/panel.html");
            });

            //Rutas para USUARIOS
            config.routes.get("/usuarios", UsuarioController::listar);
            config.routes.post("/usuarios/crear", UsuarioController::crear);
            config.routes.get("/usuarios/nuevo", UsuarioController::formNuevo);

            config.routes.get("/usuarios/editar/{id}", UsuarioController::formEditar);
            config.routes.post("/usuarios/editar/{id}", UsuarioController::editar);

            config.routes.post("/usuarios/eliminar/{id}", UsuarioController::eliminar);

            config.routes.post("/usuarios/bloquear/{id}", UsuarioController::bloquear);
            config.routes.post("/usuarios/desbloquear/{id}", UsuarioController::desbloquear);

            //Rutas LOGIN y REGISTRO
            config.routes.get("/login", UsuarioController::loginForm);
            config.routes.post("/procesarLogin", UsuarioController::procesarLogin);
            config.routes.get("/logout", UsuarioController::logout);

            config.routes.get("/registrarse", UsuarioController::registroForm);
            config.routes.post("/registrarse", UsuarioController::registroEstudiante);

            //Rutas EVENTOS
            config.routes.get("/eventos", EventoController::listar);
            config.routes.get("/eventos/nuevo", EventoController::formNuevo);
            config.routes.post("/eventos/crear", EventoController::crear);
            config.routes.get("/eventos/editar/{id}", EventoController::formEditar);
            config.routes.post("/eventos/editar/{id}", EventoController::editar);
            config.routes.post("/eventos/cancelar/{id}", EventoController::cancelar);
            config.routes.post("/eventos/publicar/{id}", EventoController::publicar);
            config.routes.post("/eventos/despublicar/{id}", EventoController::desPublicar);
            config.routes.post("/eventos/eliminar/{id}", EventoController::eliminar);
            config.routes.get("/eventos/visualizar/{id}", EventoController::visualizar);
            config.routes.get("/eventos/ver/{id}", EventoController::visualizarPublico);
            config.routes.get("/eventos/inscripciones-por-dia/{id}", EventoController::inscripcionesPorDia);
            config.routes.get("/eventos/asistencia-por-hora/{id}", EventoController::asistenciaPorHora);

            //Rutas para Inscripciones
            config.routes.post("/eventos/inscribirse/{id}", InscripcionController::inscribirse);
            config.routes.get("/mis-inscripciones", InscripcionController::misInscripciones);
            config.routes.post("/inscripciones/cancelar/{id}", InscripcionController::cancelarInscripcion);

            //Rutas para Asistencia
            config.routes.get("/asistencia", AsistenciaController::vistaEscaner);
            config.routes.post("/asistencia/marcar", AsistenciaController::marcarAsistencia);

            //Protección de rutas de eventos
            config.routes.before("/eventos/*", ctx -> {

                Usuario usuario = ctx.sessionAttribute("usuario");

                if (usuario == null) {
                    ctx.redirect("/login");
                    return;
                }

                if (!usuario.getRol().getRol().equals("Organizador") &&
                        !usuario.getRol().getRol().equals("Admin")) {

                    ctx.status(403);
                    ctx.result("No autorizado");
                }

            });

        });

        BootStrapServices.getInstancia().init();
        app.start(7000);
    }
}