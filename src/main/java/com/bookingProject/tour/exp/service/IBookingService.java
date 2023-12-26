package com.bookingProject.tour.exp.service;

import com.bookingProject.tour.exp.entity.dto.booking.BookingDTO;
import com.bookingProject.tour.exp.entity.dto.booking.SaveBooking;
import org.springframework.http.ResponseEntity;

public interface IBookingService {
    public ResponseEntity<?> crearReserva(SaveBooking saveBooking);
    public ResponseEntity<?> modificarReserva(BookingDTO bookingDTO);
    public ResponseEntity<?> eliminarReserva(Long id);
}
