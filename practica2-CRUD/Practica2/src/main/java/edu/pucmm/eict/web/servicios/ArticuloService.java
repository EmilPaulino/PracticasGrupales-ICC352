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
    private ArrayList<Etiqueta> todasLasEtiquetas = new ArrayList<>();
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
        if (autor == null) {
            return false;
        }
        titulo = titulo.trim();
        cuerpo = cuerpo.trim();
        if (titulo.isEmpty() || cuerpo.isEmpty()) {
            return false;
        }
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
        titulo = titulo.trim();
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
    public boolean actualizarArticulo(long id, String titulo, String cuerpo, Usuario usuario){
        if (usuario == null){
            return false;
        }
        Articulo articulo = buscarPorId(id);
        if(articulo == null){
            return false;
        }
        if (!usuario.getAdministrator() && !articulo.getAutor().equals(usuario)) {
            return false;
        }
        titulo = titulo.trim();
        cuerpo = cuerpo.trim();
        if (titulo.isEmpty() || cuerpo.isEmpty()) {
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
    public boolean eliminarArticulo(long id, Usuario usuario){
        if (usuario == null){
            return false;
        }
        Articulo articulo = buscarPorId(id);
        if (articulo == null){
            return false;
        }
        if (usuario.getAdministrator() || articulo.getAutor().equals(usuario)) {
            losArticulos.remove(articulo);
            return true;
        }
        return false;
    }

    /*
    * Crea un nuevo comentario en el artículo.
    * */
    public boolean agregarComentario(long articuloId, String contenido, Usuario autor) {
        Articulo articulo = buscarPorId(articuloId);
        if (articulo == null){
            return false;
        }
        contenido = contenido.trim();
        if (contenido.isEmpty()){
            return false;
        }
        if (autor == null){
            return false;
        }
        Comentario comentario = new Comentario(contadorComentarioID++, contenido, autor);
        articulo.getListaComentarios().add(comentario);
        return true;
    }

    /*
    * Elimina un comentario de un artículo.
    * */
    public boolean eliminarComentario(long articuloId, long comentarioId, Usuario usuario) {
        if (usuario == null) {
            return false;
        }
        Articulo articulo = buscarPorId(articuloId);
        if (articulo == null) {
            return false;
        }
        Comentario comentario = buscarComentarioPorId(articulo, comentarioId);
        if (comentario == null) {
            return false;
        }
        if (usuario.getAdministrator() || usuario.getAutor() || comentario.getAutor().equals(usuario)) {
            articulo.getListaComentarios().remove(comentario);
            return true;
        }
        return false;
    }


    /*
    * Busca un comentario por id.
    * */
    public Comentario buscarComentarioPorId(Articulo articulo, long comentarioId) {
        for (Comentario c : articulo.getListaComentarios()) {
            if (c.getId() == comentarioId) {
                return c;
            }
        }
        return null;
    }

    /*
    * Crea una etiqueta
    * */
    public boolean agregarEtiqueta(long articuloId, String contenido) {
        Articulo articulo = buscarPorId(articuloId);
        if (articulo == null) {
            return false;
        }
        contenido = contenido.trim();
        if (contenido.isEmpty()) {
            return false;
        }
        Etiqueta etiqueta = buscarEtiquetaPorNombre(contenido);
        if (etiqueta == null) {
            etiqueta = new Etiqueta(contadorEtiquetaID++, contenido);
            todasLasEtiquetas.add(etiqueta);
        }
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
        if (articulo == null) {
            return false;
        }
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
