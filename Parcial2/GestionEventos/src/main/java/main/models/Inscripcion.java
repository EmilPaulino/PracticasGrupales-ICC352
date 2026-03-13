package main.models;

import jakarta.persistence.*;
import main.services.GestionDb;

import java.time.*;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id","evento_id"})) //Para que no se repitan inscripciones
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha;
    private LocalTime hora;

    private String qrToken;

    private boolean asistio; // Para confirmar asistencia

    @ManyToOne
    private Usuario usuario;

    @ManyToOne
    private Evento evento;

    public Inscripcion() {

    }

    public Inscripcion(LocalDate fecha, LocalTime hora, String qrToken, Usuario usuario, Evento evento) {
        this.fecha = fecha;
        this.hora = hora;
        this.qrToken = qrToken;
        this.usuario = usuario;
        this.evento = evento;
        this.asistio = false; // nadie ha asistido cuando se inscribe
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public String getQrToken() {
        return qrToken;
    }

    public void setQrToken(String qrToken) {
        this.qrToken = qrToken;
    }

    public boolean isAsistio() {
        return asistio;
    }

    public void setAsistio(boolean asistio) {
        this.asistio = asistio;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

}