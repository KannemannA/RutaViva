package com.bookingProject.tour.exp.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handlerDuplicate(Exception e, WebRequest request){
        String uri= request.getDescription(false);
        if (uri.contains("category")) return new ResponseEntity<>("El nombre de la categoria ya esta en uso.",HttpStatus.BAD_REQUEST);
        if (uri.contains("user")) return new ResponseEntity<>("El email proporcionado ya se encuentra resgitrado",HttpStatus.BAD_REQUEST);
        if (uri.contains("character")) return new ResponseEntity<>("El nombre de la característica ya esta en uso. Por favor elige un nombre de característica diferente.", HttpStatus.BAD_REQUEST);
        if (uri.contains("product")) return new ResponseEntity<>("El nombre del producto ya esta en uso. Por favor elige un nombre de producto diferente.",HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>("ERROR: "+e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> jwtExceptionHandler(Exception ex){
        if(ex instanceof BadCredentialsException){
            return new ResponseEntity<>("El email o password no son correctas",HttpStatus.UNAUTHORIZED);
        }
        if (ex instanceof AccessDeniedException){
            return new ResponseEntity<>("No tienes suficientes privilegios para acceder a este recurso",HttpStatus.FORBIDDEN);
        }
        if(ex instanceof SignatureException){
            return new ResponseEntity<>("El token parece estar adulterado",HttpStatus.FORBIDDEN);
        }
        if (ex instanceof ExpiredJwtException){
            return new ResponseEntity<>("El token expiro",HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(ex.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
