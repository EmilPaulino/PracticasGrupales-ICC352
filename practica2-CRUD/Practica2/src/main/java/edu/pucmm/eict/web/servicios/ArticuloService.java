package edu.pucmm.eict.web.servicios;

import edu.pucmm.eict.web.entidades.Articulo;
import edu.pucmm.eict.web.entidades.Comentario;
import edu.pucmm.eict.web.entidades.Etiqueta;
import edu.pucmm.eict.web.entidades.Usuario;

import java.util.ArrayList;
import java.util.Date;

public class ArticuloService {
    private static ArticuloService instancia;
    private ArrayList<Articulo> losArticulos = new ArrayList<>();
    private long contadorArticuloID = 1;
    private long contadorComentarioID = 1;
    private long contadorEtiquetaID = 1;


    public static ArticuloService getInstancia(){
        if(instancia == null){
            instancia = new ArticuloService();
        }
        return instancia;
    }

    /*
     * Devuelve la lista de artículos
     */
    public ArrayList<Articulo> listarArticulos() {
        return losArticulos;
    }
    public ArrayList<Etiqueta> listarEtiquetas() {
        return todasLasEtiquetas;
    }


    /*
    * Crea un artículo nuevo.
    * */
    public boolean crearArticulo(String titulo, String cuerpo, Usuario autor){

        if(!validarTitulo(contadorArticuloID, titulo)){
            return false;
        }
        losArticulos.add(new Articulo(contadorArticuloID, titulo, cuerpo, autor, new Date()));
        contadorArticuloID++;
        return true;
    }

    /*
    * Valida que el título de un artículo no se repita, para evitar duplicados.
    * */
    public boolean validarTitulo(long id, String titulo){
        for(Articulo a : losArticulos){
            if(a.getTitulo().equalsIgnoreCase(titulo) && a.getId() != id){
                return false;
            }
        }
        return true;
    }

    /*
    * Actualiza la información de un artículo.
    * */
    public boolean actualizarArticulo(long id, String titulo, String cuerpo){
        Articulo articulo = buscarPorId(id);
        if(articulo == null){
            return false;
        }
        if(!validarTitulo(id, titulo)){
            return false;
        }
        articulo.setTitulo(titulo);
        articulo.setCuerpo(cuerpo);
        return true;
    }

    /*
    * Busca un artículo por su id.
    * */
    public Articulo buscarPorId(long id){
        for(Articulo a : losArticulos){
            if(a.getId() == id){
                return a;
            }
        }
        return null;
    }

    /*
     * Elimina un artículo por su id.
     */
    public boolean eliminarArticulo(long id){
        Articulo articulo = buscarPorId(id);
        if(articulo == null){
            return false;
        }
        losArticulos.remove(articulo);
        return true;
    }

    /*
    * Crea un nuevo comentario en el artículo.
    * */
    public boolean agregarComentario(long articuloId, String contenido, Usuario autor) {
        Articulo articulo = buscarPorId(articuloId);
        if (articulo == null){
            return false;
        }
        Comentario comentario = new Comentario(contadorComentarioID++, contenido, autor);
        articulo.getListaComentarios().add(comentario);
        return true;
    }

    /*
    * Elimina un comentario de un artículo.
    * */
    public boolean eliminarComentario(long articuloId, long comentarioId) {
        Articulo articulo = buscarPorId(articuloId);
        Comentario comentario = buscarComentarioPorId(articulo, comentarioId);
        if (comentario == null) {
            return false;
        }
        articulo.getListaComentarios().remove(comentario);
        return true;
    }

    /*
    * Busca un comentario por id.
    * */
    public Comentario buscarComentarioPorId(Articulo articulo, long comentarioId) {
        for (Comentario c : articulo.getListaComentarios()) {
            if (c.getId() == comentarioId) {
                return c; // Se encontró el comentario
            }
        }
        return null; // No se encontró
    }

    /*
     * Arreglo global de etiquetas
     * */
    private ArrayList<Etiqueta> todasLasEtiquetas = new ArrayList<>();

    /*
    * Crea una etiqueta
    * */
    public boolean agregarEtiqueta(long articuloId, String contenido) {

        Articulo articulo = buscarPorId(articuloId);
        if (articulo == null) {
            return false;
        }

        contenido = contenido.trim();

        // Buscar si ya existe
        Etiqueta etiqueta = buscarEtiquetaPorNombre(contenido);

        // Si no existe, la crea y la guarda globalmente
        if (etiqueta == null) {
            etiqueta = new Etiqueta(contadorEtiquetaID++, contenido);
            todasLasEtiquetas.add(etiqueta);
        }

        // Evita duplicar etiqueta en el mismo artículo
        if (!articulo.getListaEtiquetas().contains(etiqueta)) {
            articulo.getListaEtiquetas().add(etiqueta);
        }

        return true;
    }

    /*
    * Elimina una etiqueta de un artículo
    * */
    public boolean eliminarEtiqueta(long articuloId, long etiquetaId) {
        Articulo articulo = buscarPorId(articuloId);
        Etiqueta etiqueta = buscarEtiquetaPorId(articulo, etiquetaId);
        if (etiqueta == null) {
            return false;
        }
        articulo.getListaEtiquetas().remove(etiqueta);
        return true;
    }

    /*
    * Busca la etiqueta de un artículo por ID
    * */
    public Etiqueta buscarEtiquetaPorId(Articulo articulo, long etiquetaId) {
        for (Etiqueta e : articulo.getListaEtiquetas()) {
            if (e.getId() == etiquetaId) {
                return e;
            }
        }
        return null;
    }

    /*
     * Busca la etiqueta de por nombre
     * */
    public Etiqueta buscarEtiquetaPorNombre(String contenido){
        for(Etiqueta e : todasLasEtiquetas){
            if(e.getEtiqueta().equalsIgnoreCase(contenido)){
                return e;
            }
        }
        return null;
    }
}
