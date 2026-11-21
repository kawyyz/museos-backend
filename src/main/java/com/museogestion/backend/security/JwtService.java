package com.museogestion.backend.security;

import com.museogestion.backend.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    private final SecretKey key;
    private final long accessMs;
    private final long refreshMs;

    public JwtService(@Value("${app.jwt.secret:}") String secret,
                      @Value("${app.jwt.access-minutes}") long accessMinutes,
                      @Value("${app.jwt.refresh-days}") long refreshDays) {
        if (secret == null || secret.isBlank()) {
            log.warn("JWT_SECRET no está definido: se generó una clave aleatoria. "
                    + "Los tokens dejarán de valer al reiniciar la aplicación.");
            this.key = Jwts.SIG.HS256.key().build();
        } else {
            this.key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secret));
        }
        this.accessMs = accessMinutes * 60 * 1000;
        this.refreshMs = refreshDays * 24 * 60 * 60 * 1000;
    }

    public String generarAccessToken(Usuario u) {
        return construir(u, "access", accessMs);
    }

    public String generarRefreshToken(Usuario u) {
        return construir(u, "refresh", refreshMs);
    }

    public long getAccessSegundos() {
        return accessMs / 1000;
    }

    /** Verifica la firma y la expiración. Lanza JwtException si el token no es válido. */
    public Claims leer(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    private String construir(Usuario u, String tipo, long duracionMs) {
        Date ahora = new Date();
        return Jwts.builder()
                .subject(u.getCorreo())
                .claim("rol", u.getRol().name())
                .claim("tipo", tipo)
                .issuedAt(ahora)
                .expiration(new Date(ahora.getTime() + duracionMs))
                .signWith(key)
                .compact();
    }
}
