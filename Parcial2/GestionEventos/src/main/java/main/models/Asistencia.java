package main.models;

import jakarta.persistence.*;

import java.time.*;

@Entity
public class Asistencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate fecha;
    private LocalTime hora;
    @OneToOne
    @JoinColumn(unique = true)
    private Inscripcion inscripcion;

    public Asistencia() {

    }

    public Asistencia(LocalDate fecha, LocalTime hora, Inscripcion inscripcion) {
        this.fecha = fecha;
        this.hora = hora;
        this.inscripcion = inscripcion;

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

    public Inscripcion getInscripcion() {
        return inscripcion;
    }

    public void setInscripcion(Inscripcion inscripcion) {
        this.inscripcion = inscripcion;
    }
}
