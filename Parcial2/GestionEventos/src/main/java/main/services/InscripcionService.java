package main.services;

import main.models.Inscripcion;
import java.util.List;

public class InscripcionService {

    private static InscripcionService instancia;
    private GestionDb<Inscripcion> db = new GestionDb<>(Inscripcion.class);

    private InscripcionService() {}

    public static InscripcionService getInstancia() {
        if (instancia == null) {
            instancia = new InscripcionService();
        }
        return instancia;
    }

    public List<Inscripcion> findAll() {
        return db.findAll();
    }

    public Inscripcion find(Long id) {
        return db.find(id);
    }

    public Inscripcion crear(Inscripcion inscripcion) {
        return db.crear(inscripcion);
    }

    public Inscripcion editar(Inscripcion inscripcion) { return db.editar(inscripcion); }

    public boolean eliminar(Long id) {
        return db.eliminar(id);
    }

    public List<Inscripcion> findPorEvento(Long eventoId) {
        return db.findAll().stream()
                .filter(i -> i.getEvento().getId().equals(eventoId))
                .toList();
    }

    public long contarPorEvento(Long eventoId) {
        return findPorEvento(eventoId).size();
    }

    public boolean existeInscripcion(Long usuarioId, Long eventoId) {
        return db.findAll().stream()
                .anyMatch(i -> i.getUsuario().getId().equals(usuarioId)
                        && i.getEvento().getId().equals(eventoId));
    }

    public List<Inscripcion> findPorUsuario(Long usuarioId) {
        return db.findAll().stream()
                .filter(i -> i.getUsuario().getId().equals(usuarioId))
                .toList();
    }
}