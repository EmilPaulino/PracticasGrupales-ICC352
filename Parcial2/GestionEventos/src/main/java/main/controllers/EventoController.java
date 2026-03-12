package main.controllers;

import io.javalin.http.Context;
import main.models.Asistencia;
import main.models.Evento;
import main.models.Inscripcion;
import main.models.Usuario;
import main.services.AsistenciaService;
import main.services.EventoService;
import main.services.InscripcionService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EventoController {

    //LISTAR EVENTOS
    public static void listar(Context ctx) {
        int pagina = 0;
        String paginaParam = ctx.queryParam("pagina");
        if (paginaParam != null) {
            try {
                pagina = Integer.parseInt(paginaParam);
            } catch (NumberFormatException e) {
                pagina = 0;
            }
        }

        int tamano = 10;
        long total = EventoService.getInstancia().contarTotal();
        int totalPaginas = (int) Math.ceil((double) total / tamano);

        List<Evento> eventos = EventoService.getInstancia().findPaginado(pagina, tamano);

        Map<Long, Long> inscritos = new HashMap<>();
        for (Evento e : eventos) {
            inscritos.put(e.getId(), InscripcionService.getInstancia().contarPorEvento(e.getId()));
        }

        Map<String, Object> modelo = new HashMap<>();
        modelo.put("eventos", eventos);
        modelo.put("inscritos", inscritos);
        modelo.put("paginaActual", pagina);
        modelo.put("totalPaginas", totalPaginas);

        ctx.render("templates/eventos/listarEventos.html", modelo);
    }


    //FORMULARIO NUEVO EVENTO
    public static void formNuevo(Context ctx){
        ctx.render("templates/eventos/formularioEventos.html");
    }


    //CREAR EVENTO
    public static void crear(Context ctx){

        Usuario usuario = ctx.sessionAttribute("usuario");

        String titulo = ctx.formParam("titulo");
        String descripcion = ctx.formParam("descripcion");
        LocalDate fecha = LocalDate.parse(ctx.formParam("fecha"));
        LocalTime hora = LocalTime.parse(ctx.formParam("hora"));
        String lugar = ctx.formParam("lugar");
        int cupo = Integer.parseInt(ctx.formParam("cupoMaximo"));

        Evento evento = new Evento(
                titulo,
                descripcion,
                fecha,
                hora,
                lugar,
                cupo,
                false,
                false,
                usuario
        );

        EventoService.getInstancia().crear(evento);

        ctx.redirect("/eventos");
    }


    //FORMULARIO EDITAR EVENTO
    public static void formEditar(Context ctx){

        Long id = Long.parseLong(ctx.pathParam("id"));

        Evento evento = EventoService.getInstancia().find(id);

        Map<String, Object> modelo = new HashMap<>();
        modelo.put("evento", evento);

        ctx.render("templates/eventos/formularioEventos.html", modelo);
    }


    //EDITAR EVENTO
    public static void editar(Context ctx){

        Long id = Long.parseLong(ctx.pathParam("id"));

        Evento evento = EventoService.getInstancia().find(id);

        evento.setTitulo(ctx.formParam("titulo"));
        evento.setDescripcion(ctx.formParam("descripcion"));
        evento.setFecha(LocalDate.parse(ctx.formParam("fecha")));
        evento.setHora(LocalTime.parse(ctx.formParam("hora")));
        evento.setLugar(ctx.formParam("lugar"));
        evento.setCupoMaximo(Integer.parseInt(ctx.formParam("cupoMaximo")));

        EventoService.getInstancia().editar(evento);

        ctx.redirect("/eventos");
    }


    //CANCELAR EVENTO
    public static void cancelar(Context ctx){

        Long id = Long.parseLong(ctx.pathParam("id"));

        EventoService.getInstancia().cancelar(id);

        ctx.redirect("/eventos");
    }


    //PUBLICAR EVENTO
    public static void publicar(Context ctx){
        Long id = Long.parseLong(ctx.pathParam("id"));
        EventoService.getInstancia().publicar(id);
        ctx.redirect("/eventos");
    }


    //DESPUBLICAR EVENTO
    public static void desPublicar(Context ctx){
        Long id = Long.parseLong(ctx.pathParam("id"));
        EventoService.getInstancia().desPublicar(id);
        ctx.redirect("/eventos");
    }

    //Pagina principal (index)
    public static void listarPublicados(Context ctx) {
        List<Evento> eventos = EventoService.getInstancia().findPublicados();
        Map<Long, Long> inscritos = new HashMap<>();
        for (Evento e : eventos) {
            inscritos.put(e.getId(), InscripcionService.getInstancia().contarPorEvento(e.getId()));
        }
        Map<String, Object> modelo = new HashMap<>();
        modelo.put("eventos", eventos);
        modelo.put("inscritos", inscritos);
        ctx.render("templates/index.html", modelo);
    }

    public static void eliminar(Context ctx){
        Long id = Long.parseLong(ctx.pathParam("id"));
        EventoService.getInstancia().eliminar(id);
        ctx.redirect("/eventos");
    }

    public static void visualizar(Context ctx){
        Long id = Long.parseLong(ctx.pathParam("id"));
        Evento evento = EventoService.getInstancia().find(id);
        long inscritos = InscripcionService.getInstancia().contarPorEvento(id);
        long asistentes = AsistenciaService.getInstancia().contarPorEvento(id);
        double porcentaje = inscritos > 0 ? (asistentes * 100.0) / inscritos : 0;

        Map<String, Object> modelo = new HashMap<>();
        modelo.put("evento", evento);
        modelo.put("inscritos", inscritos);
        modelo.put("asistentes", asistentes);
        modelo.put("porcentaje", String.format("%.1f", porcentaje));

        ctx.render("templates/eventos/visualizarEventoAdm.html", modelo);
    }

    // Inscripciones por día
    public static void inscripcionesPorDia(Context ctx) {
        Long id = Long.parseLong(ctx.pathParam("id"));
        List<Inscripcion> inscripciones = InscripcionService.getInstancia().findPorEvento(id);

        Map<String, Long> porDia = new LinkedHashMap<>();
        for (Inscripcion i : inscripciones) {
            String dia = i.getFecha().toString();
            porDia.put(dia, porDia.getOrDefault(dia, 0L) + 1);
        }

        ctx.json(porDia);
    }

    // Asistencia por hora
    public static void asistenciaPorHora(Context ctx) {
        Long id = Long.parseLong(ctx.pathParam("id"));
        List<Asistencia> asistencias = AsistenciaService.getInstancia().findPorEvento(id);

        Map<String, Long> porHora = new LinkedHashMap<>();
        for (Asistencia a : asistencias) {
            String hora = a.getHora().getHour() + ":00";
            porHora.put(hora, porHora.getOrDefault(hora, 0L) + 1);
        }

        ctx.json(porHora);
    }

    public static void visualizarPublico(Context ctx) {
        Long id = Long.parseLong(ctx.pathParam("id"));
        Evento evento = EventoService.getInstancia().find(id);

        if (evento == null || !evento.isPublicado()) {
            ctx.redirect("/eventos/publicos");
            return;
        }

        long inscritos = InscripcionService.getInstancia().contarPorEvento(id);

        boolean yaInscrito = false;
        Usuario usuario = (Usuario) ctx.sessionAttribute("usuario");

        if (usuario != null) {
            yaInscrito = InscripcionService.getInstancia().existeInscripcion(id, usuario.getId());
        }

        Map<String, Object> modelo = new HashMap<>();
        modelo.put("evento", evento);
        modelo.put("inscritos", inscritos);
        modelo.put("yaInscrito", yaInscrito);

        ctx.render("templates/eventos/visualizarEvento.html", modelo);
    }

}