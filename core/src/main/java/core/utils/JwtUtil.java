package core.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Set;

public class JwtUtil {
    // Сырой секрет из ENV (или дефолт)
    private static final String RAW_SECRET =
            System.getenv().getOrDefault("JWT_SECRET", "ChangeMe!");

    // TTL 24 часа
    private static final long TTL_MS = 24 * 60 * 60 * 1000L;

    // Ключ в нужном формате и длине
    private static final SecretKey KEY;

    static {
        try {
            // SHA-256 от нашего RAW_SECRET → 256 бит
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashed = md.digest(RAW_SECRET.getBytes(StandardCharsets.UTF_8));
            // Готовый SecretKey для HS256
            KEY = Keys.hmacShaKeyFor(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Не удалось инициализировать JWT key", e);
        }
    }

    /** Генерация токена с subject=login и claim "roles" */
    public static String generateToken(String login, Set<String> roles) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(login)
                .claim("roles", roles)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + TTL_MS))
                // signWith принимает наш SecretKey и алгоритм
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /** Парсинг и валидация токена */
    public static Jws<Claims> parseToken(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token);
    }
}
