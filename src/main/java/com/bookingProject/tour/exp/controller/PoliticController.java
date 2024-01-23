package com.bookingProject.tour.exp.controller;

import com.bookingProject.tour.exp.config.OpenApiConfig;
import com.bookingProject.tour.exp.entity.Politic;
import com.bookingProject.tour.exp.entity.dto.politic.SavePolitic;
import com.bookingProject.tour.exp.service.IPoliticService;
import com.bookingProject.tour.exp.entity.dto.politic.PoliticDTO;
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
import java.util.List;

@RestController
@RequestMapping("/politic")
@Tag(name = "5. POLITICAS - Endpoints", description = "Contiene la lógica necesaria para la gestión de politicas de productos")
public class PoliticController {

    @Autowired
    private IPoliticService politicService;

    @Operation(summary = "Utilice este endpoint para crear una politica de producto", description = """
            Tenga en cuenta que las nuevas politicas que esten asignadas a productos tendran una vigencia hasta las 00:00 AM (GMT-3).
            
            Para concretar la operacion necesita hacer uso de un usuario con rol ADMIN.""", responses = {
            @ApiResponse(responseCode = "201", description = "Peticion correcta.",
                    content = @Content(schema = @Schema(implementation = Politic.class))),
            @ApiResponse(responseCode = "400", description = "No se pudo completar la operacion.",
            content = @Content(schema = @Schema(implementation = String.class),examples = {
                    @ExampleObject(name = "Nombre inválido", description = "Introduzca un nombre representativo para la politica. Minimo 4 caracteres.", value = "Se necesita un nombre para la politica de minimo 4 caracteres. Solo aceptamos letras o espacios. (no cuentan como caracter los espacios)"),
                    @ExampleObject(name = "Descripcion inválido", description = "Requisitos: minimo 20 caracteres.", value = "Se necesita una descripcion para la politica de minimo 20 caracteres. (no cuentan como caracter los espacios)")}))
    })
    @OpenApiConfig.WrongResponsesJWT
    @SecurityRequirement(name = "BearerToken",scopes = {"ADMIN"})
    @PostMapping("/admin/guardar")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> guardarPolitic(@RequestBody SavePolitic savePolitic) {
        if (savePolitic.getTitle()==null||!savePolitic.getTitle().matches("^(.*[a-zA-Z]){4,}.*$")) return new ResponseEntity<>("Se necesita un nombre para la politica de minimo 4 caracteres. Solo aceptamos letras o espacios. (no cuentan como caracter los espacios)",HttpStatus.BAD_REQUEST);
        if (savePolitic.getDescription()==null||!savePolitic.getDescription().matches("^(.*\\S){20,}.*$")) return new ResponseEntity<>("Se necesita una descripcion para la politica de minimo 20 caracteres. (no cuentan como caracter los espacios)",HttpStatus.BAD_REQUEST);
        return politicService.crearPolitica(savePolitic);
    }

