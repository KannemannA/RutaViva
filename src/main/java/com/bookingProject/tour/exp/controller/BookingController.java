package com.bookingProject.tour.exp.controller;

import com.bookingProject.tour.exp.entity.Booking;
import com.bookingProject.tour.exp.entity.Product;
import com.bookingProject.tour.exp.entity.dto.booking.BookingDTO;
import com.bookingProject.tour.exp.entity.dto.booking.SaveBooking;
import com.bookingProject.tour.exp.repository.IBookingRepository;
import com.bookingProject.tour.exp.service.IBookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/booking")
@Tag(name = "6. RESERVAS - Endpoints", description = "Contiene la lógica necesaria para la reservas de productos")
public class BookingController {
    @Autowired
    private IBookingService bookingService;

    @Operation(summary = "Utilice este endpoint para realizar una reserva", description = """
            Tenga en cuenta que los nuevos productos tendran una vigencia hasta las 00:00 AM (GMT-3).
            
            Para concretar la operacion necesita enviar el token de un usuario.""", responses = {
            @ApiResponse(responseCode = "201", description = "Peticion correcta",
                    content = @Content(schema = @Schema(implementation = Booking.class))),
            @ApiResponse(responseCode = "400", description = "Reservar fechas no disponibles",
                    content = @Content(schema = @Schema(implementation = String.class), examples = {
                            @ExampleObject(name = "Fechas no disponibles", description = "No se pueden reservar fechas que ya se han reservado.", value = "Las fechas que intenta reservar no estan disponibles")
                    })),
            @ApiResponse(responseCode = "404", description = "Productos y/o usuarios no encontrados.",
                    content = @Content(schema = @Schema(implementation = String.class), examples = {
                            @ExampleObject(name = "IDs no encontrados", description = "No se encontraron los IDs suministrados.", value = "No se encontraron los datos en la bd")
                    }))
    })
    @SecurityRequirement(name = "BearerToken",scopes = {"ADMIN","USER"})
    @PostMapping("/auth/guardar")
    @Transactional
    public ResponseEntity<?> crearReservas(@RequestBody SaveBooking saveBooking){
        return bookingService.crearReserva(saveBooking);
    }

    @Operation(summary = "Utilice este endpoint para modificar una reserva", description = """
            Tenga en cuenta que los nuevos productos tendran una vigencia hasta las 00:00 AM (GMT-3).
            
            Para concretar la operacion necesita enviar el token de un usuario.""", responses = {
            @ApiResponse(responseCode = "200", description = "Peticion correcta",
                    content = @Content(schema = @Schema(implementation = Booking.class))),
            @ApiResponse(responseCode = "400", description = "Reservar fechas no disponibles",
                    content = @Content(schema = @Schema(implementation = String.class), examples = {
                            @ExampleObject(name = "Fechas no disponibles", description = "No se pueden reservar fechas que ya se han reservado.", value = "Las fechas que intenta reservar no estan disponibles")
                    })),
            @ApiResponse(responseCode = "404", description = "Productos y/o usuarios y/o reservas no encontrados.",
                    content = @Content(schema = @Schema(implementation = String.class), examples = {
                            @ExampleObject(name = "IDs no encontrados", description = "No se encontraron los IDs suministrados.", value = "No se encontraron los datos en la bd"),
                            @ExampleObject(name = "Reserva no encontrada", description = "El ID que representa a la reserva no se encuentra en nuestra bd", value = "Reserva no encontrada")
                    }))
    })
    @SecurityRequirement(name = "BearerToken",scopes = {"ADMIN","USER"})
    @PutMapping("/auth/modificar")
    @Transactional
    public ResponseEntity<?> modificarReservas(@RequestBody BookingDTO bookingDTO){
        return bookingService.modificarReserva(bookingDTO);
    }

    @Operation(summary = "Utilice este endpoint para eliminar una reserva", description = """
            Tenga en cuenta que los nuevos productos tendran una vigencia hasta las 00:00 AM (GMT-3).
            
            Para concretar la operacion necesita enviar el token de un usuario.""", responses = {
            @ApiResponse(responseCode = "200", description = "Peticion correcta"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada.",
                    content = @Content(schema = @Schema(implementation = String.class), examples = {
                            @ExampleObject(name = "Reserva no encontrada", description = "El ID que representa a la reserva no se encuentra en nuestra bd", value = "Reserva no encontrada")
                    }))
    })
    @SecurityRequirement(name = "BearerToken",scopes = {"ADMIN","USER"})
    @DeleteMapping("/auth/eliminar/{id}")
    @Transactional
    public ResponseEntity<?> cancelarReserva(@PathVariable Long id){
        return bookingService.eliminarReserva(id);
    }
}
