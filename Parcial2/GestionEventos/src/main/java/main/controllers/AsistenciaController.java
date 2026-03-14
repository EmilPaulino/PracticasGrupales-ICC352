package main.controllers;

import io.javalin.http.Context;
import main.models.Asistencia;
import main.models.Inscripcion;
import main.services.AsistenciaService;
import main.services.InscripcionService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class AsistenciaController {

    public static void marcarAsistencia(Context ctx) {
        String token = ctx.formParam("token");

        Inscripcion inscripcion = InscripcionService.getInstancia()
                .findAll().stream()
                .filter(i -> i.getQrToken().equals(token))
                .findFirst()
                .orElse(null);

        if (inscripcion == null) {
            ctx.status(404);
            ctx.result("QR inválido");
            return;
        }

        if (AsistenciaService.getInstancia().yaAsistio(inscripcion.getId())) {
            ctx.status(400);
            ctx.result("La asistencia ya fue registrada");
            return;
        }

        Asistencia asistencia = new Asistencia(
                LocalDate.now(),
                LocalTime.now(),
                inscripcion
        );

        AsistenciaService.getInstancia().crear(asistencia);

        ctx.json(Map.of(
                "nombre", inscripcion.getUsuario().getNombre(),
                "evento", inscripcion.getEvento().getTitulo(),
                "hora",   LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a"))
        ));
    }

    public static void vistaEscaner(Context ctx) {
        ctx.render("templates/asistencia/asistencia.html", new HashMap<>());
    }
}