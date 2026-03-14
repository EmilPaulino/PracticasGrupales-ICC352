package main.controllers;

import io.javalin.http.Context;
import main.models.Rol;
import main.models.Usuario;
import main.services.RolService;
import main.services.UsuarioService;
import main.util.EncryptUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsuarioController {

    private static UsuarioService usuarioService = UsuarioService.getInstancia();

    public static void listar(Context ctx) {
        int pagina = 0;
        String paginaParam = ctx.queryParam("pagina");
        if (paginaParam != null) {
            try {
                pagina = Integer.parseInt(paginaParam);
            } catch (NumberFormatException e) {
                pagina = 0;
            }
        }

        int tamano = 10;
        long total = UsuarioService.getInstancia().contarTotal();
        int totalPaginas = (int) Math.ceil((double) total / tamano);

        Map<String, Object> modelo = new HashMap<>();
        modelo.put("usuarios", UsuarioService.getInstancia().findPaginado(pagina, tamano));
        modelo.put("paginaActual", pagina);
        modelo.put("totalPaginas", totalPaginas);

        ctx.render("templates/usuarios/listarUsuarios.html", modelo);
    }

    public static void crear(Context ctx){
        String nombre = ctx.formParam("nombre");
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");

        Long rolId = Long.parseLong(ctx.formParam("rol"));

        boolean activo = ctx.formParam("activo") != null;

        Rol rol = RolService.getInstancia().find(rolId);

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setPassword(password);
        usuario.setActivo(activo);
        usuario.setRol(rol);

        // FOTO
        var uploadedFile = ctx.uploadedFile("foto");
        if(uploadedFile != null && !uploadedFile.equals("")){
            try (var is = uploadedFile.content()) {
                byte[] bytes = is.readAllBytes();
                String fotoBase64 = java.util.Base64.getEncoder().encodeToString(bytes);
                usuario.setFotoBase64(fotoBase64);
            } catch(Exception e){
                e.printStackTrace();
            }
        }

        UsuarioService.getInstancia().crear(usuario);
        ctx.redirect("/usuarios");
    }

    public static void formNuevo(Context ctx) {
        Map<String, Object> model = new HashMap<>();
        model.put("roles", RolService.getInstancia().findAll());
        model.put("usuario", null);   // ← ESTA ES LA SOLUCIÓN
        ctx.render("templates/usuarios/formularioUsuarios.html", model);
    }

    public static void formEditar(Context ctx) {
        Long id = Long.parseLong(ctx.pathParam("id"));
        Usuario usuario = UsuarioService.getInstancia().find(id);
        Map<String, Object> model = new HashMap<>();
        model.put("usuario", usuario);
        model.put("roles", RolService.getInstancia().findAll());
        ctx.render("templates/usuarios/formularioUsuarios.html", model);
    }

    public static void editar(Context ctx){
        Long id = Long.parseLong(ctx.pathParam("id"));
        Usuario usuario = UsuarioService.getInstancia().find(id);
        String nombre = ctx.formParam("nombre");
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");
        Long rolId = Long.parseLong(ctx.formParam("rol"));
        boolean activo = ctx.formParam("activo") != null;

        Rol rol = RolService.getInstancia().find(rolId);

        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setActivo(activo);
        usuario.setRol(rol);

        if(password != null && !password.isBlank()){
            usuario.setPassword(password);
        }

        var uploadedFile = ctx.uploadedFile("foto");
        if(uploadedFile != null && !uploadedFile.equals("")){
            try(var is = uploadedFile.content()){
                byte[] bytes = is.readAllBytes();
                String fotoBase64 = java.util.Base64.getEncoder().encodeToString(bytes);
                usuario.setFotoBase64(fotoBase64);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        UsuarioService.getInstancia().editar(usuario);
        ctx.redirect("/usuarios");
    }

    public static void eliminar(Context ctx){
        Long id = Long.parseLong(ctx.pathParam("id"));
        if(id == 1){
            ctx.attribute("errorEliminarAdmin", true);
            ctx.attribute("usuarios", UsuarioService.getInstancia().findAll());
            ctx.render("templates/usuarios/listarUsuarios.html");
            return;
        }
        UsuarioService.getInstancia().eliminar(id);
        ctx.redirect("/usuarios");
    }

    public static void loginForm(Context ctx){
        ctx.render("public/html/login.html");
    }

    public static void procesarLogin(Context ctx){

        String email = ctx.formParam("email");
        String password = ctx.formParam("password");

        UsuarioService usuarioService = UsuarioService.getInstancia();

        Usuario usuario = usuarioService.login(email, password);

        if(usuario != null){

            if(!usuario.isActivo()){
                ctx.attribute("bloqueado", true);
                ctx.render("public/html/login.html");
                return;
            }

            ctx.sessionAttribute("usuario", usuario);
            boolean remember = ctx.formParam("remember") != null;
            if (remember) {
                String encrypted = EncryptUtil.encrypt(usuario.getEmail());
                ctx.cookie("rememberMe", encrypted, 7 * 24 * 60 * 60);
            }

            Rol rol = usuario.getRol();

            if(rol != null && ("Admin".equals(rol.getRol()) || "Organizador".equals(rol.getRol()))){
                ctx.redirect("/panel");
            }else{
                ctx.redirect("/");
            }

        }else{
            ctx.attribute("error", true);
            ctx.render("public/html/login.html");
        }

    }

    public static void bloquear(Context ctx){
        Long id = Long.parseLong(ctx.pathParam("id"));
        Usuario usuario = UsuarioService.getInstancia().find(id);
        if(usuario != null){
            usuario.setActivo(false);
            UsuarioService.getInstancia().editar(usuario);
        }
        ctx.redirect("/usuarios");
    }

    public static void desbloquear(Context ctx){
        Long id = Long.parseLong(ctx.pathParam("id"));
        Usuario usuario = UsuarioService.getInstancia().find(id);
        if(usuario != null){
            usuario.setActivo(true);
            UsuarioService.getInstancia().editar(usuario);
        }
        ctx.redirect("/usuarios");
    }

    public static void logout(Context ctx){
        ctx.req().getSession().invalidate();
        ctx.removeCookie("rememberMe");
        ctx.redirect("/login");
    }

    public static void registroForm(Context ctx){
        ctx.render("public/html/registro.html");
    }

    public static void registroEstudiante(Context ctx){
        String nombre = ctx.formParam("nombre");
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");
        Rol rol = RolService.getInstancia().find((long) 3);

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setPassword(password);
        usuario.setActivo(true);
        usuario.setRol(rol);

        // FOTO
        var uploadedFile = ctx.uploadedFile("foto");
        if(uploadedFile != null && !uploadedFile.equals("")){
            try (var is = uploadedFile.content()) {
                byte[] bytes = is.readAllBytes();
                String fotoBase64 = java.util.Base64.getEncoder().encodeToString(bytes);
                usuario.setFotoBase64(fotoBase64);
            } catch(Exception e){
                e.printStackTrace();
            }
        }

        UsuarioService.getInstancia().crear(usuario);
        ctx.sessionAttribute("usuario", usuario);
        ctx.redirect("/");
    }
}