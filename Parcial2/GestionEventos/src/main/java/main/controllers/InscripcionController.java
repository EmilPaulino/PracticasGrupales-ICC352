package main.controllers;

import io.javalin.http.Context;
import main.models.Evento;
import main.models.Inscripcion;
import main.models.Usuario;
import main.services.EventoService;
import main.services.InscripcionService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class InscripcionController {

    public static void inscribirse(Context ctx) {

        Usuario usuario = ctx.sessionAttribute("usuario");

        if (usuario == null) {
            ctx.status(401);
            ctx.result("Debe iniciar sesión");
            return;
        }

        Long eventoId = Long.parseLong(ctx.formParam("eventoId"));

        Evento evento = EventoService.getInstancia().find(eventoId);

        if (evento == null) {
            ctx.status(404);
            ctx.result("Evento no encontrado");
            return;
        }

        // validar duplicados
        boolean existe = InscripcionService.getInstancia()
                .existeInscripcion(usuario.getId(), eventoId);

        if (existe) {
            ctx.status(400);
            ctx.result("Ya estás inscrito en este evento");
            return;
        }

        // validar cupo
        long inscritos = InscripcionService.getInstancia()
                .contarPorEvento(eventoId);

        if (inscritos >= evento.getCupoMaximo()) {
            ctx.status(400);
            ctx.result("El evento está lleno");
            return;
        }

        // generar token QR
        String qrToken = UUID.randomUUID().toString();

        Inscripcion inscripcion = new Inscripcion(
                LocalDate.now(),
                LocalTime.now(),
                qrToken,
                usuario,
                evento
        );

        InscripcionService.getInstancia().crear(inscripcion);

        ctx.result("Inscripción realizada correctamente");
    }


    public static void cancelarInscripcion(Context ctx) {

        Usuario usuario = ctx.sessionAttribute("usuario");

        if (usuario == null) {
            ctx.status(401);
            ctx.result("Debe iniciar sesión");
            return;
        }

        Long inscripcionId = Long.parseLong(ctx.pathParam("id"));

        Inscripcion inscripcion = InscripcionService
                .getInstancia()
                .find(inscripcionId);

        if (inscripcion == null) {
            ctx.status(404);
            ctx.result("Inscripción no encontrada");
            return;
        }

        if (!inscripcion.getUsuario().getId().equals(usuario.getId())) {
            ctx.status(403);
            ctx.result("No autorizado");
            return;
        }

        InscripcionService.getInstancia().eliminar(inscripcionId);

        ctx.result("Inscripción cancelada");
    }


    public static void marcarAsistencia(Context ctx) {

        String token = ctx.formParam("token");

        Inscripcion inscripcion = InscripcionService
                .getInstancia()
                .findAll()
                .stream()
                .filter(i -> i.getQrToken().equals(token))
                .findFirst()
                .orElse(null);

        if (inscripcion == null) {
            ctx.status(404);
            ctx.result("QR inválido");
            return;
        }

        if (inscripcion.isAsistio()) {
            ctx.result("La asistencia ya fue registrada");
            return;
        }

        inscripcion.setAsistio(true);

        InscripcionService.getInstancia().crear(inscripcion);

        ctx.result("Asistencia registrada");
    }
}