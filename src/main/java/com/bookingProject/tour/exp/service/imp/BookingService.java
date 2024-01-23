package com.bookingProject.tour.exp.service.imp;

import com.bookingProject.tour.exp.entity.Booking;
import com.bookingProject.tour.exp.entity.Product;
import com.bookingProject.tour.exp.entity.UserEntity;
import com.bookingProject.tour.exp.entity.dto.booking.BookingDTO;
import com.bookingProject.tour.exp.entity.dto.booking.SaveBooking;
import com.bookingProject.tour.exp.repository.IBookingRepository;
import com.bookingProject.tour.exp.repository.IProductRepository;
import com.bookingProject.tour.exp.service.IBookingService;
import com.bookingProject.tour.exp.service.IEmailService;
import com.bookingProject.tour.exp.service.IProductService;
import com.bookingProject.tour.exp.service.IUserEntityService;
import com.bookingProject.tour.exp.template.EmailTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingService implements IBookingService {
    @Autowired
    private IBookingRepository bookingRepository;
    @Autowired
    private IProductService productService;
    @Autowired
    private IUserEntityService userEntityService;
    @Autowired
    private IProductRepository productRepository;
    @Autowired
    private IEmailService emailService;
    private final List<String> contactoProveedor= List.of("+5491132123456", "+5491132234567", "+5491132345678", "+5491132456789", "+5491132567890", "+5491132678901", "+5491132789012", "+5491132890123", "+5491132901234", "+5491132101234", "+5491132111234", "+5491132121234", "+5491132131234", "+5491132141234", "+5491132151234", "+5491132161234", "+5491132171234", "+5491132181234", "+5491132191234", "+5491132201234", "+5491132211234", "+5491132221234", "+5491132231234", "+5491132241234", "+5491132251234", "+5491132261234", "+5491132271234", "+5491132281234", "+5491132291234");
    private final Set<LocalDate> reservaTotal = new TreeSet<>((fecha1, fecha2) -> {
        int yearComparison = Integer.compare(fecha1.getYear(), fecha2.getYear());
        if (yearComparison != 0) {
            return yearComparison;
        }
        int monthComparison = Integer.compare(fecha1.getMonthValue(), fecha2.getMonthValue());
        if (monthComparison != 0) {
            return monthComparison;
        }
        return Integer.compare(fecha1.getDayOfMonth(), fecha2.getDayOfMonth());
    });

    @Override
    public ResponseEntity<?> crearReserva(SaveBooking saveBooking) {
        ResponseEntity<?> findP= productService.buscarProd(saveBooking.getProductId());
        ResponseEntity<?> findU= userEntityService.traerId(saveBooking.getUserId());
        if (findU.getStatusCode().is2xxSuccessful()&& findP.getStatusCode().is2xxSuccessful()){
            saveBooking.setCheckOut(saveBooking.getCheckOut().plusDays(1));
            List<LocalDate> reserva= saveBooking.getCheckIn().datesUntil(saveBooking.getCheckOut()).collect(Collectors.toList());
            UserEntity usuario= (UserEntity) findU.getBody();
            Product producto= (Product) findP.getBody();
            LocalDateTime now= LocalDateTime.now();
            DateTimeFormatter formatter= DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            Booking book= Booking.builder()
                    .user(usuario)
                    .product(producto)
                    .reservedDate(reserva).build();
            assert producto != null;
            if (producto.getBookings()!=null){
                reservaTotal.addAll(producto.getBookings());
            }
            for(LocalDate fecha : reserva){
                if(!reservaTotal.add(fecha)){
                    return new ResponseEntity<>("Las fechas que intenta reservar no estan disponibles",HttpStatus.BAD_REQUEST);
                };
            }
            producto.setBookings(reservaTotal);
            productRepository.save(producto);
            bookingRepository.save(book);
            emailService.sendEmail((Objects.requireNonNull(usuario.getEmail())),"Confirmación de reserva - Ruta Viva", new EmailTemplate().bookingTemplate(usuario.getName(),now.format(formatter),producto.getTitle(),contactoProveedor.get(new Random().nextInt(contactoProveedor.size()))));
            return new ResponseEntity<>(book, HttpStatus.CREATED);
        }
        return new ResponseEntity<>("No se encontraron los datos en la bd",HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> modificarReserva(BookingDTO bookingDTO) {
        Optional<Booking> find= bookingRepository.findById(bookingDTO.getId());
        if (find.isPresent()){
            Booking book= find.get();
            ResponseEntity<?> findP= productService.buscarProd(bookingDTO.getProductId());
            ResponseEntity<?> findU= userEntityService.traerId(bookingDTO.getUserId());
            if (findU.getStatusCode().is2xxSuccessful()&& findP.getStatusCode().is2xxSuccessful()){
                bookingDTO.setCheckOut(bookingDTO.getCheckOut().plusDays(1));
                List<LocalDate> reserva= bookingDTO.getCheckIn().datesUntil(bookingDTO.getCheckOut()).collect(Collectors.toList());
                UserEntity usuario= (UserEntity) findU.getBody();
                Product producto= (Product) findP.getBody();
                assert producto != null;
                Iterator<LocalDate> iterator = producto.getBookings().iterator();
                while (iterator.hasNext()) {
                    LocalDate fecha = iterator.next();
                    if (book.getReservedDate().contains(fecha)) {
                        iterator.remove();
                    }
                }

                if (producto.getBookings()!=null){
                    reservaTotal.addAll(producto.getBookings());
                }
                for(LocalDate fecha : reserva){
                    if(!reservaTotal.add(fecha)){
                        return new ResponseEntity<>("Las fechas que intenta reservar no estan disponibles",HttpStatus.BAD_REQUEST);
                    };
                }
                producto.setBookings(reservaTotal);
                productRepository.save(producto);
                book.setUser(usuario);
                book.setProduct(producto);
                book.setReservedDate(reserva);
                bookingRepository.save(book);
                return new ResponseEntity<>(book, HttpStatus.OK);
            }
            return new ResponseEntity<>("No se encontraron los datos en la bd",HttpStatus.NOT_FOUND);

        }
        return new ResponseEntity<>("Reserva no encontrada",HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> eliminarReserva(Long id) {
        Optional<Booking> find= bookingRepository.findById(id);
        if (find.isPresent()){
            Booking book= find.get();
            Product prod= book.getProduct();
            for (LocalDate fecha : book.getReservedDate()){
                prod.getBookings().remove(fecha);
            }
            productRepository.save(prod);
            bookingRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>("Reserva no encontrada",HttpStatus.NOT_FOUND);
    }
}
