package com.bookingProject.tour.exp.controller;

import com.bookingProject.tour.exp.config.OpenApiConfig;
import com.bookingProject.tour.exp.entity.Characteristic;
import com.bookingProject.tour.exp.entity.dto.characteristc.SaveCharacteristic;
import com.bookingProject.tour.exp.service.ICharacteristicService;
import com.bookingProject.tour.exp.entity.dto.characteristc.CharacteristicDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
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

import java.util.List;

@RestController
@RequestMapping("/character")
@Tag(name = "4. CARACTERISTICAS - Endpoints", description = "Contiene la lógica necesaria para la gestión de caracteristicas de productos")
public class CharacteristicController {
    @Autowired
    private ICharacteristicService characteristicService;

    @Operation(summary = "Utilice este endpoint para guardar una nueva característica", description = """
            Tenga en cuanta que las nuevas características tendran una vigencia hasta las 00:00 AM (GMT-3).
            
            Para concretar la operacion necesita hacer uso de un usuario con rol ADMIN.""", responses = {
            @ApiResponse(responseCode = "201", description = "Petición correcta.",
            content = @Content(schema = @Schema(implementation = CharacteristicController.class))),
            @ApiResponse(responseCode = "400", description = "No se pudo completar la operación.",
            content = @Content(schema = @Schema(implementation = String.class), examples = {
                    @ExampleObject(name = "Características existente", description = "No se permiten crear dos características con el mismo nombre.", value = "El nombre de la característica ya esta en uso. Por favor elige un nombre de característica diferente."),
                    @ExampleObject(name = "Nombre inválido", description = "Introduzca un nombre representativo para la característica. Minimo 3 caracteres.", value = "Se necesita un nombre para la caracteristica de minimo 3 caracteres. Solo aceptamos letras o espacios. (no cuentan como caracter los espacios)"),
                    @ExampleObject(name = "Imagen inválida", description = "Las extensiones de archivos validos que aceptamos son: jpg, png, gif, jpeg y svg.", value = "Solo aceptamos archivos con extensiones jpg, png, gif, jpeg y svg"),
                    @ExampleObject(name = "Imagen requerida", description = "Es requerimiento adjuntar una imagen representativa.", value = "Se necesita una imagen representativa para la característica.")})),
    })
    @OpenApiConfig.WrongResponsesJWT
    @SecurityRequirement(name = "BearerToken",scopes = {"ADMIN"})
    @PostMapping(value = "/admin/guardar", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> guardarCharacter(@ModelAttribute SaveCharacteristic saveCharacteristic) throws JsonProcessingException {
        if (saveCharacteristic.getIcon()==null||saveCharacteristic.getIcon().getSize()==0) return new ResponseEntity<>("Se necesita una imagen representativa para la característica.",HttpStatus.BAD_REQUEST);
        if (saveCharacteristic.getName()!=null&&!saveCharacteristic.getName().matches("^[a-zA-Z]{3,}$")) return new ResponseEntity<>("Se necesita un nombre para la caracteristica de minimo 3 caracteres. Solo aceptamos letras o espacios. (no cuentan como caracter los espacios)",HttpStatus.BAD_REQUEST);
        return characteristicService.guardarCharacter(saveCharacteristic);
    }

    @Operation(summary = "Utilice este endpoint para traer todas las características de productos.", description = "Los campos obtenidos son ID, name y icon de nuestra base de datos.", responses = {
            @ApiResponse(responseCode ="200", description = "Peticion correcta",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Characteristic.class))))
    })
    @GetMapping("/public/traerTodo")
    @ResponseStatus(HttpStatus.OK)
    public List<Characteristic> traerTodo(){
        return characteristicService.traertodo();
    }

    @Operation(summary = "Utilice este endpoint para traer una caracteristica de produto", description = "Obtenga los campos ID, name e icon de la característica de producto deseada.", responses = {
            @ApiResponse(responseCode = "200", description = "Peticion correcta.",
            content = @Content(schema = @Schema(implementation = Characteristic.class))),
            @ApiResponse(responseCode = "404", description = "Característica no encontrada.",
            content = @Content(schema = @Schema(implementation = String.class), examples = {
                    @ExampleObject(name = "Característica no encontrada", description = "No se encontro la característica por ID en nuestra base de datos.", value = "No se encontró el id en la base de datos")
            }))
    })
    @GetMapping("/public/detalle/{id}")
    public ResponseEntity<?> traerId(@PathVariable Long id){
        return characteristicService.traerId(id);
    }

    @Operation(summary = "Utilice este endpoint para modificar todos los campos de características de productos", description = """
            Tenga en cuenta que necesita tener un usuario con rol ADMIN. NO podra modificar las características de muestra, en este caso los IDs menores a 7.
            
            Es necesario que todos los campos contengan datos válidos. Para modificar características deberá crear nuevas características que tendran vigencia hasta las 00:00 AM (GMT-3).""", responses = {
            @ApiResponse(responseCode = "200", description = "Peticion correcta.",
            content = @Content(schema = @Schema(implementation = Characteristic.class))),
            @ApiResponse(responseCode = "400", description = "No se pudo completar la modificación de característica de producto.",
            content = @Content(schema = @Schema(implementation = String.class), examples = {
                    @ExampleObject(name = "ID inválido", description = "Solo se permiten ID mayores a 6", value = "Introduzca un numero mayor a 6."),
                    @ExampleObject(name = "Características existente", description = "No se permiten crear dos características con el mismo nombre.", value = "El nombre de la característica ya esta en uso. Por favor elige un nombre de característica diferente."),
                    @ExampleObject(name = "Nombre inválido", description = "Introduzca un nombre representativo para la característica. Minimo 3 caracteres.", value = "Se necesita un nombre para la caracteristica de minimo 3 caracteres. Solo aceptamos letras o espacios. (no cuentan como caracter los espacios)"),
                    @ExampleObject(name = "Imagen inválida", description = "Las extensiones de archivos validos que aceptamos son: jpg, png, gif, jpeg y svg.", value = "Solo aceptamos archivos con extensiones jpg, png, gif, jpeg y svg"),
                    @ExampleObject(name = "Imagen requerida", description = "Es requerimiento adjuntar una imagen representativa.", value = "Se necesita una imagen representativa para la característica.")})),
            @ApiResponse(responseCode = "404", description = "Característica no encontrada.",
            content = @Content(schema = @Schema(implementation = String.class), examples = {
                    @ExampleObject(name = "Característica no encontrada", description = "No se encontro la característica por ID en nuestra base de datos.", value = "No se encontró la característica en nuestra base de datos.")
            }))
    })
    @OpenApiConfig.WrongResponsesJWT
    @SecurityRequirement(name = "BearerToken",scopes = {"ADMIN"})
    @PutMapping(value = "/admin/modificar", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> modificar(@ModelAttribute CharacteristicDTO characteristicDTO) throws JsonProcessingException {
        if (characteristicDTO.getId()==null||characteristicDTO.getId()<7L) return new ResponseEntity<>("Introduzca un numero mayor a 6.",HttpStatus.BAD_REQUEST);
        if (characteristicDTO.getIcon()==null||characteristicDTO.getIcon().getSize()==0) return new ResponseEntity<>("Se necesita una imagen representativa para la característica.",HttpStatus.BAD_REQUEST);
        if (characteristicDTO.getName()!=null&&!characteristicDTO.getName().matches("^[a-zA-Z]{3,}$")) return new ResponseEntity<>("Se necesita un nombre para la caracteristica de minimo 3 caracteres. Solo aceptamos letras o espacios. (no cuentan como caracter los espacios)",HttpStatus.BAD_REQUEST);
        return characteristicService.modificarCharacter(characteristicDTO);
    }

    @Operation(summary = "Utilice este endpoint para modificar algunos de los campos de la característica de producto", description = """
            Tenga en cuenta que necesita tener un usuario con rol ADMIN. NO podra modificar las características de muestra, en este caso los IDs menores a 7.
            
            Es necesario que el campo ID y los campos a modificar contengan datos válidos. Para modificar características deberá crear nuevas características que tendran vigencia hasta las 00:00 AM (GMT-3).""", responses = {
            @ApiResponse(responseCode = "200", description = "Peticion correcta.",
                    content = @Content(schema = @Schema(implementation = Characteristic.class))),
            @ApiResponse(responseCode = "400", description = "No se pudo completar la modificación de característica de producto.",
                    content = @Content(schema = @Schema(implementation = String.class), examples = {
                            @ExampleObject(name = "ID inválido", description = "Solo se permiten ID mayores a 6", value = "Introduzca un numero mayor a 6."),
                            @ExampleObject(name = "Características existente", description = "No se permiten crear dos características con el mismo nombre.", value = "El nombre de la característica ya esta en uso. Por favor elige un nombre de característica diferente."),
                            @ExampleObject(name = "Nombre inválido", description = "Introduzca un nombre representativo para la característica. Minimo 3 caracteres.", value = "Se necesita un nombre para la caracteristica de minimo 3 caracteres. Solo aceptamos letras o espacios. (no cuentan como caracter los espacios)"),
                            @ExampleObject(name = "Imagen inválida", description = "Las extensiones de archivos validos que aceptamos son: jpg, png, gif, jpeg y svg.", value = "Solo aceptamos archivos con extensiones jpg, png, gif, jpeg y svg"),
                            @ExampleObject(name = "Imagen requerida", description = "Es requerimiento adjuntar una imagen representativa.", value = "Se necesita una imagen representativa para la característica.")})),
            @ApiResponse(responseCode = "404", description = "Característica no encontrada.",
                    content = @Content(schema = @Schema(implementation = String.class), examples = {
                            @ExampleObject(name = "Característica no encontrada", description = "No se encontro la característica por ID en nuestra base de datos.", value = "No se encontró la característica en nuestra base de datos.")
                    }))
    })
    @OpenApiConfig.WrongResponsesJWT
    @SecurityRequirement(name = "BearerToken",scopes = {"ADMIN"})
    @PatchMapping(value = "/admin/parcialMod", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> parcialMod(@ModelAttribute CharacteristicDTO characteristicDTO) throws JsonProcessingException {
        if (characteristicDTO.getId()==null||characteristicDTO.getId()<7L) return new ResponseEntity<>("Introduzca un numero mayor a 6.",HttpStatus.BAD_REQUEST);
        if (characteristicDTO.getIcon()!=null&&characteristicDTO.getIcon().getSize()==0) return new ResponseEntity<>("Se necesita una imagen representativa para la característica.",HttpStatus.BAD_REQUEST);
        if (characteristicDTO.getName()!=null&&!characteristicDTO.getName().matches("^[a-zA-Z]{3,}$")) return new ResponseEntity<>("Se necesita un nombre para la caracteristica de minimo 3 caracteres. Solo aceptamos letras o espacios. (no cuentan como caracter los espacios)",HttpStatus.BAD_REQUEST);
        return characteristicService.parcialMod(characteristicDTO);
    }

    @Operation(summary = "Utilice este endpoint para borrar una característica de producto", description = "Tenga en cuenta que necesita tener un usuario con rol ADMIN. NO podrá eliminar las características de muestra, en este caso los IDs menores a 6.", responses = {
            @ApiResponse(responseCode = "200", description = "La característica a sido eliminada correctamente."),
            @ApiResponse(responseCode = "400", description = "La característica no se pudo eliminar correctamente.",
                    content = @Content(schema = @Schema(implementation = String.class), examples = {
                            @ExampleObject(name = "ID inválido", description = "Solo se permiten ID mayores a 6", value = "Introduzca un numero mayor a 6."),})),
            @ApiResponse(responseCode = "404", description = "El ID no esta relacionado a un registro de nuestra base de datos.",
                    content = @Content(schema = @Schema(implementation = String.class),examples = {
                            @ExampleObject(name = "Característica no encontrada", description = "El ID no se encontro en nuestra base de datos", value = "No se encontró la caracteristica en la base de datos")}))
    })
    @OpenApiConfig.WrongResponsesJWT
    @SecurityRequirement(name = "BearerToken",scopes = {"ADMIN"})
    @DeleteMapping("/admin/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> eliminar(@PathVariable Long id){
        if (id==null||id<7L) return new ResponseEntity<>("Introduzca un numero mayor a 6.",HttpStatus.BAD_REQUEST);
        return characteristicService.eliminar(id);
    }
}
