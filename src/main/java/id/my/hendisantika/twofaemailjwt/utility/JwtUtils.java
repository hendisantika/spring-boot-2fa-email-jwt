package id.my.hendisantika.twofaemailjwt.utility;

import id.my.hendisantika.twofaemailjwt.config.jwt.JwtConfigurationProperties;
import id.my.hendisantika.twofaemailjwt.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-2fa-email-jwt
 * User: hendisantika
 * Link: s.id/hendisantika
 * Email: hendisantika@yahoo.co.id
 * Telegram : @hendisantika34
 * Date: 22/04/25
 * Time: 07.02
 * To change this template use File | Settings | File Templates.
 */
@Component
@AllArgsConstructor
@EnableConfigurationProperties(JwtConfigurationProperties.class)
public class JwtUtils {

    private final JwtConfigurationProperties jwtConfigurationProperties;

    public String extractEmail(final String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public UUID extractUserId(final String token) {
        return UUID.fromString((String) extractAllClaims(token).get("user_id"));
    }

    public Date extractExpiration(final String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(final String token, final Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        String secretKey = jwtConfigurationProperties.getJwt().getSecretKey();
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Boolean isTokenExpired(final String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    public String generateAccessToken(final User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("account_creation_timestamp", user.getCreatedAt().toString());
        claims.put("user_id", user.getId());
        claims.put("email_id", user.getEmailId());
        claims.put("email_verified", user.isEmailVerified());
        return createToken(claims, user.getEmailId(), TimeUnit.HOURS.toMillis(1));
    }

    public String generateRefreshToken(final User user) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, user.getEmailId(), TimeUnit.DAYS.toMillis(15));
    }

    private String createToken(final Map<String, Object> claims, final String subject,
                               final Long expiration) {
        String secretKey = jwtConfigurationProperties.getJwt().getSecretKey();
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

    public Boolean validateToken(final String token, final UserDetails userDetails) {
        try {
            final String username = extractEmail(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (ExpiredJwtException e) {
            return false;
        }
    }
}
