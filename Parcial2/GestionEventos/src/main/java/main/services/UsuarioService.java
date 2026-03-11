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

    public List<Usuario> findPaginado(int pagina, int tamano) {
        return db.findPaginado(pagina, tamano);
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

    public Usuario login(String email, String password){

        for(Usuario u : findAll()){
            if(u.getEmail().equals(email) &&
                    u.getPassword().equals(password)){
                return u;
            }
        }

        return null;
    }

    public Usuario buscarPorEmail(String email){
        for(Usuario u : findAll()){
            if(u.getEmail().equals(email)){
                return u;
            }
        }
        return null;
    }

    public long contarTotal() {
        return db.contarTotal();
    }
}