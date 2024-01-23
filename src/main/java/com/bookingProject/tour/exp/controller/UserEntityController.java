package com.bookingProject.tour.exp.controller;

import com.bookingProject.tour.exp.config.OpenApiConfig;
import com.bookingProject.tour.exp.entity.UserEntity;
import com.bookingProject.tour.exp.entity.dto.userEntity.SaveUser;
import com.bookingProject.tour.exp.entity.dto.userEntity.UserEntityDTO;
import com.bookingProject.tour.exp.service.IUserEntityService;
import com.bookingProject.tour.exp.entity.dto.userEntity.LoginUser;
import com.nimbusds.jose.JOSEException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

@RestController
@RequestMapping("/user")
@Tag(name = "1. USUARIOS - Endpoints",description = "Contiene la lógica necesaria para la gestión de usuarios")
public class UserEntityController {
    @Autowired
    private IUserEntityService userEntityService;

    @Operation(summary = "Utilice este endpoint para registrar un nuevo usuario y obtener su token.", description = """
            Los tokens tienen una duración de 24hs desde su obtención.

            Al registrarse le llegará un correo de confirmación de email, pero no hace una validación real.""", responses = {
            @ApiResponse( responseCode = "201",
                    description = "El registro del usuario finalizó correctamente.",
                    content = @Content(schema = @Schema(implementation = String.class), examples = {@ExampleObject(name = "Token",description = "Utilice el token para rellenar el pop-up que aparece luego de pinchar en el boton authorize",value = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")})),
            @ApiResponse( responseCode = "400",
            description = "El registro del usuario no se pudo completar.",
            content = @Content(schema = @Schema(implementation = String.class),examples = {
                    @ExampleObject(name = "Email duplicado", description = "No se permiten crear dos usuarios con el mismo email.", value = "El email proporcionado ya se encuentra resgitrado."),
                    @ExampleObject(name = "Formato email", description = "Introduzca un email con formato valido", value = "Introduzca un correo con formato valido. ej example@example.com"),
                    @ExampleObject(name = "Nombre-apellido inválido", description = "Requisitos: minimo 4 caracteres, solo letras", value = "Solo aceptamos letras o espacios para los campos nombre y apellido, cada uno con un minimo de 4 caracteres. (no cuentan como caracter los espacios)"),
                    @ExampleObject(name = "Contraseña inválida", description = "Requisitos: minimo 4 caracteres.", value = "La contraseña necesita de un minimo de 4 caracteres.")}))
    })
    @PostMapping("/public/guardar")
    @Transactional
    public ResponseEntity<?> createUser(@RequestBody SaveUser user) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, JOSEException {
        if (!user.getEmail().matches("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$")) return new ResponseEntity<>("Introduzca un correo con formato valido. ej example@example.com", HttpStatus.BAD_REQUEST);
        if (!user.getName().matches("^[a-zA-Z]{4,}$")||!user.getLastName().matches("^[a-zA-Z]{4,}$")) return new ResponseEntity<>("Solo aceptamos letras o espacios para los campos nombre y apellido, cada uno con un minimo de 4 caracteres. (no cuentan como caracter los espacios)",HttpStatus.BAD_REQUEST);
        if (!user.getPassword().matches("^\\S{3,}\\S$")) return new ResponseEntity<>("La contraseña necesita de un minimo de 4 caracteres.",HttpStatus.BAD_REQUEST);
        return userEntityService.registrarUsuario(user);
    }


    @Operation(summary = "Utilice este endpoint para logearse con un usuario existente y obtener su token", description = "Los tokens tienen una duración de 24hs desde su obtención.", responses = {
            @ApiResponse(responseCode = "200",
            description = "Autenticacion correcta.",
            content = @Content(schema = @Schema(implementation = String.class), examples = {@ExampleObject(name = "Token",description = "Utilice el token para rellenar el pop-up que aparece luego de pinchar en el boton authorize", value = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")})),
            @ApiResponse(responseCode = "401",
            description = "Autenticacion incorrecta.",
            content = @Content(schema = @Schema(implementation = String.class), examples = {@ExampleObject(name = "Credenciales incorrectas", description = "Los datos ingresados no coinciden.", value = "El email o password no son correctas")}))
    })
    @PostMapping("/public/login")
    public ResponseEntity<?> login(@RequestBody LoginUser login) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, JOSEException {
        return userEntityService.login(login);
    }

    @Operation(summary = "Utilice este endpoint para traer todos los datos de los usuarios de nuestra base de datos", description = "Obtenga los datos de los campos id, name, lastname, email, password y role de todos los usuarios de nuestra base de datos.", responses = {
            @ApiResponse(responseCode = "200",
            description = "Tenga en cuenta que necesita tener un usuario con role ADMIN.",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserEntity.class))))
    })
    @OpenApiConfig.WrongResponsesJWT
    @SecurityRequirement(name = "JWT Security",scopes = {"ADMIN"})
    @GetMapping("/admin/traerTodo")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public List<UserEntity> traerTodo(){
        return userEntityService.traerTodo();
    }

    @Operation(summary = "Utilice este endpoint para traer la información de un usuario", description = "Tenga en cuenta que necesita tener un usuario con role ADMIN.", responses = {
            @ApiResponse(responseCode = "200", description = "peticion correcta.",
                content = @Content(schema = @Schema(implementation = UserEntity.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado, el ID no existe en nuestra base de datos.",
                content = @Content(schema = @Schema(implementation = String.class),examples = {@ExampleObject(name = "Usuario no encontrado.", description = "No se encontro el usuario en nuestra base de datos", value = "No se encontró el id en la base de datos")}))})
    @OpenApiConfig.WrongResponsesJWT
    @SecurityRequirement(name = "JWT Security",scopes = {"ADMIN"})
    @GetMapping("/admin/detalle/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> detalleUser(@PathVariable Long id){
        return userEntityService.traerId(id);
    }

    @Operation(summary = "Utilice este endpoint para modificar los datos de todos los campos del usuario", description = """
                    Tenga en cuenta que necesita tener un usuario con role ADMIN. NO podra modificar el usuario de muestra, en este caso el usuario con ID 1.
                    
                    Unicamente podra modificar los campos name, lastname, password y rol. Es necesario que todos los campos contengan informacion valida.""", responses = {
            @ApiResponse(responseCode = "200", description = "Peticion correcta.",
                    content = @Content(schema = @Schema(implementation = UserEntity.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado, el ID no existe en nuestra base de datos.",
                    content = @Content(schema = @Schema(implementation = String.class),examples = {@ExampleObject(name = "Usuario no encontrado.", description = "No se encontro el usuario en nuestra base de datos", value = "No se encontró el id en la base de datos")})),
            @ApiResponse( responseCode = "400",
                    description = "La modificacion del usuario no se pudo completar.",
                    content = @Content(schema = @Schema(implementation = String.class),examples = {
                            @ExampleObject(name = "ID inválido", description = "Solo se permiten ID mayores a 1", value = "Introduzca un numero mayor a uno."),
                            @ExampleObject(name = "Nombre-apellido inválido", description = "Requisitos: minimo 4 caracteres, solo letras", value = "Solo aceptamos letras o espacios para los campos nombre y apellido, cada uno con un minimo de 4 caracteres. (no cuentan como caracter los espacios)"),
                            @ExampleObject(name = "Contraseña inválida", description = "Requisitos: minimo 4 caracteres.", value = "La contraseña necesita de un minimo de 4 caracteres.")}))
    })
    @OpenApiConfig.WrongResponsesJWT
    @SecurityRequirement(name = "JWT Security",scopes = {"ADMIN"})
    @PutMapping("/admin/modificar")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> modificar(@RequestBody UserEntityDTO userEntityDTO){
        if (userEntityDTO.getId()==null||userEntityDTO.getId()<2L) return new ResponseEntity<>("Introduzca un numero mayor a uno.",HttpStatus.BAD_REQUEST);
        if (!userEntityDTO.getName().matches("^[a-zA-Z]{4,}$")||!userEntityDTO.getLastName().matches("^[a-zA-Z]{4,}$")) return new ResponseEntity<>("Solo aceptamos letras o espacios para los campos nombre y apellido, cada uno con un minimo de 4 caracteres. (no cuentan como caracter los espacios)",HttpStatus.BAD_REQUEST);
        if (!userEntityDTO.getPassword().matches("^\\S{3,}\\S$")) return new ResponseEntity<>("La contraseña necesita de un minimo de 4 caracteres.",HttpStatus.BAD_REQUEST);
        return userEntityService.modificarUsuario(userEntityDTO);
    }

    @Operation(summary = "Utilice este endpoint para modificar algunos datos de los campos del usuario", description = """
                    Tenga en cuenta que necesita tener un usuario con role ADMIN. NO podra modificar el usuario de muestra, en este caso el usuario con ID 1.
                    
                    Unicamente podra modificar los campos name, lastname, password y rol. Unicamente rellene los campos que desea modificar, el resto de los campos puede eliminarlos. Campos necesarios ID y el campo a modificar.""", responses = {
            @ApiResponse(responseCode = "200", description = "Peticion correcta.",
                    content = @Content(schema = @Schema(implementation = UserEntity.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado, el ID no existe en nuestra base de datos.",
                    content = @Content(schema = @Schema(implementation = String.class),examples = {@ExampleObject(name = "Usuario no encontrado.", description = "No se encontro el usuario en nuestra base de datos", value = "No se encontró el id en la base de datos")})),
            @ApiResponse( responseCode = "400",
                    description = "La modificacion del usuario no se pudo completar.",
                    content = @Content(schema = @Schema(implementation = String.class),examples = {
                            @ExampleObject(name = "ID inválido", description = "Solo se permiten ID mayores a 1", value = "Introduzca un numero mayor a uno."),
                            @ExampleObject(name = "Nombre-apellido inválido", description = "Requisitos: minimo 4 caracteres, solo letras", value = "Solo aceptamos letras o espacios para los campos nombre y apellido, cada uno con un minimo de 4 caracteres. (no cuentan como caracter los espacios)"),
                            @ExampleObject(name = "Contraseña inválida", description = "Requisitos: minimo 4 caracteres.", value = "La contraseña necesita de un minimo de 4 caracteres.")}))
    })
    @OpenApiConfig.WrongResponsesJWT
    @SecurityRequirement(name = "JWT Security",scopes = {"ADMIN"})
    @PatchMapping("/admin/parcialMod")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> parcialMod(@RequestBody UserEntityDTO userEntityDTO){
        if (userEntityDTO.getId()==null||userEntityDTO.getId()<2L) return new ResponseEntity<>("Introduzca un numero mayor a uno.",HttpStatus.BAD_REQUEST);
        if (userEntityDTO.getName()!=null&&
            !userEntityDTO.getName().matches("^[a-zA-Z]{4,}$")||
            userEntityDTO.getLastName()!=null&&
            !userEntityDTO.getLastName().matches("^[a-zA-Z]{4,}$")) return new ResponseEntity<>("Solo aceptamos letras o espacios para los campos nombre y apellido, cada uno con un minimo de 4 caracteres. (no cuentan como caracter los espacios)",HttpStatus.BAD_REQUEST);
        if (userEntityDTO.getPassword()!=null&&
            !userEntityDTO.getPassword().matches("^\\S{3,}\\S$")) return new ResponseEntity<>("La contraseña necesita de un minimo de 4 caracteres.",HttpStatus.BAD_REQUEST);
        return userEntityService.patchMod(userEntityDTO);
    }

    @Operation(summary = "Utilice este endpoint para borrar un usuario", description = "Tenga en cuenta que necesita tener un usuario con role ADMIN. NO podra borrar el usuario de muestra, en este caso el usuario con ID 1.", responses = {
            @ApiResponse(responseCode = "200", description = "Usuario eliminado."),
            @ApiResponse(responseCode = "400", description = "No puede borrar el usuario de muestra.",
            content = @Content(schema = @Schema(implementation = String.class),examples = {
                    @ExampleObject(name = "Usuario de muestra", description = "No puede borrar el usuario de muestra.", value = "No se puede eliminar el usuario de muestra")})),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado, el ID no existe en nuestra base de datos.",
                    content = @Content(schema = @Schema(implementation = String.class),examples = {@ExampleObject(name = "Usuario no encontrado.", description = "No se encontro el usuario en nuestra base de datos", value = "No se encontró el id en la base de datos")}))
    })
    @OpenApiConfig.WrongResponsesJWT
    @SecurityRequirement(name = "JWT Security",scopes = {"ADMIN"})
    @DeleteMapping("/admin/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> delete(@PathVariable Long id){
        if (id<2L) return new ResponseEntity<>("No se puede eliminar el usuario de muestra",HttpStatus.BAD_REQUEST);
        return userEntityService.borrarUsuario(id);
    }
}
