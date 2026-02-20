package edu.pucmm.eict.web.entidades;

public class Etiqueta {
    private long id;
    private String etiqueta;

    public Etiqueta(long id, String etiqueta){
        this.id = id;
        this.etiqueta = etiqueta;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    //Sobrescribe equals() y hashCode() en Etiqueta usando el id para que contains() funcione correctamente
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Etiqueta)) return false;
        Etiqueta e = (Etiqueta) o;
        return id == e.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}

