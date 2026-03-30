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
        if (instancia == null){
            instancia = new UsuarioServices();
        }
        return instancia;
    }

    public List<Usuario> listarUsuarios() {
        return ds.find(Usuario.class).iterator().toList();
    }

    public Usuario getUsuarioByUsername(String username) {
        if (username == null || username.isBlank()){
            return null;
        }
        return ds.find(Usuario.class).filter(Filters.eq("username", username)).first();
    }

    public Usuario getUsuarioById(String id) {
        if (!ObjectId.isValid(id)){
            return null;
        }
        return ds.find(Usuario.class).filter(Filters.eq("_id", new ObjectId(id))).first();
    }

    public Usuario crearUsuario(Usuario usuario) {
        if (usuario == null){
            return null;
        }

        if (usuario.getUsername() == null || usuario.getUsername().isBlank()){
            return null;
        }

        if (usuario.getPassword() == null || usuario.getPassword().isBlank()){
            return null;
        }

        if (getUsuarioByUsername(usuario.getUsername()) != null){
            return null;
        }

        ds.save(usuario);
        return usuario;
    }

    public Usuario actualizarUsuario(Usuario usuario) {

        if (usuario == null || usuario.getId() == null){
            return null;
        }

        Usuario existente = getUsuarioById(usuario.getId().toHexString());

        if (existente == null){
            return null;
        }

        ds.save(usuario);
        return usuario;
    }

    public boolean eliminarUsuario(String id) {
        Usuario usuario = getUsuarioById(id);

        if (usuario == null){
            return false;
        }

        ds.delete(usuario);
        return true;
    }
}