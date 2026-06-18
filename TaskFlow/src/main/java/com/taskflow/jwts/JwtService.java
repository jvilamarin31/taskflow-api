package com.taskflow.jwts;

import com.taskflow.models.UserModel;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.llave}")
    private String llaveScreta;

    @Value("${jwt.expiracion}")
    private long expiracionMinutos;


    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(llaveScreta);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getToken(String userId, String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);

        return Jwts.builder()
                .claims(claims)
                .subject(userId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiracionMinutos))
                .signWith(getKey(), Jwts.SIG.HS256)
                .compact();
    }

    public String getUserIdFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public Object getClaimByName(String token, String claimName) {
        final Claims claims = getAllClaims(token);
        return claims.get(claimName);
    }

    public boolean isTokenValid(String token, UserModel user) {
        final String usuarioId = getUserIdFromToken(token);
        return (usuarioId.equals(user.getId()) && !isTokenExpired(token));
    }

    private Claims getAllClaims(String token){
        return Jwts
                .parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T getClaim(String token, Function<Claims,T> claimsResolver){
        final Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Date getExpiration(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    public boolean isTokenExpired(String token) {
        try{
            return getExpiration(token).before(new Date());
        } catch (Exception e) {
            return true;
        }

    }
}
