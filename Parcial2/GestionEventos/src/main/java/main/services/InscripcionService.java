package main.services;

import main.models.Inscripcion;
import main.models.Evento;
import main.models.Usuario;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

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

    public Inscripcion inscribir(Usuario usuario, Evento evento){

        if(existeInscripcion(usuario.getId(), evento.getId())){
            return null;
        }

        if(contarPorEvento(evento.getId()) >= evento.getCupoMaximo()){
            return null;
        }

        String token = UUID.randomUUID().toString();

        Inscripcion inscripcion = new Inscripcion(
                LocalDate.now(),
                LocalTime.now(),
                token,
                usuario,
                evento
        );

        return crear(inscripcion);
    }

    public boolean marcarAsistencia(Long inscripcionId) {
        Inscripcion inscripcion = db.find(inscripcionId);

        if (inscripcion == null) {
            return false;
        }

        if (inscripcion.isAsistio()) {
            return false; // ya fue marcada
        }

        inscripcion.setAsistio(true);
        db.editar(inscripcion);

        return true;
    }
}