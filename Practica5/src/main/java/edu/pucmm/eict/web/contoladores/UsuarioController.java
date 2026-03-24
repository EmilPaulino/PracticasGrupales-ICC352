package edu.pucmm.eict.web.contoladores;

import edu.pucmm.eict.web.entidades.Usuario;
import edu.pucmm.eict.web.servicios.UsuarioService;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;

import java.util.List;

public class UsuarioController {

    private final UsuarioService usuarioService = UsuarioService.getInstancia();

    /*
     * Lista todos los usuarios
     */
    public void listar(Context ctx){
        List<Usuario> usuarios = usuarioService.findAll(); // ahora trae de H2
        ctx.attribute("usuarios", usuarios);
        ctx.render("/templates/usuarios/listarUsuarios.html");
    }

    /*
     * Muestra el formulario para crear usuario
     */
    public void formularioCrear(Context ctx){
        ctx.render("/templates/usuarios/formularioUsuario.html");
    }

    /*
     * Procesa la creación de usuario
     */
    public void crear(Context ctx){
        String username = ctx.formParam("username");
        String nombre = ctx.formParam("nombre");
        String password = ctx.formParam("password");
        boolean administrator = ctx.formParam("administrator") != null;
        boolean autor = ctx.formParam("autor") != null;

        if(usuarioService.findByUsername(username) != null){
            ctx.attribute("error", "El nombre de usuario ya existe");
            ctx.render("/templates/usuarios/formularioUsuario.html");
            return;
        }

        String fotoBase64 = null;

        var uploadedFile = ctx.uploadedFile("foto");

        if(uploadedFile != null){
            try (var is = uploadedFile.content()) {
                byte[] bytes = is.readAllBytes();
                if(bytes.length > 0){
                    fotoBase64 = java.util.Base64.getEncoder().encodeToString(bytes);
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        }

        Usuario usuario = new Usuario(username, nombre, password, administrator, autor);
        usuario.setFotoBase64(fotoBase64);

        usuarioService.crearUsuario(usuario);

        ctx.redirect("/usuarios");
    }

    /*
     * Muestra el formulario para editar usuario
     */
    public void formularioEditar(Context ctx){
        long id = Long.parseLong(ctx.pathParam("id"));
        Usuario usuario = usuarioService.find(id);

        if(usuario == null){
            throw new NotFoundResponse("Usuario no encontrado");
        }

        ctx.attribute("usuario", usuario);
        ctx.render("/templates/usuarios/formularioUsuario.html");
    }

    /*
     * Procesa la edición de usuario
     */
    public void editar(Context ctx){
        long id = Long.parseLong(ctx.pathParam("id"));
        Usuario usuario = usuarioService.find(id);

        if(usuario == null){
            throw new NotFoundResponse("Usuario no encontrado");
        }

        String username = ctx.formParam("username");
        String nombre = ctx.formParam("nombre");
        String password = ctx.formParam("password");
        boolean administrator = ctx.formParam("administrator") != null;
        boolean autor = ctx.formParam("autor") != null;

        Usuario existente = usuarioService.findByUsername(username);

        if(existente != null && existente.getId() != id){
            ctx.attribute("error", "El nombre de usuario ya existe");
            ctx.attribute("usuario", usuario);
            ctx.render("/templates/usuarios/formularioUsuario.html");
            return;
        }

        var uploadedFile = ctx.uploadedFile("foto");

        if(uploadedFile != null){
            try (var is = uploadedFile.content()) {
                byte[] bytes = is.readAllBytes();
                if(bytes.length > 0){
                    String fotoBase64 = java.util.Base64.getEncoder().encodeToString(bytes);
                    usuario.setFotoBase64(fotoBase64);
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        }

        usuario.setUsername(username);
        usuario.setNombre(nombre);

        if(password != null && !password.isEmpty()){
            usuario.setPassword(password);
        }

        usuario.setAdministrator(administrator);
        usuario.setAutor(autor);

        usuarioService.actualizarUsuario(usuario);

        ctx.redirect("/usuarios");
    }

    /*
     * Elimina un usuario
     */
    public void eliminar(Context ctx){
        long id = Long.parseLong(ctx.pathParam("id"));
        boolean eliminado = usuarioService.eliminarUsuario(id);

        if(!eliminado){
            throw new NotFoundResponse("Usuario no encontrado");
        }

        ctx.redirect("/usuarios");
    }
}