    @Operation(summary = "Utilice este endpoint para traer todas las politicas de productos", description = "Obtenga todas las politicas de productos de la base de datos.", responses = {
            @ApiResponse(responseCode = "200", description = "Peticion correcta.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Politic.class))))
    })
   @GetMapping("/public/traerTodo")
   @ResponseStatus(HttpStatus.OK)
   public List<Politic> politics(){
        return politicService.traerPolitica();
   }

   @Operation(summary = "Utilice este endpoint para traer una politica de producto", description = "Obtenga los campos title y description de la politica deseada.", responses = {
           @ApiResponse(responseCode = "200", description = "Peticion correcta.",
           content = @Content(schema = @Schema(implementation = Politic.class))),
           @ApiResponse(responseCode = "404", description = "Politica no encontrada.",
           content = @Content(schema = @Schema(implementation = String.class),examples = {
                   @ExampleObject(name = "Politica no encontrada", description = "No se encontro la politica por ID en nuestra base de datos.", value = "No se encontró la politica en la base de datos.")}))
   })
   @GetMapping("/public/detalle/{id}")
   public ResponseEntity<?> traerUno(@PathVariable Long id){
      return politicService.traerId(id);
   }

    @Operation(summary = "Utilice este endpoint para modificar todos los campos de politicas de productos", description = """
            Tenga en cuenta que necesita tener un usuario con rol ADMIN. NO podra modificar las politicas de muestra, en este caso los IDs menores a 87.
            
            Es necesario que todos los campos contengan datos válidos. Para modificar politicas deberá crear nuevas politicas que, si estan asignadas a productos tendran vigencia hasta las 00:00 AM (GMT-3).""", responses = {
            @ApiResponse(responseCode = "200", description = "Peticion correcta.",
                    content = @Content(schema = @Schema(implementation = Politic.class))),
            @ApiResponse(responseCode = "400", description = "No se pudo completar la operacion.",
            content = @Content(schema = @Schema(implementation = String.class), examples = {
                    @ExampleObject(name = "ID inválido", description = "Solo se permiten ID mayores a 86", value = "Introduzca un numero mayor a 86."),
                    @ExampleObject(name = "Nombre inválido", description = "Introduzca un nombre representativo para la politica. Minimo 4 caracteres.", value = "Se necesita un nombre para la politica de minimo 4 caracteres. Solo aceptamos letras o espacios. (no cuentan como caracter los espacios)"),
                    @ExampleObject(name = "Descripcion inválido", description = "Requisitos: minimo 20 caracteres.", value = "Se necesita una descripcion para la politica de minimo 20 caracteres. (no cuentan como caracter los espacios)")})),
            @ApiResponse(responseCode = "404", description = "No se encontro el id como registro de la base de datos",
            content = @Content(schema = @Schema(implementation = String.class), examples = {
                    @ExampleObject(name = "Politica no encontrada", description = "No se encontró la politica.", value = "No se encontró la politica en la base de datos.")
            }))
    })
    @OpenApiConfig.WrongResponsesJWT
    @SecurityRequirement(name = "jwt", scopes = {"ADMIN"})
    @PutMapping("/admin/modificar")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> modificar(@RequestBody PoliticDTO politicDTO) {
        if (politicDTO.getId()==null||politicDTO.getId()<87L) return new ResponseEntity<>("Introduzca un numero mayor a 86.",HttpStatus.BAD_REQUEST);
        if (politicDTO.getTitle()==null||!politicDTO.getTitle().matches("^(.*[a-zA-Z]){4,}.*$")) return new ResponseEntity<>("Se necesita un nombre para la politica de minimo 4 caracteres. Solo aceptamos letras o espacios. (no cuentan como caracter los espacios)",HttpStatus.BAD_REQUEST);
        if (politicDTO.getDescription()==null||!politicDTO.getDescription().matches("^(.*\\S){20,}.*$")) return new ResponseEntity<>("Se necesita una descripcion para la politica de minimo 20 caracteres. (no cuentan como caracter los espacios)",HttpStatus.BAD_REQUEST);
        return politicService.modificarPolitica(politicDTO);
    }

    @Operation(summary = "Utilice este endpoint para modificar algunos de los campos de la politica de productos", description = """
            Tenga en cuenta que necesita tener un usuario con rol ADMIN. NO podra modificar las politicas de muestra, en este caso los IDs menores a 87.
            
            Es necesario que el campo ID y los campos a modificar contengan datos válidos. Para modificar politicas deberá crear nuevas politicas que, si estan asignadas a productos tendran vigencia hasta las 00:00 AM (GMT-3).""", responses = {
            @ApiResponse(responseCode = "200", description = "Peticion correcta.",
                    content = @Content(schema = @Schema(implementation = Politic.class))),
            @ApiResponse(responseCode = "400", description = "No se pudo completar la operacion.",
                    content = @Content(schema = @Schema(implementation = String.class), examples = {
                            @ExampleObject(name = "ID inválido", description = "Solo se permiten ID mayores a 86", value = "Introduzca un numero mayor a 86."),
                            @ExampleObject(name = "Nombre inválido", description = "Introduzca un nombre representativo para la politica. Minimo 4 caracteres.", value = "Se necesita un nombre para la politica de minimo 4 caracteres. Solo aceptamos letras o espacios. (no cuentan como caracter los espacios)"),
                            @ExampleObject(name = "Descripcion inválido", description = "Requisitos: minimo 20 caracteres.", value = "Se necesita una descripcion para la politica de minimo 20 caracteres. (no cuentan como caracter los espacios)")})),
            @ApiResponse(responseCode = "404", description = "No se encontro el id como registro de la base de datos",
                    content = @Content(schema = @Schema(implementation = String.class), examples = {
                            @ExampleObject(name = "Politica no encontrada", description = "No se encontró la politica.", value = "No se encontró la politica en la base de datos.")
                    }))
    })
    @OpenApiConfig.WrongResponsesJWT
    @SecurityRequirement(name = "jwt", scopes = {"ADMIN"})
    @PatchMapping("/admin/parcialMod")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> parcialMod(@RequestBody PoliticDTO politicDTO) {
        if (politicDTO.getId()==null||politicDTO.getId()<87L) return new ResponseEntity<>("Introduzca un numero mayor a 86.",HttpStatus.BAD_REQUEST);
        if (politicDTO.getTitle()!=null&&!politicDTO.getTitle().matches("^(.*[a-zA-Z]){4,}.*$")) return new ResponseEntity<>("Se necesita un nombre para la politica de minimo 4 caracteres. Solo aceptamos letras o espacios. (no cuentan como caracter los espacios)",HttpStatus.BAD_REQUEST);
        if (politicDTO.getDescription()!=null&&!politicDTO.getDescription().matches("^(.*\\S){20,}.*$")) return new ResponseEntity<>("Se necesita una descripcion para la politica de minimo 20 caracteres. (no cuentan como caracter los espacios)",HttpStatus.BAD_REQUEST);
        return politicService.parcialMod(politicDTO);
    }

    @Operation(summary = "Utilice este endpoint para borrar una politica de producto", description = "Tenga en cuenta que necesita tener un usuario con rol ADMIN. NO podra modificar las politicas de muestra, en este caso los IDs menores a 87.", responses = {
            @ApiResponse(responseCode = "200", description = "La politica se borro exitosamente"),
            @ApiResponse(responseCode = "400", description = "La politica no se pudo eliminar correctamente.",
                    content = @Content(schema = @Schema(implementation = String.class), examples = {
                            @ExampleObject(name = "ID inválido", description = "Solo se permiten ID mayores a 86", value = "Introduzca un numero mayor a 86."),})),
            @ApiResponse(responseCode = "404", description = "No se encontro el id como registro de la base de datos",
                    content = @Content(schema = @Schema(implementation = String.class), examples = {
                            @ExampleObject(name = "Politica no encontrada", description = "No se encontró la politica.", value = "No se encontró la politica en la base de datos.")
                    }))
    })
    @OpenApiConfig.WrongResponsesJWT
    @SecurityRequirement(name = "jwt",scopes = {"ADMIN"})
    @DeleteMapping("/admin/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> eliminarPolitic(@PathVariable Long id) {
        if (id==null||id<87L) return new ResponseEntity<>("Introduzca un numero mayor a 86.",HttpStatus.BAD_REQUEST);
        return politicService.eliminarPolitica(id);
    }
}
