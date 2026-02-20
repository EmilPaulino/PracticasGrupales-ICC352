package edu.pucmm.eict.web.servicios;

import edu.pucmm.eict.web.entidades.Usuario;

import java.util.ArrayList;

public class UsuarioService {
    private static UsuarioService instancia;
    private ArrayList<Usuario> losUsuarios = new ArrayList<Usuario>();
    private long contadorID = 1;

    private UsuarioService(){
        losUsuarios.add(new Usuario(
                contadorID,
                "admin",
                "Administrador",
                "admin",
                true,
                true
        ));
        contadorID++;
    }

    public static UsuarioService getInstancia(){
        if(instancia == null){
            instancia = new UsuarioService();
        }
        return instancia;
    }

    /*
    * Esta función valída si existe un usuario con el nombre de usuario y contraseña
    * enviado por parámetros, si existe lo true, sino retorna false.
    * */
    public Usuario validarLogin(String username, String password){
        for(Usuario u : losUsuarios){
            if(u.getUsername().equals(username) && u.getPassword().equals(password)){
                return u;
            }
        }
        return null;
    }

    /*
    * Esta función registra un usuario nuevo.
    * */
    public boolean registrarUsuario(String username, String name, String password, boolean administrator, boolean autor){
        if(!validarUsername(contadorID, username)){
            return false;
        }
        losUsuarios.add(new Usuario(
                contadorID,
                username,
                name,
                password,
                administrator,
                autor
        ));
        contadorID++;
        return true;
    }

    /*
    * Esta función busca un usuario por ID.
    * */
    public Usuario buscarUsuarioPorID(long id){
        for(Usuario u : losUsuarios){
            if(u.getId() == id){
                return u;
            }
        }
        return null;
    }

    /*
    * Esta función actualiza un usuario buscado por su id.
    * */
    public boolean actualizarUsuario(long id, String username, String name,
                                     String password, boolean administrator, boolean autor){

        Usuario user = buscarUsuarioPorID(id);
        if(user == null){
            return false;
        }
        if(!validarUsername(id, username)){
            return false;
        }
        user.setUsername(username);
        user.setNombre(name);
        if(password != null && !password.isEmpty()){
            user.setPassword(password);
        }
        user.setAdministrator(administrator);
        user.setAutor(autor);

        return true;
    }

    /*
    * Esta función elimina un usuario de la lista.
    * */
    public boolean eliminarUsuario(long id){
        Usuario user = buscarUsuarioPorID(id);
        if(user == null){
            return false;
        }
        losUsuarios.remove(user);
        return true;
    }

    /*
    * Esta función devuelve la lista de usuarios.
    * */
    public ArrayList<Usuario> listarUsuarios(){
        return losUsuarios;
    }

    /*
    * Esta función valída que un nombre de usuario (username) no
    * se repita.
    * */
    public boolean validarUsername(long id, String username){
        for (Usuario u : losUsuarios) {
            if (u.getUsername().equalsIgnoreCase(username) && u.getId() != id) {
                return false;
            }
        }
        return true;
    }


}