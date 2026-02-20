package edu.pucmm.eict.web.entidades;

public class Comentario {
    private long id;
    private String comentario;
    private Usuario autor;

    public Comentario(long id, String comentario, Usuario autor) {
        this.id = id;
        this.comentario = comentario;
        this.autor = autor;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
}
