package com.bookingProject.tour.exp.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@OpenAPIDefinition(
        info = @Info(
                title = "Rutaviva Doc Api",
                contact = @Contact(
                        name = "Contactame en LinkedIn",
                        url = "https://www.linkedin.com/in/alejo-kannemann-10b58b191/"
                ),
                description = """
                        Rutaviva es un proyecto que ofrece un portal para reservar experiencias turísticas. Con esta API, podrás realizar las siguientes acciones:
                        * Crear nuevos usuarios, obtener, modificar y eliminar, incluso modificar el rol del usuario. (USER - ADMIN)
                        * Crear nuevos productos turísticos, obtener, modificar y eliminar, asignarle categoría, características y políticas de uso.
                        * Crear nuevas categorías, obtener, modificar y eliminar, y asignarselas a productos.
                        * Crear nuevas características, obtener, modificar y eliminar, y asignarselas a productos.
                        * Crear nuevas políticas de uso, obtener, modificar y eliminar, y asignarselas a productos.
                        * Crear nuevas reservas de productos, modificar fechas o cancelarlas.
                        * Próximamente, puntuar las experiencias reservadas y ver puntuaciones de otros usuarios.
                        
                        Esta API está desarrollada con el lenguaje Java y el framework Spring Boot. Implementa los métodos HTTP GET, POST, PUT, PATCH y DELETE para interactuar con los recursos, a la hora de registrarse nuevo usuario, crear, modificar y eliminar una reserva recibira un correo electronico al correo proporcionado por el usuario al registrase.
                        
                        Para comenzar a operar, les proporciono un usuario con rol ADMIN:
                        * Email: ADMIN@admin.com
                        * Password: 1234
                        
                        Tenga en cuenta que los productos, categorias, caracteristicas nuevos que guarde en la base de datos tiene una vigencia hasta las 00:00 AM (GMT-3), esto es una medida que tomé ya que estas entidades hacen uso de imagenes para ser guardadas y no existen muchos servicios de almacenamiento gratuitos con gran volumen de almacenamiento, asi como las entidades relacionadas.
                        """,
                version = "1.0"
        ),
        servers = {
                @Server(
                        url = "https://project-1.alejokannemann.com.ar",
                        description = "Prod ENV"),
                @Server(
                        url = "http://localhost:9090",
                        description = "Test ENV"
                )
        }

)
@SecurityScheme(
        name = "JWT Security",
        description = "rellene el campo con la respuesta obtenida del metodo login o register de USUARIOS - Endpoints",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig  {

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponse(responseCode = "403", description = "peticion denegada.",
    content = @Content(schema = @Schema(implementation = Schema.class),examples = {
            @ExampleObject(name = "Permisos insuficientes", description = "El usuario con el que intenta acceder al recurso no tiene los permisos de role suficientes.", value = "No tienes suficientes privilegios para acceder a este recurso"),
            @ExampleObject(name = "Token modificado",description = "El token parece estar modificado, no se reconoce como un token nuestro.", value = "El token parece estar adulterado"),
            @ExampleObject(name = "Token expirado", description = "El token ingresado no se encuentra vigente", value = "El token expiro")
    }))
    public @interface WrongResponsesJWT {
    }


}
    /* No lo borro ya que lo tengo como modelo por si lo necesito algun dia.
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public @interface ApiResponsesDefault {
    }*/