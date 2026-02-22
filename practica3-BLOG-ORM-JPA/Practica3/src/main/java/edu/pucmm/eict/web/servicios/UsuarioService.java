package edu.pucmm.eict.web.servicios;

import edu.pucmm.eict.web.entidades.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

public class UsuarioService extends GestionDb<Usuario> {

    private static UsuarioService instancia;

    public UsuarioService() {
        super(Usuario.class);
    }

    public static UsuarioService getInstancia() {
        if (instancia == null) {
            instancia = new UsuarioService();
        }
        return instancia;
    }

    /**
     * Valida login por username y password
     */
    public Usuario validarLogin(String username, String password) {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery(
                    "SELECT u FROM Usuario u WHERE u.username = :username AND u.password = :password"
            );
            q.setParameter("username", username);
            q.setParameter("password", password);
            return (Usuario) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Crear un usuario
     */
    public Usuario crearUsuario(Usuario usuario) {
        return crear(usuario);
    }

    /**
     * Editar un usuario
     */
    public Usuario actualizarUsuario(Usuario usuario) {
        return editar(usuario);
    }

    /**
     * Eliminar un usuario por ID
     */
    public boolean eliminarUsuario(Long id) {
        return eliminar(id);
    }

    /**
    * Buscar por id
    */
    public Usuario buscarPorId(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Usuario.class, id);
        } finally {
            em.close();
        }
    }

    /**
     * Busca por username
     */
    public Usuario buscarPorUsername(String username) {

        EntityManager em = getEntityManager();
        Usuario usuario = null;

        try {
            TypedQuery<Usuario> query = em.createQuery(
                    "SELECT u FROM Usuario u WHERE u.username = :username",
                    Usuario.class
            );
            query.setParameter("username", username);
            usuario = query.getSingleResult();
        } catch (NoResultException e) {
            usuario = null;
        } finally {
            em.close();
        }
        return usuario;
    }
}