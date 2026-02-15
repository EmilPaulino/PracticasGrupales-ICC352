package edu.pucmm.eict.web.contoladores;

import edu.pucmm.eict.web.entidades.Articulo;
import edu.pucmm.eict.web.entidades.Usuario;
import edu.pucmm.eict.web.servicios.ArticuloService;
import io.javalin.http.Context;

import java.util.ArrayList;

public class ArticuloController {
    private ArticuloService articuloService = ArticuloService.getInstancia();

    /*
     * Lista todos los artículos
     */
    public void listar(Context ctx){
        ArrayList<Articulo> articulos = articuloService.getInstancia().listarArticulos();
        ctx.attribute("articulos", articulos);
        ctx.render("templates/articulos/listarArticulos.html");
    }

    /*
     * Muestra un articulo seleccionado
     */
    public void ver(Context ctx){
        long id = Long.parseLong(ctx.pathParam("id"));
        Articulo articulo = articuloService.buscarPorId(id);

        if(articulo == null){
            ctx.status(404);
            return;
        }

        ctx.attribute("articulo", articulo);
        ctx.render("templates/articulos/verArticulo.html");
    }

    /*
     * Muestra el formulario para crear artículo
     */
    public void formularioCrear(Context ctx){
        ctx.attribute("etiquetasTexto", "");
        ctx.render("templates/articulos/formularioArticulo.html");
    }

    /*
     * Procesa la creación de un artículo
     */
    public void crear(Context ctx){
        String titulo = ctx.formParam("titulo");
        String cuerpo = ctx.formParam("cuerpo");
        String etiquetasTexto = ctx.formParam("etiquetas");

        Usuario autor = (Usuario) ctx.sessionAttribute("user");

        boolean creado = articuloService.crearArticulo(titulo, cuerpo, autor);

        if(!creado){
            ctx.attribute("error", "El artículo ya existe");
            ctx.attribute("etiquetasTexto", etiquetasTexto);
            ctx.render("templates/articulos/formularioArticulo.html");
            return;
        }

        // Obtener el artículo recién creado
        ArrayList<Articulo> lista = articuloService.listarArticulos();
        Articulo articulo = lista.get(lista.size() - 1);

        // Procesar etiquetas
        if(etiquetasTexto != null && !etiquetasTexto.isBlank()){
            String[] etiquetas = etiquetasTexto.split(",");

            for(String e : etiquetas){
                articuloService.agregarEtiqueta(articulo.getId(), e.trim());
            }
        }

        ctx.redirect("/articulos");
    }


    /*
     * Muestra el formulario para editar un artículo
     */
    public void formularioEditar(Context ctx){
        long id = Long.parseLong(ctx.pathParam("id"));
        Articulo articulo = articuloService.buscarPorId(id);

        if(articulo == null){
            ctx.status(404);
            return;
        }

        ctx.attribute("articulo", articulo);

        //Convierte lista de etiquetas a texto
        StringBuilder etiquetas = new StringBuilder();

        for(var e : articulo.getListaEtiquetas()){
            if(etiquetas.length() > 0){
                etiquetas.append(", ");
            }
            etiquetas.append(e.getEtiqueta());
        }

        ctx.attribute("etiquetasTexto", etiquetas.toString());

        ctx.render("templates/articulos/formularioArticulo.html");
    }

    /*
     * Procesa la edición de un artículo
     */
    public void editar(Context ctx){
        long id = Long.parseLong(ctx.pathParam("id"));
        String titulo = ctx.formParam("titulo");
        String cuerpo = ctx.formParam("cuerpo");
        String etiquetasTexto = ctx.formParam("etiquetas");

        boolean actualizado = articuloService.actualizarArticulo(id, titulo, cuerpo);

        if(!actualizado){
            ctx.attribute("error", "El artículo ya existe o no fue encontrado");
            ctx.attribute("etiquetasTexto", etiquetasTexto);
            ctx.render("templates/articulos/formularioArticulo.html");
            return;
        }

        Articulo articulo = articuloService.buscarPorId(id);

        // Limpia etiquetas actuales
        articulo.getListaEtiquetas().clear();

        // Vuelve a agregarlas
        if(etiquetasTexto != null && !etiquetasTexto.isBlank()){
            String[] etiquetas = etiquetasTexto.split(",");

            for(String e : etiquetas){
                articuloService.agregarEtiqueta(id, e.trim());
            }
        }

        ctx.redirect("/articulos");
    }

    /*
     * Elimina un artículo
     */
    public void eliminar(Context ctx){
        long id = Long.parseLong(ctx.pathParam("id"));
        boolean eliminado = articuloService.eliminarArticulo(id);
        if(!eliminado){
            ctx.status(404);
            return;
        }
        ctx.redirect("/articulos");
    }

    /*
    * Agrega un nuevo comentario a un artículo.
    * */
    public void agregarComentario(Context ctx){
        long articuloId = Long.parseLong(ctx.pathParam("id"));
        String texto = ctx.formParam("texto");
        Usuario autor = (Usuario) ctx.sessionAttribute("user");
        boolean agregado = articuloService.agregarComentario(articuloId, texto, autor);
        if(!agregado){
            ctx.status(400);
            return;
        }
        ctx.redirect("/articulos/ver/" + articuloId);
    }

    /*
    * Elimina un comentario de un artículo.
    * */
    public void eliminarComentario(Context ctx){
        long articuloId = Long.parseLong(ctx.pathParam("id"));
        long comentarioId = Long.parseLong(ctx.pathParam("comentarioId"));
        boolean eliminado = articuloService.eliminarComentario(articuloId, comentarioId);
        if(!eliminado){
            ctx.status(404);
            return;
        }
        ctx.redirect("/articulos/editar/" + articuloId);
    }

    /*
    * Agrega una etiqueta a un artículo.
    * */
    public void agregarEtiqueta(Context ctx){
        long articuloId = Long.parseLong(ctx.pathParam("id"));
        String nombre = ctx.formParam("nombre");
        boolean agregado = articuloService.agregarEtiqueta(articuloId, nombre);
        if(!agregado){
            ctx.status(400);
            return;
        }
        ctx.redirect("/articulos/editar/" + articuloId);
    }

    /*
    * Elimina una etiqueta de un artículo
    * */
    public void eliminarEtiqueta(Context ctx){
        long articuloId = Long.parseLong(ctx.pathParam("id"));
        long etiquetaId = Long.parseLong(ctx.pathParam("etiquetaId"));
        boolean eliminado = articuloService.eliminarEtiqueta(articuloId, etiquetaId);
        if(!eliminado){
            ctx.status(404).result("Etiqueta no encontrada");
            return;
        }
        ctx.redirect("/articulos/editar/" + articuloId);
    }
}
