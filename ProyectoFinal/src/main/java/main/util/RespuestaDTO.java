package main.util;

public class RespuestaDTO {

    private boolean exito;
    private String mensaje;
    private Object datos;

    public RespuestaDTO(boolean exito, String mensaje, Object datos) {
        this.exito = exito;
        this.mensaje = mensaje;
        this.datos = datos;
    }

    public static RespuestaDTO ok(Object datos) {
        return new RespuestaDTO(true, "OK", datos);
    }

    public static RespuestaDTO error(String mensaje) {
        return new RespuestaDTO(false, mensaje, null);
    }

    public boolean isExito() {
        return exito;
    }

    public String getMensaje() {
        return mensaje;
    }

    public Object getDatos() {
        return datos;
    }
}