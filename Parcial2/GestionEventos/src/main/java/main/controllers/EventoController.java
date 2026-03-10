package main.controllers;

import io.javalin.http.Context;
import main.models.Evento;
import main.models.Usuario;
import main.services.EventoService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class EventoController {

    //LISTAR EVENTOS
    public static void listar(Context ctx){

        Map<String, Object> modelo = new HashMap<>();

        modelo.put("eventos", EventoService.getInstancia().findAll());

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

}