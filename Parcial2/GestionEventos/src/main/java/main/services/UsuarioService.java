package main.services;

import main.models.Usuario;
import java.util.List;

public class UsuarioService {

    private static UsuarioService instancia;
    private GestionDb<Usuario> db = new GestionDb<>(Usuario.class);

    private UsuarioService() {}

    public static UsuarioService getInstancia() {
        if (instancia == null) {
            instancia = new UsuarioService();
        }
        return instancia;
    }

    public List<Usuario> findAll() {
        return db.findAll();
    }

    public Usuario find(Long id) {
        return db.find(id);
    }

    public Usuario crear(Usuario usuario) {
        return db.crear(usuario);
    }

    public Usuario editar(Usuario usuario) {
        return db.editar(usuario);
    }

    public boolean eliminar(Long id) {
        return db.eliminar(id);
    }
}