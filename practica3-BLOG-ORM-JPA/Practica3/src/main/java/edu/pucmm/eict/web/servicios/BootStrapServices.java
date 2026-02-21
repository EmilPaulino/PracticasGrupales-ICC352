package edu.pucmm.eict.web.servicios;

import edu.pucmm.eict.web.entidades.Usuario;
import org.h2.tools.Server;
import java.sql.SQLException;

public class BootStrapServices {
    private static BootStrapServices instancia;

    private BootStrapServices(){

    }

    public static BootStrapServices getInstancia(){
        if(instancia == null){
            instancia=new BootStrapServices();
        }
        return instancia;
    }

    public void startDb() {
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

    private void crearUsuarioAdmin() {
        UsuarioService usuarioService = UsuarioService.getInstancia();
        if(usuarioService.validarLogin("admin", "admin") == null){
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setNombre("Administrador");
            admin.setPassword("admin"); // puedes cambiar luego
            admin.setAdministrator(true);
            admin.setAutor(true);
            usuarioService.crearUsuario(admin);
            System.out.println("Usuario admin creado automáticamente.");
        }
    }

    public void init(){
        startDb();
        crearUsuarioAdmin();
    }
}
