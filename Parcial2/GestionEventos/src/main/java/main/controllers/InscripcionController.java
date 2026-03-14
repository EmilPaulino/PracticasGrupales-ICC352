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
import java.util.*;

public class InscripcionController {

    public static void inscribirse(Context ctx) {
        Usuario usuario = ctx.sessionAttribute("usuario");

        if (usuario == null) {
            ctx.status(401);
            ctx.result("Debe iniciar sesión");
            return;
        }

        Long eventoId = Long.parseLong(ctx.pathParam("id"));
        Evento evento = EventoService.getInstancia().find(eventoId);

        if (evento == null || evento.isCancelado() || !evento.isPublicado()) {
            ctx.status(404);
            ctx.result("Evento no encontrado");
            return;
        }

        if (InscripcionService.getInstancia().existeInscripcion(usuario.getId(), eventoId)) {
            ctx.status(400);
            ctx.result("Ya estás inscrito en este evento");
            return;
        }

        if (InscripcionService.getInstancia().contarPorEvento(eventoId) >= evento.getCupoMaximo()) {
            ctx.status(400);
            ctx.result("El evento está lleno");
            return;
        }

        Inscripcion inscripcion = new Inscripcion(
                LocalDate.now(),
                LocalTime.now(),
                UUID.randomUUID().toString(),
                usuario,
                evento
        );

        InscripcionService.getInstancia().crear(inscripcion);

        long inscritos   = InscripcionService.getInstancia().contarPorEvento(eventoId);
        long disponibles = evento.getCupoMaximo() - inscritos;
        long porcentaje  = (inscritos * 100) / evento.getCupoMaximo();

        ctx.status(200);
        ctx.json(Map.of(
                "qrToken",     inscripcion.getQrToken(),
                "inscritos",   inscritos,
                "disponibles", disponibles,
                "porcentaje",  porcentaje
        ));
    }


    public static void cancelarInscripcion(Context ctx) {
        Usuario usuario = ctx.sessionAttribute("usuario");

        if (usuario == null) {
            ctx.redirect("/login");
            return;
        }

        Long inscripcionId = Long.parseLong(ctx.pathParam("id"));
        Inscripcion inscripcion = InscripcionService.getInstancia().find(inscripcionId);

        if (inscripcion == null) {
            ctx.sessionAttribute("error", "Inscripción no encontrada.");
            ctx.redirect("/mis-inscripciones");
            return;
        }

        if (!inscripcion.getUsuario().getId().equals(usuario.getId())) {
            ctx.sessionAttribute("error", "No estás autorizado para cancelar esta inscripción.");
            ctx.redirect("/mis-inscripciones");
            return;
        }

        if (!inscripcion.getEvento().getFecha().isAfter(LocalDate.now())) {
            ctx.sessionAttribute("error", "Solo puedes cancelar hasta el día anterior al evento.");
            ctx.redirect("/mis-inscripciones");
            return;
        }

        InscripcionService.getInstancia().eliminar(inscripcionId);
        ctx.sessionAttribute("exito", "Inscripción cancelada correctamente.");
        ctx.redirect("/mis-inscripciones");
    }

    public static void misInscripciones(Context ctx) {

        Usuario usuario = ctx.sessionAttribute("usuario");

        if (usuario == null) {
            ctx.redirect("/login");
            return;
        }

        String error = ctx.sessionAttribute("error");
        String exito = ctx.sessionAttribute("exito");
        ctx.sessionAttribute("error", null);
        ctx.sessionAttribute("exito", null);

        List<Inscripcion> inscripciones = InscripcionService.getInstancia()
                .findPorUsuario(usuario.getId());

        List<Asistencia> asistencias = AsistenciaService.getInstancia().findAll();

        // Lista nueva donde guardaremos solo las inscripciones sin asistencia
        List<Inscripcion> resultado = new ArrayList<>();

        for (Inscripcion inscripcion : inscripciones) {

            boolean asistio = false;

            for (Asistencia asistencia : asistencias) {
                if (asistencia.getInscripcion().getId().equals(inscripcion.getId())) {
                    asistio = true;
                    break;
                }
            }

            if (!asistio) {
                resultado.add(inscripcion);
            }
        }

        Map<String, Object> modelo = new HashMap<>();
        modelo.put("inscripciones", resultado);
        modelo.put("error", error);
        modelo.put("exito", exito);

        ctx.render("templates/inscripciones/misInscripciones.html", modelo);
    }
}