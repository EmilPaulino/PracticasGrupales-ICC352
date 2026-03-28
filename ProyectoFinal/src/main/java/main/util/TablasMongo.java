package main.util;

public enum TablasMongo {

    USUARIOS("usuarios"),
    FORMULARIOS("formularios");

    private String valor;

    TablasMongo(String valor){
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

}