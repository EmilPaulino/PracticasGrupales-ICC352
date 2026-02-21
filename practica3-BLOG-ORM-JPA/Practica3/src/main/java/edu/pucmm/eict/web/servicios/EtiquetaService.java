package edu.pucmm.eict.web.servicios;

import edu.pucmm.eict.web.entidades.Articulo;
import edu.pucmm.eict.web.entidades.Etiqueta;
import jakarta.persistence.EntityManager;
import java.util.List;

public class EtiquetaService extends GestionDb<Etiqueta> {

    private static EtiquetaService instancia;

    private EtiquetaService() {
        super(Etiqueta.class);
    }

    public static EtiquetaService getInstancia() {
        if (instancia == null) {
            instancia = new EtiquetaService();
        }
        return instancia;
    }

    public Etiqueta agregarEtiqueta(String nombre) {
        nombre = nombre.trim();
        if(nombre.isEmpty()) return null;

        EntityManager em = getEntityManager();
        try {
            List<Etiqueta> lista = em.createQuery(
                            "SELECT e FROM Etiqueta e WHERE LOWER(e.etiqueta) = :nombre", Etiqueta.class)
                    .setParameter("nombre", nombre.toLowerCase())
                    .getResultList();
            if(!lista.isEmpty()) return lista.get(0);
        } finally {
            em.close();
        }

        Etiqueta e = new Etiqueta();
        e.setEtiqueta(nombre);
        return crear(e);
    }

    public boolean eliminarEtiqueta(Long id) {
        return eliminar(id);
    }

    public List<Etiqueta> findAll() {
        return super.findAll();
    }

    public Etiqueta buscarPorId(Long id) {
        EntityManager em = getEntityManager();
        try {
            Etiqueta e = em.find(Etiqueta.class, id);
            if(e != null) e.getListaArticulos().size(); // inicializar relación
            return e;
        } finally {
            em.close();
        }
    }
}