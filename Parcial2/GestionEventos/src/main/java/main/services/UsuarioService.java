package main.services;

import main.models.Usuario;

public class UsuarioService extends GestionDb<Usuario>{

    private static UsuarioService instancia;

    public UsuarioService(){
        super(Usuario.class);
    }

    public static UsuarioService getInstancia(){

        if(instancia == null){
            instancia = new UsuarioService();
        }

        return instancia;
    }

}