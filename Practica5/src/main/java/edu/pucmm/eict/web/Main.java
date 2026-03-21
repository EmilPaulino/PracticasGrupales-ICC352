package edu.pucmm.eict.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import edu.pucmm.eict.web.contoladores.ArticuloController;
import edu.pucmm.eict.web.contoladores.LoginController;
import edu.pucmm.eict.web.contoladores.UsuarioController;
import edu.pucmm.eict.web.entidades.Articulo;
import edu.pucmm.eict.web.entidades.Usuario;
import edu.pucmm.eict.web.servicios.ArticuloService;
import edu.pucmm.eict.web.servicios.BootStrapServices;
import edu.pucmm.eict.web.servicios.EtiquetaService;
import edu.pucmm.eict.web.servicios.UsuarioService;
import edu.pucmm.eict.web.util.EncryptUtil;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.staticfiles.Location;
import io.javalin.json.JavalinJackson;
import io.javalin.rendering.template.JavalinThymeleaf;

import java.util.List;
import java.util.Map;

public class Main {
    private static int parsePage(Context ctx) {
        String pageParam = ctx.queryParam("page");
        if (pageParam == null) return 0;
        try {
            return Integer.parseInt(pageParam);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static void setAtributosComunes(Context ctx, List<?> articulos, long total, int page, EtiquetaService etiquetaService) {
        int totalPaginas = (int) Math.ceil(total / 5.0);
        ctx.attribute("articulos", articulos);
        ctx.attribute("etiquetas", etiquetaService.findAll());
        ctx.attribute("page", page);
        ctx.attribute("totalPaginas", totalPaginas);
    }

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

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new Hibernate6Module());
            config.jsonMapper(new JavalinJackson(mapper, false));
        });

        BootStrapServices.getInstancia().init();

        //Endpoint para la /, es decir, index
        app.get("/", ctx -> {
            int page = parsePage(ctx);
            ArticuloService articuloService = ArticuloService.getInstancia();
            EtiquetaService etiquetaService = EtiquetaService.getInstancia();
            setAtributosComunes(ctx, articuloService.listarPaginado(page), articuloService.contarArticulos(), page, etiquetaService);
            ctx.render("templates/index.html");
        });

        //Endpoint para filtrar por etiqueta en el index
        app.get("/etiqueta/{nombre}", ctx -> {
            String nombre = ctx.pathParam("nombre");
            int page = parsePage(ctx);
            ArticuloService articuloService = ArticuloService.getInstancia();
            EtiquetaService etiquetaService = EtiquetaService.getInstancia();

            setAtributosComunes(ctx,
                    articuloService.listarPorEtiquetaPaginado(nombre, page),
                    articuloService.contarPorEtiqueta(nombre),
                    page,
                    etiquetaService);

            ctx.attribute("etiquetaSeleccionada", nombre);
            ctx.render("templates/index.html");
        });

        //Rutas para el fetch
        app.get("/api/articulos", ctx -> {
            int page = parsePage(ctx);
            ArticuloService articuloService = ArticuloService.getInstancia();

            long total = articuloService.contarArticulos();
            int totalPaginas = (int) Math.ceil(total / 5.0);

            ctx.json(Map.of(
                    "articulos",    articuloService.listarPaginado(page),
                    "page",         page,
                    "totalPaginas", totalPaginas
            ));
        });

        app.get("/api/etiqueta/{nombre}", ctx -> {
            String nombre = ctx.pathParam("nombre");
            int page = parsePage(ctx);
            ArticuloService articuloService = ArticuloService.getInstancia();

            long total = articuloService.contarPorEtiqueta(nombre);
            int totalPaginas = (int) Math.ceil(total / 5.0);

            ctx.json(Map.of(
                    "articulos",    articuloService.listarPorEtiquetaPaginado(nombre, page),
                    "page",         page,
                    "totalPaginas", totalPaginas,
                    "etiqueta",     nombre
            ));
        });

        //Endpoints para Login
        LoginController loginController = new LoginController();
        app.get("/logout", loginController::logout);
        app.post("/procesarLogin", loginController::procesarLogin);
        app.before(ctx -> {
            Usuario user = ctx.sessionAttribute("user");
            if (user == null) {
                String cookie = ctx.cookie("rememberMe");
                if (cookie != null) {
                    try {
                        String username = EncryptUtil.decrypt(cookie);
                        UsuarioService usuarioService = new UsuarioService();
                        Usuario usuario = usuarioService.buscarPorUsername(username);
                        if (usuario != null) {
                            ctx.sessionAttribute("user", usuario);
                            user = usuario;
                        }
                    } catch (Exception e) {
                        ctx.removeCookie("rememberMe");
                    }
                }
            }
            if (user != null) {
                ctx.attribute("user", user);
            }
        });

        //Endpoints para Usuarios
        UsuarioController usuarioController = new UsuarioController();
        app.get("/usuarios", usuarioController::listar);
        app.get("/usuarios/crear", usuarioController::formularioCrear);
        app.post("/usuarios/crear", usuarioController::crear);
        app.get("/usuarios/editar/{id}", usuarioController::formularioEditar);
        app.post("/usuarios/editar/{id}", usuarioController::editar);
        app.get("/usuarios/eliminar/{id}", usuarioController::eliminar);

        //Endpoints para Articulos
        ArticuloController articuloController = new ArticuloController();
        app.get("/articulos", articuloController::listar);
        app.get("/articulos/crear", articuloController::formularioCrear);
        app.post("/articulos/crear", articuloController::crear);
        app.get("/articulos/editar/{id}", articuloController::formularioEditar);
        app.post("/articulos/editar/{id}", articuloController::editar);
        app.get("/articulos/eliminar/{id}", articuloController::eliminar);
        app.get("/articulos/ver/{id}", ctx -> {
            long id = Long.parseLong(ctx.pathParam("id"));
            Articulo articulo = ArticuloService.getInstancia().buscarPorId(id);

            if (articulo == null) {
                ctx.status(404);
                ctx.result("Artículo no encontrado");
                return;
            }

            ctx.attribute("articulo", articulo);
            ctx.render("templates/articulos/verArticulo.html");
        });

        // Endpoints para Comentarios
        app.post("/articulos/{id}/comentarios/agregar", articuloController::agregarComentario);
        app.get("/articulos/{id}/comentarios/eliminar/{comentarioId}", articuloController::eliminarComentario);

        // Endpoints para Etiquetas
        app.post("/articulos/{id}/etiquetas/agregar", articuloController::agregarEtiqueta);
        app.get("/articulos/{id}/etiquetas/eliminar/{etiquetaId}", articuloController::eliminarEtiqueta);

        //Filtros

        //Filtro para que no se pueda acceder al login cuando ya se inicio sesion
        app.get("/login", loginController::mostrarLogin);
        app.before("/login", ctx -> {
            if (ctx.sessionAttribute("user") != null) {
                ctx.redirect("/");
                return;
            }
        });

        //Auto login si la cookie existe
        app.before(ctx -> {
            if (ctx.sessionAttribute("usuario") == null) {
                String cookie = ctx.cookie("rememberMe");
                if (cookie != null) {
                    try {
                        String username = EncryptUtil.decrypt(cookie);
                        UsuarioService usuarioService = new UsuarioService();
                        Usuario usuario = usuarioService.buscarPorUsername(username);
                        if (usuario != null) {
                            ctx.sessionAttribute("usuario", usuario);
                        }
                    } catch (Exception e) {
                        ctx.removeCookie("rememberMe");
                    }
                }
            }
        });

        /*Filtros para que no se pueda acceder a ninguna ruta de gestionar usuarios
          si el usuario no es administrador*/
        app.before("/usuarios/*", ctx -> validarAdmin(ctx));
        app.before("/usuarios", ctx -> validarAdmin(ctx));

        /*Filtros para que no se pueda acceder a las funciones de administrador y
          autor en la gestión de artículos*/
        app.before("/articulos", ctx -> validarAutorOAdmin(ctx));
        app.before("/articulos/crear", ctx -> validarAutorOAdmin(ctx));
        app.before("/articulos/editar/*", ctx -> validarAutorOAdmin(ctx));
        app.before("/articulos/eliminar/*", ctx -> validarAutorOAdmin(ctx));

        //Filtros para que no se puedan manejar las etiquetas y comentarios sin estar logueados
        app.before("/articulos/*/etiquetas/*", ctx -> {
            if (ctx.sessionAttribute("user") == null) {
                ctx.redirect("/login");
                return;
            }
        });
        app.before("/articulos/*/comentarios/eliminar/*", ctx -> {
            if (ctx.sessionAttribute("user") == null) {
                ctx.redirect("/login");
                return;
            }
        });

        //Filtro para que no se puedan agregar comentarios si no se esta logueado
        app.before("/articulos/*/comentarios/agregar", ctx -> {
            if (ctx.sessionAttribute("user") == null) {
                ctx.redirect("/login");
                return;
            }
        });

        //Levanta la aplicacion en el puerto 7000
        app.start(7000);
    }

    private static void validarAdmin(Context ctx) {
        Usuario user = ctx.sessionAttribute("user");

        if (user == null) {
            ctx.redirect("/login");
            return;
        }

        if (!user.isAdministrator()) {
            ctx.redirect("/");
            return;
        }
    }

    private static void validarAutorOAdmin(Context ctx) {
        Usuario user = ctx.sessionAttribute("user");
        if (user == null) {
            ctx.redirect("/login");
            return;
        }
        if (!user.isAdministrator() && !user.isAutor()) {
            ctx.redirect("/");
            return;
        }
    }
}