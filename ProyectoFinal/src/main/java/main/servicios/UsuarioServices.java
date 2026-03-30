package main.servicios;

import dev.morphia.Datastore;
import dev.morphia.query.filters.Filters;
import main.entidades.Usuario;
import org.bson.types.ObjectId;

import java.util.List;

public class UsuarioServices {

    private static UsuarioServices instancia;
    private final Datastore ds;

    private UsuarioServices() {
        this.ds = MongoDBConexion.getInstance().getDatastore();
    }

    public static UsuarioServices getInstancia() {
        if (instancia == null)
            instancia = new UsuarioServices();
        return instancia;
    }

    // Listar todos
    public List<Usuario> listarUsuarios() {
        return ds.find(Usuario.class).iterator().toList();
    }

    // Buscar por username
    public Usuario getUsuarioByUsername(String username) {
        return ds.find(Usuario.class)
                .filter(Filters.eq("username", username))
                .first();
    }

    // Buscar por id
    public Usuario getUsuarioById(String id) {
        return ds.find(Usuario.class)
                .filter(Filters.eq("_id", new ObjectId(id)))
                .first();
    }

    // Crear — verifica que el username no exista
    public Usuario crearUsuario(Usuario usuario) {
        if (getUsuarioByUsername(usuario.getUsername()) != null)
            return null;   // ya existe → el controller responde 400
        ds.save(usuario);
        return usuario;
    }

    // Actualizar
    public Usuario actualizarUsuario(Usuario usuario) {
        ds.save(usuario);
        return usuario;
    }

    // Eliminar — devuelve false si no existe
    public boolean eliminarUsuario(String id) {
        Usuario usuario = getUsuarioById(id);
        if (usuario == null) return false;
        ds.delete(usuario);
        return true;
    }
}