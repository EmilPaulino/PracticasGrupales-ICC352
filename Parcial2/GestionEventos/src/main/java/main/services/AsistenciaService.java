package main.services;

import main.models.Asistencia;
import java.util.List;

public class AsistenciaService {

    private static AsistenciaService instancia;
    private GestionDb<Asistencia> db = new GestionDb<>(Asistencia.class);

    private AsistenciaService() {}

    public static AsistenciaService getInstancia() {
        if (instancia == null) {
            instancia = new AsistenciaService();
        }
        return instancia;
    }

    public List<Asistencia> findAll() {
        return db.findAll();
    }

    public Asistencia find(Long id) {
        return db.find(id);
    }

    public Asistencia crear(Asistencia asistencia) {
        return db.crear(asistencia);
    }

    public long contarPorEvento(Long eventoId) {
        return db.findAll().stream()
                .filter(a -> a.getInscripcion().getEvento().getId().equals(eventoId))
                .count();
    }

    public boolean yaAsistio(Long inscripcionId) {
        return db.findAll().stream()
                .anyMatch(a -> a.getInscripcion().getId().equals(inscripcionId));
    }

    public List<Asistencia> findPorEvento(Long eventoId) {
        return db.findAll().stream()
                .filter(a -> a.getInscripcion().getEvento().getId().equals(eventoId))
                .toList();
    }
}