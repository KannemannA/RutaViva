package com.bookingProject.tour.exp.controller;

import com.bookingProject.tour.exp.config.OpenApiConfig;
import com.bookingProject.tour.exp.entity.Characteristic;
import com.bookingProject.tour.exp.entity.Product;
import com.bookingProject.tour.exp.entity.dto.product.SaveProduct;
import com.bookingProject.tour.exp.service.IProductService;
import com.bookingProject.tour.exp.entity.dto.product.ProductDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequestMapping("/product")
@RestController
@Tag(name = "2. PRODUCTOS - Endpoints", description = "Contiene la lógica necesaria para la gestión de productos")
public class ProductController {
    @Autowired
    private IProductService productsService;

    @Operation(summary = "Utilice este endpoint para agregar un nuevo producto al catálogo", description = """
            Tenga en cuanta que los nuevos productos tendran una vigencia hasta las 00:00 AM (GMT-3).
            
            Para concretar la operacion necesita hacer uso de un usuario con rol ADMIN.""", responses = {
            @ApiResponse(responseCode = "200", description = "Peticion correcta",
                    content = @Content(schema = @Schema(implementation = Product.class))),
            @ApiResponse( responseCode = "400",
                    description = "El producto no se pudo guardar.",
                    content = @Content(schema = @Schema(implementation = String.class),examples = {
                            @ExampleObject(name = "Producto existente", description = "No se permiten crear dos productos con el mismo nombre.", value = "El nombre del producto ya esta en uso. Por favor elige un nombre de producto diferente."),
                            @ExampleObject(name = "Nombre inválido", description = "Introduzca un nombre representativo para el producto. Minimo 4 caracteres.", value = "Se necesita un nombre para el producto de minimo 4 caracteres. Solo aceptamos letras o espacios. (no cuentan como caracter los espacios)"),
                            @ExampleObject(name = "Descripcion inválido", description = "Requisitos: minimo 20 caracteres.", value = "Se necesita una descripcion para el producto de minimo 20 caracteres. (no cuentan como caracter los espacios)"),
                            @ExampleObject(name = "Imagen inválida", description = "Las extensiones de archivos validos que aceptamos son: jpg, png, gif, jpeg y svg.", value = "Solo aceptamos archivos con extensiones jpg, png, gif, jpeg y svg"),
                            @ExampleObject(name = "Imagen requerida", description = "Es requerimiento adjuntar una imagen representativa.", value = "Se necesita una imagen representativa para el producto.")}))
    })
    @OpenApiConfig.WrongResponsesJWT
    @SecurityRequirement(name = "BearerToken",scopes = {"ADMIN"})
    @PostMapping(value = "/admin/guardar", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> guardarProd(@ModelAttribute SaveProduct saveProduct) throws JsonProcessingException {
        if (saveProduct.getTitle()==null||!saveProduct.getTitle().matches("^[a-zA-Z]{4,}$")) return new ResponseEntity<>("Se necesita un nombre para el producto de minimo 4 caracteres. Solo aceptamos letras o espacios. (no cuentan como caracter los espacios)",HttpStatus.BAD_REQUEST);
        if (saveProduct.getDescription()==null||!saveProduct.getDescription().matches("^\\S{19,}\\S$")) return new ResponseEntity<>("Se necesita una descripcion para el producto de minimo 20 caracteres. (no cuentan como caracter los espacios)",HttpStatus.BAD_REQUEST);
        if (saveProduct.getThumbnail()==null||saveProduct.getThumbnail().getSize()==0) return new ResponseEntity<>("Se necesita una imagen representativa para el producto.",HttpStatus.BAD_REQUEST);
        if (saveProduct.getImages()==null|| saveProduct.getImages().isEmpty()) return new ResponseEntity<>("se necesitan imagenes representativas para la galeria del producto.",HttpStatus.BAD_REQUEST);
        for (MultipartFile file : saveProduct.getImages()){
            if (file==null||file.getSize()==0) return new ResponseEntity<>("Se necesitan imagenes representativas para la galeria del producto.",HttpStatus.BAD_REQUEST);
        }
        if (saveProduct.getPoliticIds()!=null){
            for (Long id : saveProduct.getPoliticIds()){
                if (id<86L) return new ResponseEntity<>("No puedes hacer uso de las politicas de muestra.",HttpStatus.BAD_REQUEST);
            }
        }
        return productsService.guardarProd(saveProduct);
    }

    @Operation(summary = "Utilice este endpoint para traer todos los productos.", description = "Los campos obtenidos son ID, title, description, thumbnail, images, category, characteristics y politics de los productos de nuestra base de datos.", responses = {
            @ApiResponse(responseCode ="200", description = "Peticion correcta",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Product.class))))
    })
    @GetMapping("/public/traerTodo")
    @ResponseStatus(HttpStatus.OK)
    public List<Product> traerTodos(){
        return productsService.traerTodo();
    }

    @Operation(summary = "Utilice este endpoint para traer un producto.", description = "Los campos obtenidos son ID, title, description, thumbnail, images, category, characteristics y politics del producto de nuestra base de datos.", responses = {
            @ApiResponse(responseCode ="200", description = "Peticion correcta",
                    content = @Content(schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado.",
                    content = @Content(schema = @Schema(implementation = String.class), examples = {
                            @ExampleObject(name = "Producto no encontrado", description = "No se encontro el producto por ID en nuestra base de datos.", value = "No se encontró el id en la base de datos")
                    }))
    })
    @GetMapping("/public/detalle/{id}")
    public ResponseEntity<?> detalleProd(@PathVariable Long id){
        return productsService.buscarProd(id);
    }

    @Operation(summary = "Utilice este endpoint para filtrar los productos por categoria", description = "Los campos obtenidos son ID, title, description, thumbnail, images, category, characteristics y politics de los productos de nuestra base de datos.", responses = {
            @ApiResponse(responseCode = "200", description = "Peticion correcta",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Product.class)))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado.",
                    content = @Content(schema = @Schema(implementation = String.class), examples = {
                            @ExampleObject(name = "Producto no encontrado", description = "No se encontro el producto por ID en nuestra base de datos.", value = "No se encontró el id en la base de datos")
                    }))
    })
    @GetMapping("/public/findCategory")
    public ResponseEntity<?> buscarPorCategory(@RequestParam(value = "category") Long id){
        return productsService.buscarPorCategory(id);
    }

    @Operation(summary = "Utilice este endpoint para modificar todos los campos de producto", description = """
            Tenga en cuenta que necesita tener un usuario con rol ADMIN. NO podra modificar los productos de muestra, en este caso los IDs menores a 27.
            
            Es necesario que todos los campos contengan datos válidos. Para modificar productos deberá crear nuevos productos que tendran una vigencia hasta las 00:00 AM (GMT-3).""", responses = {
            @ApiResponse(responseCode = "200", description = "Peticion correcta.",
                    content = @Content(schema = @Schema(implementation = Characteristic.class))),
            @ApiResponse(responseCode = "400", description = "No se pudo completar la modificación de característica de producto.",
                    content = @Content(schema = @Schema(implementation = String.class), examples = {
                            @ExampleObject(name = "ID inválido", description = "Solo se permiten ID mayores a 26", value = "Introduzca un numero mayor a 26."),
                            @ExampleObject(name = "Producto existente", description = "No se permiten crear dos productos con el mismo nombre.", value = "El nombre del producto ya esta en uso. Por favor elige un nombre de producto diferente."),
                            @ExampleObject(name = "Nombre inválido", description = "Introduzca un nombre representativo para el producto. Minimo 4 caracteres.", value = "Se necesita un nombre para el producto de minimo 4 caracteres. Solo aceptamos letras o espacios. (no cuentan como caracter los espacios)"),
                            @ExampleObject(name = "Descripcion inválido", description = "Requisitos: minimo 20 caracteres.", value = "Se necesita una descripcion para el producto de minimo 20 caracteres. (no cuentan como caracter los espacios)"),
                            @ExampleObject(name = "Imagen inválida", description = "Las extensiones de archivos validos que aceptamos son: jpg, png, gif, jpeg y svg.", value = "Solo aceptamos archivos con extensiones jpg, png, gif, jpeg y svg"),
                            @ExampleObject(name = "Imagen requerida", description = "Es requerimiento adjuntar una imagen representativa.", value = "Se necesita una imagen representativa para el producto."),
                            @ExampleObject(name = "Politicas de muestra", description = "No puedes hacer uso de politicas de muestra, IDs menores a 87.", value = "No puedes hacer uso de las politicas de muestra.")})),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado.",
                    content = @Content(schema = @Schema(implementation = String.class), examples = {
                            @ExampleObject(name = "Producto no encontrado", description = "No se encontro el producto por ID en nuestra base de datos.", value = "No se encontró el id en la base de datos")
                    }))
    })
    @OpenApiConfig.WrongResponsesJWT
    @SecurityRequirement(name = "BearerToken",scopes = {"ADMIN"})
    @PutMapping(value = "/admin/modificar", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> modificarProd(@ModelAttribute ProductDTO productDTO) throws IOException {
        if (productDTO.getId()==null||productDTO.getId()<27L) return new ResponseEntity<>("Introduzca un numero mayor a 26.",HttpStatus.BAD_REQUEST);
        if (productDTO.getTitle()==null||!productDTO.getTitle().matches("^[a-zA-Z]{4,}$")) return new ResponseEntity<>("Se necesita un nombre para el producto de minimo 4 caracteres. Solo aceptamos letras o espacios. (no cuentan como caracter los espacios)",HttpStatus.BAD_REQUEST);
        if (productDTO.getDescription()==null||!productDTO.getDescription().matches("^\\S{19,}\\S$")) return new ResponseEntity<>("Se necesita una descripcion para el producto de minimo 20 caracteres. (no cuentan como caracter los espacios)",HttpStatus.BAD_REQUEST);
        if (productDTO.getThumbnail()==null||productDTO.getThumbnail().getSize()==0) return new ResponseEntity<>("Se necesita una imagen representativa para el producto.",HttpStatus.BAD_REQUEST);
        if (productDTO.getImages()==null|| productDTO.getImages().isEmpty()) return new ResponseEntity<>("se necesitan imagenes representativas para la galeria del producto.",HttpStatus.BAD_REQUEST);
        for (MultipartFile file : productDTO.getImages()){
            if (file==null||file.getSize()==0) return new ResponseEntity<>("Se necesitan imagenes representativas para la galeria del producto.",HttpStatus.BAD_REQUEST);
        }
        if (productDTO.getPoliticIds()!=null){
            for (Long id : productDTO.getPoliticIds()){
                if (id<87L) return new ResponseEntity<>("No puedes hacer uso de las politicas de muestra.",HttpStatus.BAD_REQUEST);
            }
        }
        return productsService.modificarProd(productDTO);
    }

    @Operation(summary = "Utilice este endpoint para modificar algunos de los campos de productos", description = """
            Tenga en cuenta que necesita tener un usuario con rol ADMIN. NO podra modificar los productos de muestra, en este caso los IDs menores a 27.
            
            Es necesario que los campos a modificar contengan datos válidos. Para modificar productos deberá crear nuevos productos que tendran vigencia una hasta las 00:00 AM (GMT-3).""", responses = {
            @ApiResponse(responseCode = "200", description = "Peticion correcta.",
                    content = @Content(schema = @Schema(implementation = Characteristic.class))),
            @ApiResponse(responseCode = "400", description = "No se pudo completar la modificación de característica de producto.",
                    content = @Content(schema = @Schema(implementation = String.class), examples = {
                            @ExampleObject(name = "ID inválido", description = "Solo se permiten ID mayores a 26", value = "Introduzca un numero mayor a 26."),
                            @ExampleObject(name = "Producto existente", description = "No se permiten crear dos productos con el mismo nombre.", value = "El nombre del producto ya esta en uso. Por favor elige un nombre de producto diferente."),
                            @ExampleObject(name = "Nombre inválido", description = "Introduzca un nombre representativo para el producto. Minimo 4 caracteres.", value = "Se necesita un nombre para el producto de minimo 4 caracteres. Solo aceptamos letras o espacios. (no cuentan como caracter los espacios)"),
                            @ExampleObject(name = "Descripcion inválido", description = "Requisitos: minimo 20 caracteres.", value = "Se necesita una descripcion para el producto de minimo 20 caracteres. (no cuentan como caracter los espacios)"),
                            @ExampleObject(name = "Imagen inválida", description = "Las extensiones de archivos validos que aceptamos son: jpg, png, gif, jpeg y svg.", value = "Solo aceptamos archivos con extensiones jpg, png, gif, jpeg y svg"),
                            @ExampleObject(name = "Imagen requerida", description = "Es requerimiento adjuntar una imagen representativa.", value = "Se necesita una imagen representativa para el producto."),
                            @ExampleObject(name = "Politicas de muestra", description = "No puedes hacer uso de politicas de muestra, IDs menores a 87.", value = "No puedes hacer uso de las politicas de muestra.")})),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado.",
                    content = @Content(schema = @Schema(implementation = String.class), examples = {
                            @ExampleObject(name = "Producto no encontrado", description = "No se encontro el producto por ID en nuestra base de datos.", value = "No se encontró el id en la base de datos")
                    }))
    })
    @OpenApiConfig.WrongResponsesJWT
    @SecurityRequirement(name = "BearerToken",scopes = {"ADMIN"})
    @PatchMapping(value = "/admin/parcialMod", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> parcialMod(@ModelAttribute ProductDTO productDTO) throws IOException, InterruptedException {
        if (productDTO.getId()!=null&&productDTO.getId()<27L) return new ResponseEntity<>("Introduzca un numero mayor a 26.",HttpStatus.BAD_REQUEST);
        if (productDTO.getTitle()!=null&&!productDTO.getTitle().matches("^[a-zA-Z]{4,}$")) return new ResponseEntity<>("Se necesita un nombre para el producto de minimo 4 caracteres. Solo aceptamos letras o espacios. (no cuentan como caracter los espacios)",HttpStatus.BAD_REQUEST);
        if (productDTO.getDescription()!=null&&!productDTO.getDescription().matches("^\\S{19,}\\S$")) return new ResponseEntity<>("Se necesita una descripcion para el producto de minimo 20 caracteres. (no cuentan como caracter los espacios)",HttpStatus.BAD_REQUEST);
        if (productDTO.getThumbnail()!=null&&productDTO.getThumbnail().getSize()==0) return new ResponseEntity<>("Se necesita una imagen representativa para el producto.",HttpStatus.BAD_REQUEST);
        if (productDTO.getImages()!=null&&productDTO.getImages().isEmpty()) return new ResponseEntity<>("se necesitan imagenes representativas para la galeria del producto.",HttpStatus.BAD_REQUEST);
        for (MultipartFile file : productDTO.getImages()){
            if (file!=null&&file.getSize()==0) return new ResponseEntity<>("Se necesitan imagenes representativas para la galeria del producto.",HttpStatus.BAD_REQUEST);
        }
        if (productDTO.getPoliticIds()!=null){
            for (Long id : productDTO.getPoliticIds()){
                if (id<87L) return new ResponseEntity<>("No puedes hacer uso de las politicas de muestra.",HttpStatus.BAD_REQUEST);
            }
        }
        return productsService.parcialMod(productDTO);
    }

    /*@Operation(summary = "", description = "", responses = {
            @ApiResponse(responseCode = "200", description = "",
                    content = @Content(schema = @Schema()))
    })
    @SecurityRequirement(name = "BearerToken",scopes = {"USER","ADMIN"})
    @PatchMapping("/auth/rating")
    @Transactional
    public ResponseEntity<?> valoracion(@RequestParam(value = "idUser") Long idUser,@RequestParam(value = "rating") int rating, @RequestParam(value = "idProduct") Long idProduct){
        return productsService.valoracion(idUser,rating,idProduct);
    }*/

    @Operation(summary = "Utilice este endpoint para borrar el producto de la base de datos", description = """
            Tenga en cuenta que necesita tener un usuario con rol ADMIN. NO podra borrar los productos de muestra, en este caso los IDs menores a 27.
            
            Para borrar productos deberá crear nuevos productos que tendran vigencia una hasta las 00:00 AM (GMT-3).""", responses = {
            @ApiResponse(responseCode = "200", description = "Peticion correcta"),
            @ApiResponse(responseCode = "400", description = "El producto no se pudo eliminar correctamente.",
            content = @Content(schema = @Schema(implementation = String.class), examples = {
                    @ExampleObject(name = "ID inválido", description = "Solo se permiten ID mayores a 26", value = "Introduzca un numero mayor a 26."),})),
            @ApiResponse(responseCode = "404", description = "El ID no esta relacionado a un registro de nuestra base de datos.",
                    content = @Content(schema = @Schema(implementation = String.class),examples = {
                            @ExampleObject(name = "Producto no encontrado", description = "El ID no se encontro en nuestra base de datos", value = "No se encontró la caracteristica en la base de datos.")}))
    })
    @OpenApiConfig.WrongResponsesJWT
    @SecurityRequirement(name = "BearerToken",scopes = {"ADMIN"})
    @DeleteMapping("/admin/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> eliminarProd(@PathVariable Long id){
        if (id<27L) return new ResponseEntity<>("Introduzca un numero mayor a 26.",HttpStatus.BAD_REQUEST);
        return productsService.eliminarProd(id);
    }
}
