package main.services;

import main.models.Evento;
import java.util.List;

public class EventoService {

    private static EventoService instancia;
    private GestionDb<Evento> db = new GestionDb<>(Evento.class);

    private EventoService(){}

    public static EventoService getInstancia(){
        if(instancia == null){
            instancia = new EventoService();
        }
        return instancia;
    }

    public List<Evento> findAll(){
        return db.findAll();
    }

    public Evento find(Long id){
        return db.find(id);
    }

    public Evento crear(Evento evento){
        return db.crear(evento);
    }

    public Evento editar(Evento evento){
        return db.editar(evento);
    }

    public boolean eliminar(Long id){
        return db.eliminar(id);
    }

    public List<Evento> findPaginado(int pagina, int tamano) {
        return db.findPaginado(pagina, tamano);
    }

    public long contarTotal() {
        return db.contarTotal();
    }

    //Lista de eventos que están publicados
    public List<Evento> findPublicados(){
        return db.findAll().stream()
                .filter(e -> e.isPublicado() && !e.isCancelado())
                .toList();
    }

    //Cancelar evento
    public Evento cancelar(Long id){
        Evento evento = find(id);

        if(evento != null){
            evento.setCancelado(true);
            return editar(evento);
        }

        return null;
    }

    //Publicar evento
    public Evento publicar(Long id){
        Evento evento = find(id);
        if(evento != null){
            evento.setPublicado(true);
            return editar(evento);
        }
        return null;
    }

    //Despublicar evento
    public Evento desPublicar(Long id){
        Evento evento = find(id);
        if(evento != null){
            evento.setPublicado(false);
            return editar(evento);
        }
        return null;
    }
}