package edu.pucmm.eict.web.servicios;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaQuery;
import java.util.List;

public class GestionDb<T> {

    private static EntityManagerFactory emf;
    private Class<T> claseEntidad;

    public GestionDb(Class<T> claseEntidad) {
        // Inicializar EntityManagerFactory una sola vez
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("blogPU");
        }
        this.claseEntidad = claseEntidad;
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    /*
     * Crear una nueva entidad
     **/
    public T crear(T entidad) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(entidad);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return entidad;
    }

    /*
     * Editar una entidad existente
     **/
    public T editar(T entidad) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(entidad);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return entidad;
    }

    /*
     * Eliminar por ID
     **/
    public boolean eliminar(Object id) {
        EntityManager em = getEntityManager();
        boolean ok = false;
        try {
            em.getTransaction().begin();
            T entidad = em.find(claseEntidad, id);
            if (entidad != null) {
                em.remove(entidad);
                ok = true;
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return ok;
    }

    /*
     * Buscar entidad por ID
     **/
    public T find(Object id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(claseEntidad, id);
        } finally {
            em.close();
        }
    }

    /*
     * Listar todas las entidades
     **/
    public List<T> findAll() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery<T> query = em.getCriteriaBuilder().createQuery(claseEntidad);
            query.select(query.from(claseEntidad));
            return em.createQuery(query).getResultList();
        } finally {
            em.close();
        }
    }
}