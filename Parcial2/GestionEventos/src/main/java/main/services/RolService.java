package main.services;

import main.models.Rol;
import java.util.List;

public class RolService {

    private static RolService instancia;
    private GestionDb<Rol> db = new GestionDb<>(Rol.class);

    private RolService() {}

    public static RolService getInstancia() {
        if (instancia == null) {
            instancia = new RolService();
        }
        return instancia;
    }

    public List<Rol> findAll() {
        return db.findAll();
    }

    public Rol find(Long id) {
        return db.find(id);
    }

    public Rol crear(Rol rol) {
        return db.crear(rol);
    }

    public Rol editar(Rol rol) {
        return db.editar(rol);
    }

    public boolean eliminar(Long id) {
        return db.eliminar(id);
    }
}