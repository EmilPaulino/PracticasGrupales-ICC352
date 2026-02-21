package edu.pucmm.eict.web.servicios;

import edu.pucmm.eict.web.entidades.Articulo;
import edu.pucmm.eict.web.entidades.Etiqueta;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.util.List;

public class ArticuloService extends GestionDb<Articulo> {

    private static ArticuloService instancia;

    private ArticuloService() {
        super(Articulo.class);
    }

    public static ArticuloService getInstancia() {
        if (instancia == null) {
            instancia = new ArticuloService();
        }
        return instancia;
    }

    public Articulo crearArticulo(Articulo articulo) {
        return crear(articulo);
    }

    public Articulo actualizarArticulo(Articulo articulo) {
        return editar(articulo);
    }

    public boolean eliminarArticulo(Long id) {
        return eliminar(id);
    }

    public Articulo buscarPorId(Long id) {
        EntityManager em = getEntityManager();
        try {
            Articulo a = em.find(Articulo.class, id);
            if (a != null) {
                a.getListaEtiquetas().size();
                a.getListaComentarios().size();
                a.getListaComentarios()
                        .forEach(c -> c.getAutor().getNombre());
                if (a.getAutor() != null) {
                    a.getAutor().getNombre();
                }
            }
            return a;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Articulo> findAll() {
        EntityManager em = getEntityManager();
        Query query = em.createQuery("select distinct a from Articulo a left join fetch a.listaComentarios left join fetch a.listaEtiquetas order by a.fecha desc");
        List<Articulo> listaArticulos = query.getResultList();
        em.close();
        return listaArticulos;
    }

    public void agregarEtiqueta(Long articuloId, Long etiquetaId) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Articulo articulo = em.find(Articulo.class, articuloId);
            Etiqueta etiqueta = em.find(Etiqueta.class, etiquetaId);
            articulo.getListaEtiquetas().add(etiqueta);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

}