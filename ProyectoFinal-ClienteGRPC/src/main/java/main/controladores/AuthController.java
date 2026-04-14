package main.controladores;

import io.javalin.http.Context;

import formulariogrpc.Formulario;
import main.grpc.GrpcClient;
import main.util.EncryptUtil;

public class AuthController {

    public static void mostrarLogin(Context ctx) {

        String username = ctx.sessionAttribute("username");

        if (username != null) {

            ctx.redirect("/formularios");
            return;

        }

        ctx.render("/templates/login.html");

    }

    public static void login(Context ctx) {

        String username = ctx.formParam("username");

        String password = ctx.formParam("password");

        if (username == null || password == null) {

            ctx.attribute("error", "Ingrese sus credenciales");

            ctx.render("/templates/login/login.html");
            return;

        }

        Formulario.LoginResponse response = GrpcClient.getInstancia().login(username, password);

        if (!response.getOk()) {

            ctx.attribute("error", "Credenciales incorrectas.");

            ctx.render("/templates/login.html");
            return;

        }

        ctx.sessionAttribute("username", response.getUsername());

        boolean remember = ctx.formParam("remember") != null;

        if (remember) {

            String encrypted = EncryptUtil.encrypt(response.getUsername());

            ctx.cookie("rememberMe", encrypted, 7 * 24 * 60 * 60);
        }

        ctx.redirect("/formularios");

    }

    public static void logout(Context ctx) {

        ctx.req().getSession().invalidate();

        ctx.removeCookie("rememberMe");

        ctx.redirect("/login");

    }

}