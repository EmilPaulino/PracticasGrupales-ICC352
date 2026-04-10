package main.servicios;

import dev.morphia.Datastore;
import dev.morphia.query.filters.Filters;
import main.entidades.Usuario;
import org.bson.types.ObjectId;

import java.util.List;

public class UsuarioService {

    private static UsuarioService instancia;

    private Datastore ds;

    private UsuarioService() {
        ds = MongoDBConexion.getInstance().getDatastore();
    }

    public static UsuarioService getInstancia() {
        if (instancia == null) {
            instancia = new UsuarioService();
        }
        return instancia;
    }

    public List<Usuario> findAll() {
        return ds.find(Usuario.class)
                .iterator()
                .toList();
    }

    public Usuario findById(String id) {
        if (!ObjectId.isValid(id)) {
            return null;
        }

        return ds.find(Usuario.class)
                .filter(Filters.eq("_id", new ObjectId(id)))
                .first();
    }

    public Usuario findByUsername(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }

        return ds.find(Usuario.class)
                .filter(Filters.eq("username", username))
                .first();
    }

    public Usuario crear(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        if (findByUsername(usuario.getUsername()) != null) {
            return null;
        }

        ds.save(usuario);
        return usuario;
    }

    public Usuario editar(Usuario usuario) {
        if (usuario == null || usuario.getId() == null) {
            return null;
        }

        Usuario existente = findById(usuario.getId().toHexString());
        if (existente == null) {
            return null;
        }

        existente.setNombre(usuario.getNombre());
        existente.setRol(usuario.getRol());

        if (usuario.getPassword() != null &&
                !usuario.getPassword().isBlank()) {
            existente.setPassword(usuario.getPassword());
        }

        ds.save(existente);
        return existente;
    }

    public boolean eliminar(String id) {
        Usuario usuario = findById(id);
        if (usuario == null) {
            return false;
        }
        ds.delete(usuario);
        return true;
    }
}