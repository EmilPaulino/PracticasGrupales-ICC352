package main.controladores;

import io.javalin.http.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import main.entidades.Formulario;
import main.entidades.Usuario;
import main.entidades.UsuarioEmbebido;
import main.servicios.FormularioServices;
import main.servicios.UsuarioService;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class RestController {

    public static final String LLAVE_SECRETA = "mi_clave_super_secreta_para_jwt_2026_123456";

    public static void loginApi(Context ctx) {

        String username = ctx.formParam("username");
        String password = ctx.formParam("password");
        Usuario usuario = UsuarioService.getInstancia().findByUsername(username);

        if (usuario == null) {
            ctx.status(HttpStatus.UNAUTHORIZED).json(Map.of(
                    "error", "Credenciales inválidas",
                    "mensaje", "Usuario no encontrado"
            ));
            return;
        }

        if (!usuario.getPassword().equals(password)) {
            ctx.status(HttpStatus.UNAUTHORIZED).json(Map.of(
                    "error", "Credenciales inválidas",
                    "mensaje", "Password incorrecto"
            ));
            return;
        }

        ctx.json(generacionJsonWebToken(usuario));
    }

    private static Map<String, Object> generacionJsonWebToken(Usuario usuario) {

        SecretKey secretKey = Keys.hmacShaKeyFor(LLAVE_SECRETA.getBytes());

        LocalDateTime localDateTime = LocalDateTime.now().plusMinutes(60);

        Date fechaExpiracion = Date.from(localDateTime.toInstant(ZoneOffset.ofHours(-4)));

        String jwt = Jwts.builder()
                .issuer("PUCMM-ECT")
                .subject("Login API")
                .expiration(fechaExpiracion)
                .claim("usuario", usuario.getUsername())
                .signWith(secretKey)
                .compact();

        return Map.of("token", jwt, "expiresIn", fechaExpiracion.getTime());
    }

    public static void filtroJwt(Context ctx) {

        System.out.println("Validando JWT en la petición...");

        if (ctx.path().equals("/api/login")) {
            return;
        }

        if (ctx.method() == HandlerType.OPTIONS) {
            return;
        }

        String headerAutenticacion = ctx.header("Authorization");
        String prefijo = "Bearer";

        if (headerAutenticacion == null || !headerAutenticacion.startsWith(prefijo)) {
            throw new UnauthorizedResponse("Debe autenticarse para acceder al servicio.");
        }

        String tramaJwt = headerAutenticacion.replace(prefijo, "").trim();
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(LLAVE_SECRETA.getBytes()))
                    .build()
                    .parseSignedClaims(tramaJwt)
                    .getPayload();

            System.out.println("JWT válido recibido");

            ctx.attribute("jwt-claims", claims);

        } catch (ExpiredJwtException e) {
            throw new ForbiddenResponse("El token JWT ha expirado");
        } catch (MalformedJwtException | SignatureException e) {
            throw new ForbiddenResponse("Token JWT inválido");
        }
    }

    public static void listarFormulariosApi(Context ctx) {

        Claims claims = ctx.attribute("jwt-claims");
        String username = claims.get("usuario").toString();

        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1);
        int size = ctx.queryParamAsClass("size", Integer.class).getOrDefault(8);
        int skip = (page - 1) * size;
        List<Formulario> lista = FormularioServices
                .getInstancia()
                .listarFormulariosPorUsuarioPaginado(username, skip, size);

        long total = FormularioServices.getInstancia().contarFormulariosPorUsuario(username);

        ctx.json(Map.of(
                "data", lista,
                "total", total,
                "page", page,
                "size", size
        ));
    }

    public static void crearFormularioApi(Context ctx) {
        Claims claims = ctx.attribute("jwt-claims");
        String username = claims.get("usuario").toString();
        Formulario formulario = ctx.bodyAsClass(Formulario.class);
        Usuario usuario = UsuarioService.getInstancia().findByUsername(username);
        UsuarioEmbebido usuarioEmbebido = new UsuarioEmbebido();
        usuarioEmbebido.setUsername(usuario.getUsername());
        usuarioEmbebido.setNombre(usuario.getNombre());
        formulario.setUsuario(usuarioEmbebido);
        formulario.setFechaRegistro(new Date());
        FormularioServices.getInstancia().crearFormulario(formulario);
        ctx.status(HttpStatus.CREATED).json(formulario);
    }

}
