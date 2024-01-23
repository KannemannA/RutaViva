package com.bookingProject.tour.exp.controller;

import com.bookingProject.tour.exp.config.OpenApiConfig;
import com.bookingProject.tour.exp.entity.Category;
import com.bookingProject.tour.exp.entity.dto.category.SaveCategory;
import com.bookingProject.tour.exp.service.ICategoryService;
import com.bookingProject.tour.exp.entity.dto.category.CategoryDTO;
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
@RequestMapping("/category")
@Tag(name = "3. CATEGORIAS - Endpoints", description = "Contiene la lógica necesaria para la gestión de categorias de productos")
public class CategoryController {
    @Autowired
    private ICategoryService categoryService;

    @Operation(summary = "Utilice este endpoint para agregar una nueva categoria la lista de categorias.", description = """
            Tenga en cuanta que las nuevas categorias tendran una vigencia hasta las 00:00 AM (GMT-3).
            
            Para concretar la operacion necesita hacer uso de un usuario con rol ADMIN.""", responses = {
            @ApiResponse( responseCode = "201",
                    description = "La categoria fue agregada correctamente.",
                    content = @Content(schema = @Schema(implementation = Category.class))),
            @ApiResponse( responseCode = "400",
                    description = "La categoria no se pudo crear.",
                    content = @Content(schema = @Schema(implementation = String.class),examples = {
                            @ExampleObject(name = "Categoria existente", description = "No se permiten crear dos categorias con el mismo nombre.", value = "El nombre de la categoria ya esta en uso."),
                            @ExampleObject(name = "Nombre inválido", description = "Introduzca un nombre representativo para la categoria. Minimo 4 caracteres.", value = "Se necesita un nombre para la categoria de minimo 4 caracteres. Solo aceptamos letras o espacios. (no cuentan como caracter los espacios)"),
                            @ExampleObject(name = "Descripcion inválido", description = "Requisitos: minimo 20 caracteres.", value = "Se necesita una descripcion para la categoria de minimo 20 caracteres. (no cuentan como caracter los espacios)"),
                            @ExampleObject(name = "Imagen inválida", description = "Las extensiones de archivos validos que aceptamos son: jpg, png, gif, jpeg y svg.", value = "Solo aceptamos archivos con extensiones jpg, png, gif, jpeg y svg"),
                            @ExampleObject(name = "Imagen requerida", description = "Es requerimiento adjuntar una imagen representativa.", value = "Se necesita una imagen representativa para la categoria.")}))
    })
    @OpenApiConfig.WrongResponsesJWT
    @SecurityRequirement(name = "BearerToken",scopes = {"ADMIN"})
    @PostMapping(value = "/admin/guardar", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> guardarCat(@ModelAttribute SaveCategory saveCategory) throws JsonProcessingException {
        if (saveCategory.getCategory()==null||!saveCategory.getCategory().matches("^[a-zA-Z]{4,}$")) return new ResponseEntity<>("Se necesita un nombre para la categoria de minimo 4 caracteres. Solo aceptamos letras o espacios. (no cuentan como caracter los espacios)",HttpStatus.BAD_REQUEST);
        if (saveCategory.getDescription()==null||!saveCategory.getDescription().matches("^\\S{19,}\\S$")) return new ResponseEntity<>("Se necesita una descripcion para la categoria de minimo 20 caracteres. (no cuentan como caracter los espacios)",HttpStatus.BAD_REQUEST);
        if (saveCategory.getThumbnail()==null||saveCategory.getThumbnail().getSize()==0) return new ResponseEntity<>("Se necesita una imagen representativa para la categoria.",HttpStatus.BAD_REQUEST);
        return categoryService.crearCategoria(saveCategory);
    }

    @Operation(summary = "Utilice este endpoint para traer todos las categorias de productos", description = "Obtenga los datos de los campos id, category, description y thumbnail de todas las categorias de nuestra base de datos.", responses = {
            @ApiResponse(responseCode = "200", description = "Peticion correcta.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Category.class))))
    })
    @GetMapping("/public/traerTodo")
    @ResponseStatus(HttpStatus.OK)
    public List<Category> traerTodo(){
        return categoryService.traercategorias();
    }

    @Operation(summary = "Utilice este endpoint para traer una categoria de producto", description = "Obtrnga los campos ID, category, description y thumbnail de una categoria de producto en especifico.", responses = {
            @ApiResponse(responseCode = "200", description = "Peticion correcta.",
                    content = @Content(schema = @Schema(implementation = Category.class))),
            @ApiResponse(responseCode = "404", description = "No se encontro la categoria.",
            content = @Content(schema = @Schema(implementation = String.class), examples = {
                    @ExampleObject(name = "Categoria no encontrada", description = "No se encontro la categoria por ID en nuestra base de datos.", value = "No se encontró el ID en nuestra base de datos.")
            }))
    })
    @GetMapping("/public/detalle/{id}")
    public ResponseEntity<?> traerUno(@PathVariable Long id){
        return categoryService.traerId(id);
    }

    @Operation(summary = "Utilice este endpoint para modificar todos los campos de la categoria", description = """
            Tenga en cuenta que necesita tener un usuario con rol ADMIN. NO podra modificar las categorias de muestra, en este caso los IDs menores a 6.
            
            Es necesario que todos los campos contengan datos válidos. Para modificar categorias deberá crear nuevas categorias que tendran vigencia hasta las 00:00 AM (GMT-3).""", responses = {
            @ApiResponse( responseCode = "200",
                    description = "La categoria fue modificada correctamente.",
                    content = @Content(schema = @Schema(implementation = Category.class))),
            @ApiResponse( responseCode = "400",
                    description = "La categoria no se pudo modificar.",
                    content = @Content(schema = @Schema(implementation = String.class),examples = {
                            @ExampleObject(name = "ID inválido", description = "Solo se permiten ID mayores a 5", value = "Introduzca un numero mayor a 5."),
                            @ExampleObject(name = "Categoria existente", description = "No se permiten crear dos categorias con el mismo nombre.", value = "El nombre de la categoria ya esta en uso."),
                            @ExampleObject(name = "Nombre inválido", description = "Introduzca un nombre representativo para la categoria. Minimo 4 caracteres.", value = "Se necesita un nombre para la categoria de minimo 4 caracteres. Solo aceptamos letras o espacios. (no cuentan como caracter los espacios)"),
                            @ExampleObject(name = "Descripcion inválido", description = "Requisitos: minimo 20 caracteres.", value = "Se necesita una descripcion para la categoria de minimo 20 caracteres. (no cuentan como caracter los espacios)"),
                            @ExampleObject(name = "Imagen inválida", description = "Las extensiones de archivos validos que aceptamos son: jpg, png, gif, jpeg y svg.", value = "Solo aceptamos archivos con extensiones jpg, png, gif, jpeg y svg"),
                            @ExampleObject(name = "Imagen requerida", description = "Es requerimiento adjuntar una imagen representativa.", value = "Se necesita una imagen representativa para la categoria.")})),
            @ApiResponse(responseCode = "404",
            description = "El ID no esta relacionado a un registro de nuestra base de datos.",
            content = @Content(schema = @Schema(implementation = String.class),examples = {
                    @ExampleObject(name = "Categoria no encontrada", description = "El ID no se encontro en nuestra base de datos", value = "No se encontró la categoría en la base de datos")}))
    })
    @OpenApiConfig.WrongResponsesJWT
    @SecurityRequirement(name = "BearerToken",scopes = {"ADMIN"})
    @PutMapping(value = "/admin/modificar", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> modificar(@ModelAttribute CategoryDTO categoryDTO) throws JsonProcessingException {
        if (categoryDTO.getId()==null||categoryDTO.getId()<6L) return new ResponseEntity<>("Introduzca un numero mayor a 5.",HttpStatus.BAD_REQUEST);
        if (categoryDTO.getCategory()==null||!categoryDTO.getCategory().matches("^[a-zA-Z]{4,}$")) return new ResponseEntity<>("Se necesita un nombre para la categoria de minimo 4 caracteres. Solo aceptamos letras o espacios. (no cuentan como caracter los espacios)",HttpStatus.BAD_REQUEST);
        if (categoryDTO.getDescription()==null||!categoryDTO.getDescription().matches("^\\S{19,}\\S$")) return new ResponseEntity<>("Se necesita una descripcion para la categoria de minimo 20 caracteres. (no cuentan como caracter los espacios)",HttpStatus.BAD_REQUEST);
        if (categoryDTO.getThumbnail()==null||categoryDTO.getThumbnail().getSize()==0) return new ResponseEntity<>("Se necesita una imagen representativa para la categoria.",HttpStatus.BAD_REQUEST);
        return categoryService.modificarCategoria(categoryDTO);
    }

    @Operation(summary = "Utilice este endpoint para modificar algunos de los campos de categoria", description = """
            Tenga en cuenta que necesita tener un usuario con rol ADMIN. NO podra modificar las categorias de muestra, en este caso los IDs menores a 6.
            
            Es necesario que el campo ID y los campos a modificar contengan datos válidos. Para modificar categorias deberá crear nuevas categorias que tendran vigencia hasta las 00:00 AM (GMT-3).""", responses = {
            @ApiResponse( responseCode = "200",
                    description = "La categoria fue modificada correctamente.",
                    content = @Content(schema = @Schema(implementation = Category.class))),
            @ApiResponse( responseCode = "400",
                    description = "La categoria no se pudo modificar.",
                    content = @Content(schema = @Schema(implementation = String.class),examples = {
                            @ExampleObject(name = "ID inválido", description = "Solo se permiten ID mayores a 5", value = "Introduzca un numero mayor a 5."),
                            @ExampleObject(name = "Categoria existente", description = "No se permiten crear dos categorias con el mismo nombre.", value = "El nombre de la categoria ya esta en uso."),
                            @ExampleObject(name = "Nombre inválido", description = "Introduzca un nombre representativo para la categoria. Minimo 4 caracteres.", value = "Se necesita un nombre para la categoria de minimo 4 caracteres. Solo aceptamos letras o espacios. (no cuentan como caracter los espacios)"),
                            @ExampleObject(name = "Descripcion inválido", description = "Requisitos: minimo 20 caracteres.", value = "Se necesita una descripcion para la categoria de minimo 20 caracteres. (no cuentan como caracter los espacios)"),
                            @ExampleObject(name = "Imagen inválida", description = "Las extensiones de archivos validos que aceptamos son: jpg, png, gif, jpeg y svg.", value = "Solo aceptamos archivos con extensiones jpg, png, gif, jpeg y svg"),
                            @ExampleObject(name = "Imagen vacia", description = "Si quiere modificar la imagen, por favor ingrese una imagen válida", value = "No se admiten imagenes vacias.")})),
            @ApiResponse(responseCode = "404",
                    description = "El ID no esta relacionado a un registro de nuestra base de datos.",
                    content = @Content(schema = @Schema(implementation = String.class),examples = {
                            @ExampleObject(name = "Categoria no encontrada", description = "El ID no se encontro en nuestra base de datos", value = "No se encontró la categoría en la base de datos")}))
    })
    @OpenApiConfig.WrongResponsesJWT
    @SecurityRequirement(name = "BearerToken",scopes = {"ADMIN"})
    @PatchMapping(value = "/admin/parcialMod", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> parcialMod(@ModelAttribute CategoryDTO categoryDTO) throws JsonProcessingException {
        if (categoryDTO.getId()==null||categoryDTO.getId()<6L) return new ResponseEntity<>("Introduzca un numero mayor a cinco.",HttpStatus.BAD_REQUEST);
        if (categoryDTO.getCategory()!=null&&!categoryDTO.getCategory().matches("^[a-zA-Z]{4,}$")) return new ResponseEntity<>("Se necesita un nombre para la categoria de minimo 4 caracteres. Solo aceptamos letras o espacios. (no cuentan como caracter los espacios)",HttpStatus.BAD_REQUEST);
        if (categoryDTO.getDescription()!=null&&!categoryDTO.getDescription().matches("^\\S{19,}\\S$")) return new ResponseEntity<>("Se necesita una descripcion para la categoria de minimo 20 caracteres. (no cuentan como caracter los espacios)",HttpStatus.BAD_REQUEST);
        if (categoryDTO.getThumbnail()!=null&&categoryDTO.getThumbnail().getSize()==0) return new ResponseEntity<>("No se admiten imagenes vacias.",HttpStatus.BAD_REQUEST);
        return categoryService.parcialMod(categoryDTO);
    }

    @Operation(summary = "Utilice este endpoint para borrar una categoria", description = "Tenga en cuenta que necesita tener un usuario con rol ADMIN. NO podrá eliminar las categorias de muestra, en este caso los IDs menores a 6.", responses = {
            @ApiResponse(responseCode = "200", description = "La categoria a sido eliminada correctamente."),
            @ApiResponse(responseCode = "400", description = "La categoria no se pudo eliminar correctamente.",
            content = @Content(schema = @Schema(implementation = String.class), examples = {
                    @ExampleObject(name = "ID inválido", description = "Solo se permiten ID mayores a 5", value = "Introduzca un numero mayor a 5."),})),
            @ApiResponse(responseCode = "404",
                    description = "El ID no esta relacionado a un registro de nuestra base de datos.",
                    content = @Content(schema = @Schema(implementation = String.class),examples = {
                            @ExampleObject(name = "Categoria no encontrada", description = "El ID no se encontro en nuestra base de datos", value = "No se encontró la categoría en la base de datos")}))
    })
    @OpenApiConfig.WrongResponsesJWT
    @SecurityRequirement(name = "BearerToken",scopes = {"ADMIN"})
    @DeleteMapping("/admin/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> eliminarCat(@PathVariable Long id){
        if (id==null||id<6L) return new ResponseEntity<>("Introduzca un numero mayor a cinco.",HttpStatus.BAD_REQUEST);
        return categoryService.eliminarCategoria(id);
    }
}
