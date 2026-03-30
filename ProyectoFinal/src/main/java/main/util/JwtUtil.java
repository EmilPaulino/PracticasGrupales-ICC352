package main.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {

    private static final String SECRET = "pw_proyecto_jwt_clave_super_segura_2026_123456";

    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; //24 Horas

    public static String generarToken(String username) {
        Date now = new Date();
        Date expiracion = new Date(now.getTime() + EXPIRATION_TIME);
        return Jwts.builder().issuer("PW").subject("Login").issuedAt(now).expiration(expiracion).claim("username", username).signWith(KEY).compact();
    }

    public static Claims validarToken(String token) {
        return Jwts.parser().verifyWith(KEY).build().parseSignedClaims(token).getPayload();
    }
}