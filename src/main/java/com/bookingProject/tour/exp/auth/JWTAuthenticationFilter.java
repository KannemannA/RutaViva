package com.bookingProject.tour.exp.auth;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserDetailsService userDetailsService;
    private final HandlerExceptionResolver resolver;

    @Autowired
    public JWTAuthenticationFilter(HandlerExceptionResolver handlerExceptionResolver){
        this.resolver= handlerExceptionResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String tokenCrudo = request.getHeader("Authorization");
        if (tokenCrudo == null || !tokenCrudo.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = tokenCrudo.substring(7);
        try {
            JWTClaimsSet claims = jwtUtils.parseJWT(token);
            UserDetails userDetails= userDetailsService.loadUserByUsername(claims.getSubject());
            UsernamePasswordAuthenticationToken authenticationToken= new UsernamePasswordAuthenticationToken(claims.getSubject(), null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        catch (JOSEException | NoSuchAlgorithmException | InvalidKeySpecException | ParseException ex){
            resolver.resolveException(request,response,null,ex);
        }
        filterChain.doFilter(request, response);
    }
}
