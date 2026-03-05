package main.services;

import main.models.Rol;
import main.models.Usuario;
import org.h2.tools.Server;

import java.sql.SQLException;

public class BootStrapServices {

    private static BootStrapServices instancia;

    private BootStrapServices(){}

    public static BootStrapServices getInstancia(){

        if(instancia == null){
            instancia = new BootStrapServices();
        }

        return instancia;
    }

    public void startDb(){
        try {
            //Modo servidor H2.
            Server.createTcpServer("-tcpPort",
                    "9092",
                    "-tcpAllowOthers",
                    "-tcpDaemon",
                    "-ifNotExists").start();
            //Abriendo el cliente web. El valor 0 representa puerto aleatorio.
            String status = Server.createWebServer("-trace", "-webPort", "0").start().getStatus();
            //
            System.out.println("Status Web: "+status);
        }catch (SQLException ex){
            System.out.println("Problema con la base de datos: "+ex.getMessage());
        }
    }

    private void crearRoles(){
        GestionDb<Rol> rolDb = new GestionDb<>(Rol.class);
        boolean existeAdmin = false;
        boolean existeOrganizador = false;
        boolean existeParticipante = false;

        for(Rol r : rolDb.findAll()){
            if(r.getRol().equals("ADMIN")){
                existeAdmin = true;
            }
            if(r.getRol().equals("ORGANIZADOR")){
                existeOrganizador = true;
            }
            if(r.getRol().equals("PARTICIPANTE")){
                existeParticipante = true;
            }
        }

        if(!existeAdmin){
            rolDb.crear(new Rol("Admin"));
        }

        if(!existeOrganizador){
            rolDb.crear(new Rol("Organizador"));
        }

        if(!existeParticipante){
            rolDb.crear(new Rol("Participante"));
        }
    }

    private void crearUsuarioAdmin(){
        UsuarioService usuarioService = UsuarioService.getInstancia();
        GestionDb<Rol> rolDb = new GestionDb<>(Rol.class);

        boolean existeAdmin = false;

        for(Usuario u : usuarioService.findAll()){
            if(u.getEmail().equals("admin@admin.com")){
                existeAdmin = true;
                break;
            }
        }

        if(!existeAdmin){

            Rol rolAdmin = null;

            for(Rol r : rolDb.findAll()){
                if(r.getRol().equals("Admin")){
                    rolAdmin = r;
                    break;
                }
            }

            Usuario admin = new Usuario(
                    "Administrador",
                    "admin@admin.com",
                    "admin",
                    true,
                    rolAdmin
            );
            usuarioService.crear(admin);
        }
    }

    public void init(){
        startDb();
        crearRoles();
        crearUsuarioAdmin();
    }

}