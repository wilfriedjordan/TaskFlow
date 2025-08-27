package com.taskflow.taskflowBackend.utils;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtils {

    @Value("${app.secret-key}")
    private String secretKey;

    @Value("${app.expiration-time}")
    private Long expirationTime;

    public String generateToken(String username){
        Map<String, Object> claims = new HashMap<String, Object>();
        return createToken(claims,username);
    }

    private String createToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSignkey(),SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignkey() {
        byte [] keyBytes = secretKey.getBytes();
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    public Boolean validateToken(String token, UserDetails userDetails){
        String username = extractUsernameToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpire(token));
    }

    private boolean isTokenExpire(String token) {
        return expirationDate(token).before(new Date());
    }

    private Date expirationDate(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractUsernameToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token , Function<Claims, T> claimsTFunction) {
        final Claims claims = Jwts.parser()
                .setSigningKey(getSignkey())
                .parseClaimsJws(token)
                .getBody();
        return claimsTFunction.apply(claims);
    }
}
