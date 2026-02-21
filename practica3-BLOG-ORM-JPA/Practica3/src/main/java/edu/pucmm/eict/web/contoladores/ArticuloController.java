package edu.pucmm.eict.web.contoladores;

import edu.pucmm.eict.web.entidades.Articulo;
import edu.pucmm.eict.web.entidades.Comentario;
import edu.pucmm.eict.web.entidades.Etiqueta;
import edu.pucmm.eict.web.entidades.Usuario;
import edu.pucmm.eict.web.servicios.ArticuloService;
import edu.pucmm.eict.web.servicios.ComentarioService;
import edu.pucmm.eict.web.servicios.EtiquetaService;
import edu.pucmm.eict.web.servicios.UsuarioService;
import io.javalin.http.Context;

import java.util.Date;
import java.util.List;

public class ArticuloController {

    private final ArticuloService articuloService = ArticuloService.getInstancia();
    private final ComentarioService comentarioService = ComentarioService.getInstancia();
    private final EtiquetaService etiquetaService = EtiquetaService.getInstancia();


    /*
    * Funcion que lista los articulos
    * */
    public void listar(Context ctx) {
        List<Articulo> articulos = articuloService.findAll();
        ctx.attribute("articulos", articulos);
        ctx.render("templates/articulos/listarArticulos.html");
    }

    /*
    * Funcion que muestra el formulario para crear un articulo
    * */
    public void formularioCrear(Context ctx) {
        ctx.attribute("etiquetas", etiquetaService.findAll());
        ctx.render("templates/articulos/formularioArticulo.html");
    }

    /*
    * Funcion que crea un articulo
    * */
    public void crear(Context ctx) {
        Usuario userSesion = ctx.sessionAttribute("user");
        Usuario autor = UsuarioService.getInstancia().buscarPorId(userSesion.getId());

        Articulo articulo = new Articulo();
        articulo.setTitulo(ctx.formParam("titulo"));
        articulo.setCuerpo(ctx.formParam("cuerpo"));
        articulo.setFecha(new Date());
        articulo.setAutor(autor);

        procesarEtiquetas(ctx, articulo);

        articuloService.crearArticulo(articulo);
        ctx.redirect("/articulos");
    }

    /*
    * Funcion que muestra el formulario de editar un articulo
    * */
    public void formularioEditar(Context ctx) {
        long id = Long.parseLong(ctx.pathParam("id"));
        Articulo articulo = articuloService.buscarPorId(id);

        if (articulo == null) {
            ctx.status(404);
            ctx.result("Artículo no encontrado");
            return;
        }

        String etiquetasTexto = articulo.getListaEtiquetas()
                .stream()
                .map(Etiqueta::getEtiqueta)
                .reduce((a, b) -> a + ", " + b)
                .orElse("");

        ctx.attribute("articulo", articulo);
        ctx.attribute("etiquetasTexto", etiquetasTexto);
        ctx.render("templates/articulos/formularioArticulo.html");
    }

    /*
    * Funcion que actualiza un articulo
    * */
    public void editar(Context ctx) {
        long id = Long.parseLong(ctx.pathParam("id"));
        Articulo articulo = articuloService.buscarPorId(id);

        if (articulo == null) {
            ctx.status(404);
            ctx.result("Artículo no encontrado");
            return;
        }

        articulo.setTitulo(ctx.formParam("titulo"));
        articulo.setCuerpo(ctx.formParam("cuerpo"));

        procesarEtiquetas(ctx, articulo);

        articuloService.actualizarArticulo(articulo);
        ctx.redirect("/articulos");
    }

    /*
    * Funcion que elimina un articulo
    * */
    public void eliminar(Context ctx) {
        long id = Long.parseLong(ctx.pathParam("id"));
        articuloService.eliminarArticulo(id);
        ctx.redirect("/articulos");
    }

    /*
    * Funcion que agrega un comentario a un articulo
    * */
    public void agregarComentario(Context ctx) {
        long articuloId = Long.parseLong(ctx.pathParam("id"));
        Usuario usuario = ctx.sessionAttribute("user");

        Articulo articulo = articuloService.buscarPorId(articuloId);
        if (articulo == null) {
            ctx.status(404);
            return;
        }

        Comentario comentario = new Comentario();
        comentario.setComentario(ctx.formParam("comentario"));
        comentario.setAutor(usuario);
        comentario.setArticulo(articulo);

        comentarioService.crearComentario(comentario);
        ctx.redirect("/articulos/ver/" + articuloId);
    }

    /*
    * Funcion que elimina un comentario de un articulo
    * */
    public void eliminarComentario(Context ctx) {
        long comentarioId = Long.parseLong(ctx.pathParam("comentarioId"));
        long articuloId = Long.parseLong(ctx.pathParam("id"));

        comentarioService.eliminarComentario(comentarioId);
        ctx.redirect("/articulos/ver/" + articuloId);
    }

    /*
    * Funcion que agrega una etiqueta a un articulo
    * */
    public void agregarEtiqueta(Context ctx) {
        long articuloId = Long.parseLong(ctx.pathParam("id"));
        long etiquetaId = Long.parseLong(ctx.formParam("etiquetaId"));

        articuloService.agregarEtiqueta(articuloId, etiquetaId);
        ctx.redirect("/articulos/ver/" + articuloId);
    }

    /*
    * Funcion que elimina una etiqueta de un articulo
    * */
    public void eliminarEtiqueta(Context ctx) {
        long articuloId = Long.parseLong(ctx.pathParam("id"));
        long etiquetaId = Long.parseLong(ctx.pathParam("etiquetaId"));

        Articulo articulo = articuloService.buscarPorId(articuloId);
        Etiqueta etiqueta = etiquetaService.buscarPorId(etiquetaId);

        if (articulo != null && etiqueta != null) {
            articulo.getListaEtiquetas().remove(etiqueta);
            articuloService.actualizarArticulo(articulo);
        }

        ctx.redirect("/articulos/ver/" + articuloId);
    }

    /*
    * Procesa el input de etiquetas del formulario (separadas por comas),
    * busca las etiquetas existentes o las crea si no existen, y las relaciona
    * al artículo. Al editar, reemplaza completamente las etiquetas actuales.
    * */
    private void procesarEtiquetas(Context ctx, Articulo articulo) {
        String etiquetasInput = ctx.formParam("etiquetas");

        articulo.getListaEtiquetas().clear();

        if (etiquetasInput == null || etiquetasInput.isBlank()) {
            return;
        }

        List<Etiqueta> existentes = etiquetaService.findAll();

        String[] nombres = etiquetasInput.split(",");

        for (String nombre : nombres) {
            String nombreLimpio = nombre.trim().toLowerCase();
            if (nombreLimpio.isEmpty()) continue;

            Etiqueta etiqueta = null;

            for (Etiqueta e : existentes) {
                if (e.getEtiqueta().equalsIgnoreCase(nombreLimpio)) {
                    etiqueta = e;
                    break;
                }
            }

            if (etiqueta == null) {
                etiqueta = new Etiqueta();
                etiqueta.setEtiqueta(nombreLimpio);
                etiquetaService.crear(etiqueta);
            }

            articulo.getListaEtiquetas().add(etiqueta);
        }
    }
}