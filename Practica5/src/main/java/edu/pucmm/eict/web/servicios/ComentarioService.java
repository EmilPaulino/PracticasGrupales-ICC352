package edu.pucmm.eict.web.servicios;

import edu.pucmm.eict.web.entidades.Comentario;


public class ComentarioService extends GestionDb<Comentario> {

    private static ComentarioService instancia;

    private ComentarioService() {
        super(Comentario.class);
    }

    public static ComentarioService getInstancia() {
        if (instancia == null) {
            instancia = new ComentarioService();
        }
        return instancia;
    }

    public Comentario crearComentario(Comentario comentario) {
        return crear(comentario);
    }

    public boolean eliminarComentario(Long id) {
        return eliminar(id);
    }


}