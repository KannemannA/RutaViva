package com.bookingProject.tour.exp.controller;

import com.bookingProject.tour.exp.entity.dto.booking.BookingDTO;
import com.bookingProject.tour.exp.entity.dto.booking.SaveBooking;
import com.bookingProject.tour.exp.service.IBookingService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/booking")
public class BookingController {
    @Autowired
    private IBookingService bookingService;

    @SecurityRequirement(name = "BearerToken",scopes = {"ADMIN","USER"})
    @PostMapping("/auth/guardar")
    @Transactional
    public ResponseEntity<?> crearReservas(@RequestBody SaveBooking saveBooking){
        return bookingService.crearReserva(saveBooking);
    }

    @SecurityRequirement(name = "BearerToken",scopes = {"ADMIN","USER"})
    @PutMapping("/auth/modificar")
    @Transactional
    public ResponseEntity<?> modificarReservas(@RequestBody BookingDTO bookingDTO){
        return bookingService.modificarReserva(bookingDTO);
    }

    @SecurityRequirement(name = "BearerToken",scopes = {"ADMIN","USER"})
    @DeleteMapping("/auth/eliminar")
    @Transactional
    public ResponseEntity<?> cancelarReserva(@PathVariable Long id){
        return bookingService.eliminarReserva(id);
    }
}
