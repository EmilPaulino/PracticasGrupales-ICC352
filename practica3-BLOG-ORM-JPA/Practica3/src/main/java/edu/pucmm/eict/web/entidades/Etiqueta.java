package edu.pucmm.eict.web.entidades;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
public class Etiqueta implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String etiqueta;

    @ManyToMany(mappedBy = "listaEtiquetas")
    private Set<Articulo> listaArticulos;

    public Etiqueta(){

    }

    public Etiqueta(String etiqueta){
        this.etiqueta = etiqueta;
    }

    public Long getId() {
        return id;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public Set<Articulo> getListaArticulos() {
        return listaArticulos;
    }

    public void setListaArticulos(Set<Articulo> listaArticulos) {
        this.listaArticulos = listaArticulos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Etiqueta)) return false;
        Etiqueta other = (Etiqueta) o;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}

