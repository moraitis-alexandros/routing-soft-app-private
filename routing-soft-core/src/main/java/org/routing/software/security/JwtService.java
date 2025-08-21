package org.routing.software.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.enterprise.context.ApplicationScoped;
import org.routing.software.model.User;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

@ApplicationScoped
public class JwtService {

    //TODO put secret key into env variables && reduce expiration date
    //    Strong security 384-bits = 48 bytes = 64 Base64URL characters
    private final String secretKey;
    private long jwtExpiration = 10800000;  // 3 hours in milliseconds

    public JwtService() {
        // Read secret from environment variable
        String envKey = System.getenv("JWT_SECRET");
        if (envKey == null || envKey.isBlank()) {
            throw new IllegalStateException("JWT_SECRET environment variable is not set!");
        }
        this.secretKey = envKey;
    }
    //TODO token purposes enum
    public String generateToken(String uuid, String role, String token_type) {
        var claims = new HashMap<String, Object>();
        claims.put("role", role); //public claims
        claims.put("token_type", token_type);

        //in case of registration i want a small time window
        long expiration = jwtExpiration;
        if ("registration".equals(token_type)) {
            expiration = 15 * 60 * 1000; // 15 minutes
        }

        return Jwts
                .builder()
                .setIssuer("self") // todo
                .setClaims(claims)
                .setSubject(uuid)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, User user) {
        final String subject = extractSubject(token);
        return (subject.equals(user.getUuid())) && !isTokenExpired(token);
    }

    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isRegistrationToken(String token) {
        Object type = extractClaim(token, c -> c.get("token_type"));
        return "registration".equals(type != null ? type.toString() : null);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Creates a HS256 Key. Key is an interface.
     * Starting from secretKey we get a byte array
     * of the secret. Then we get the {@link javax.crypto.SecretKey,
     * class that implements the {@link Key } interface.
     *
     *
     * @return  a SecretKey which implements Key.
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
