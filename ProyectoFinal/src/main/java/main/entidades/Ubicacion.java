package main.entidades;

import dev.morphia.annotations.Embedded;

@Embedded
public class Ubicacion {

    private double latitud;
    private double longitud;

    public Ubicacion() {
    }

    public Ubicacion(double latitud, double longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double l) {
        this.latitud = l;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double l) {
        this.longitud = l;
    }
}