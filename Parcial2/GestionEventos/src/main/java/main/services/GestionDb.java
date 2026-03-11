package main.services;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import java.util.List;

public class GestionDb<T> {

    private static EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("eventosPUCMM");

    private Class<T> claseEntidad;

    public GestionDb(Class<T> claseEntidad) {
        this.claseEntidad = claseEntidad;
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

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

    public boolean eliminar(Object id) {

        EntityManager em = getEntityManager();
        boolean ok = false;

        try {

            em.getTransaction().begin();

            T entidad = em.find(claseEntidad, id);

            if(entidad != null){
                em.remove(entidad);
                ok = true;
            }

            em.getTransaction().commit();

        } finally {
            em.close();
        }

        return ok;
    }

    public T find(Object id){

        EntityManager em = getEntityManager();

        try{
            return em.find(claseEntidad, id);
        }finally{
            em.close();
        }

    }

    public List<T> findAll(){

        EntityManager em = getEntityManager();

        try{

            CriteriaQuery<T> query = em.getCriteriaBuilder().createQuery(claseEntidad);

            query.select(query.from(claseEntidad));

            return em.createQuery(query).getResultList();

        }finally{
            em.close();
        }

    }

    public List<T> findPaginado(int pagina, int tamano) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery<T> query = em.getCriteriaBuilder().createQuery(claseEntidad);
            query.select(query.from(claseEntidad));
            return em.createQuery(query)
                    .setFirstResult(pagina * tamano)
                    .setMaxResults(tamano)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public long contarTotal() {
        EntityManager em = getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> query = cb.createQuery(Long.class);
            query.select(cb.count(query.from(claseEntidad)));
            return em.createQuery(query).getSingleResult();
        } finally {
            em.close();
        }
    }

}