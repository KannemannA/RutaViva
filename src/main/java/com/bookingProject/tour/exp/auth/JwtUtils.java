package com.bookingProject.tour.exp.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtils {
    @Value("${spring.secret.key}")
    private String secretKey;
    @Value("${spring.time.expiration}")
    private String timeExpiration;

    //generar token de acceso
    public String generarTokenkey(Map<String,Object>extraClamis, UserDetails userDetails){
        return Jwts.builder()
                .setClaims(extraClamis)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+Long.parseLong(timeExpiration)))
                .signWith(SignatureAlgorithm.HS256,getSignatureKey())
                .compact();
    }

    //validar el token de acceso
    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username= getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername())&& !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractEpiration(token).before(new Date());
    }

    private Date extractEpiration(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    //obtener el email del token
    public String getUsernameFromToken(String token){
        return getClaim(token,Claims::getSubject);
    }

    //obtener un solo claim
    public <T> T getClaim(String token, Function<Claims,T> claimsTFunction){
        Claims claims= extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }

    //obtener todos los claims del token
    public Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSignatureKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    //obtener firma del token
    public Key getSignatureKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
