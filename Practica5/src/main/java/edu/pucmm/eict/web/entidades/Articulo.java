package edu.pucmm.eict.web.entidades;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Articulo implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true)
    private String titulo;

    @Lob //Para textos largos
    @Column(nullable = false)
    private String cuerpo;

    @ManyToOne
    @JoinColumn(name = "idUsuario", nullable = false)
    private Usuario autor;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;

    @OneToMany(mappedBy = "articulo", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comentario> listaComentarios= new HashSet<>();

    @ManyToMany
    @JoinTable(name = "articulo_etiqueta",joinColumns = @JoinColumn(name = "idArticulo"), inverseJoinColumns = @JoinColumn(name = "idEtiqueta"))
    private Set<Etiqueta> listaEtiquetas = new HashSet<>();

    public Articulo(){

    }

    public Articulo(String titulo, String cuerpo, Usuario autor) {
        this.titulo = titulo;
        this.cuerpo = cuerpo;
        this.autor = autor;
        this.fecha = new Date();
    }

    public Long getId() {

        return id;
    }

    public String getTitulo() {

        return titulo;
    }

    public void setTitulo(String titulo) {

        this.titulo = titulo;
    }

    public String getCuerpo() {

        return cuerpo;
    }

    public void setCuerpo(String cuerpo) {
        this.cuerpo = cuerpo;
    }

    public Usuario getAutor() {
        return autor;
    }

    public void setAutor(Usuario autor) {
        this.autor = autor;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Set<Comentario> getListaComentarios() {
        return listaComentarios;
    }

    public void setListaComentarios(Set<Comentario> listaComentarios) {
        this.listaComentarios = listaComentarios;
    }

    public Set<Etiqueta> getListaEtiquetas() {
        return listaEtiquetas;
    }

    public void setListaEtiquetas(Set<Etiqueta> listaEtiquetas) {
        this.listaEtiquetas = listaEtiquetas;
    }
}
