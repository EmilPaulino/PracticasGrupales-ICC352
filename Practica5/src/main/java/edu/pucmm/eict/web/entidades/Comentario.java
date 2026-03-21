package edu.pucmm.eict.web.entidades;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
public class Comentario implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000) //No puede ser null y máx de caracteres 1000
    private String comentario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="idAutor", nullable = false) //Nombre de la columna de la clave foránea idAutor
    private Usuario autor;

    @ManyToOne
    @JoinColumn(name = "idArticulo", nullable = false)
    private Articulo articulo;

    public Comentario(){

    }

    public Comentario(String comentario, Usuario autor, Articulo articulo) {
        this.comentario = comentario;
        this.autor = autor;
        this.articulo = articulo;
    }

    public Long getId() {
        return id;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Usuario getAutor() {
        return autor;
    }

    public void setAutor(Usuario autor) {
        this.autor = autor;
    }

    public Articulo getArticulo(){
        return articulo;
    }

    public void setArticulo(Articulo articulo){
        this.articulo = articulo;
    }
}
