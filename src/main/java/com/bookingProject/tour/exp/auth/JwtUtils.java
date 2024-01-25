package com.bookingProject.tour.exp.auth;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtils {
    @Value("${spring.time.expiration}")
    private String timeExpiration;

    @Value("/etc/secrets/private_key.pem")
    //@Value("classpath:jwtKeys/private_key.pem")
    private Resource privateKeyResource;
    @Value("/etc/secrets/public_key.pem")
    //@Value("classpath:jwtKeys/public_key.pem")
    private Resource publicKeyResource;

    private PrivateKey loadPrivateKey(Resource resource) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes= Files.readAllBytes(Paths.get(resource.getURI()));
        String privateKeyPem = new String(keyBytes, StandardCharsets.UTF_8)
                .replace("-----BEGIN PRIVATE KEY-----","")
                .replace("-----END PRIVATE KEY-----","")
                .replaceAll("\\s","");
        byte[] decodeKey= Base64.getDecoder().decode(privateKeyPem);
        KeyFactory keyFactory= KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(decodeKey));
    }

    private PublicKey loadPublicKey(Resource resource) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes= Files.readAllBytes(Paths.get(resource.getURI()));
        String publicKeyPem = new String(keyBytes, StandardCharsets.UTF_8)
                .replace("-----BEGIN PUBLIC KEY-----","")
                .replace("-----END PUBLIC KEY-----","")
                .replaceAll("\\s","");
        byte[] decodeKey= Base64.getDecoder().decode(publicKeyPem);
        KeyFactory keyFactory= KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(new X509EncodedKeySpec(decodeKey));
    }

    //generar token de acceso
    public String generarTokenkey(UserDetails userDetails) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, JOSEException {
        PrivateKey privateKey= loadPrivateKey(privateKeyResource);
        JWSSigner signer= new RSASSASigner(privateKey);
        JWTClaimsSet claimsSet= new JWTClaimsSet.Builder()
                .subject(userDetails.getUsername())
                .issueTime(new Date(System.currentTimeMillis()))
                .expirationTime(new Date(System.currentTimeMillis()+Long.parseLong(timeExpiration)))
                .build();
        SignedJWT signedJWT= new SignedJWT(new JWSHeader(JWSAlgorithm.RS256),claimsSet);
        signedJWT.sign(signer);
        return signedJWT.serialize();
    }

    //validar el token de acceso
    public JWTClaimsSet parseJWT(String token) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, ParseException, JOSEException {
        PublicKey publicKey= loadPublicKey(publicKeyResource);
        SignedJWT signedJWT= SignedJWT.parse(token);
        JWSVerifier verifier = new RSASSAVerifier((RSAPublicKey) publicKey);
        if (!signedJWT.verify(verifier)){
            throw new JOSEException("El token parece estar adulterado");
        }
        JWTClaimsSet claimsSet= signedJWT.getJWTClaimsSet();
        if (claimsSet.getExpirationTime().before(new Date(System.currentTimeMillis()))){
            throw new JOSEException("El token expiró");
        }
        return claimsSet;
    }
}
