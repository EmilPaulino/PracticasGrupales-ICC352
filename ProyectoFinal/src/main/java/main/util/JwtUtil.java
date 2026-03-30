package main.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

public class JwtUtil {

    private static final String SECRET = "pw_proyecto_jwt_clave_super_segura_2026_123456";

    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    public static String generarToken(String username) {
        LocalDateTime tiempo = LocalDateTime.now().plusHours(2);
        Date expiracion = Date.from(tiempo.toInstant(ZoneOffset.ofHours(-4)));
        return Jwts.builder().issuer("PW").subject("Login").expiration(expiracion).claim("username", username).signWith(KEY).compact();
    }

    public static Claims validarToken(String token) {
        return Jwts.parser().verifyWith(KEY).build().parseSignedClaims(token).getPayload();
    }
}