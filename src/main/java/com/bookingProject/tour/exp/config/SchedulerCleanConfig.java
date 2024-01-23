package com.bookingProject.tour.exp.config;

import com.bookingProject.tour.exp.entity.*;
import com.bookingProject.tour.exp.repository.IBookingRepository;
import com.bookingProject.tour.exp.repository.ICategoryRepository;
import com.bookingProject.tour.exp.repository.ICharacteristicRepository;
import com.bookingProject.tour.exp.repository.IProductRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class SchedulerCleanConfig {
    private final IProductRepository productRepository;
    private final IBookingRepository bookingRepository;
    private final IProductRepository politicRepository;
    private final ICharacteristicRepository characteristicRepository;
    private final ICategoryRepository categoryRepository;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void start() {
        final Runnable cleaner = () -> {
            List<Category> caResult= categoryRepository.findByIdGreaterThan(5L);
            List<Characteristic> chResult= characteristicRepository.findByIdGreaterThan(6L);
            List<Product> pResult= productRepository.findByIdGreaterThan(26L);
            for (Category category: caResult){
                categoryRepository.deleteById(category.getId());
            }
            for (Characteristic characteristic : chResult){
                characteristicRepository.deleteById(characteristic.getId());
            }
            for (Product product : pResult){
                if (!product.getPolitics().isEmpty()){
                    for (Politic politic : product.getPolitics()){
                        politicRepository.deleteById(politic.getId());
                    }
                }
                if (!product.getBookings().isEmpty()){
                    List<Booking> bResult= bookingRepository.findByIdProduct(product.getId());
                    for (Booking booking : bResult){
                        bookingRepository.deleteById(booking.getId());
                    }
                }
                productRepository.deleteById(product.getId());
            }
        };
        long initialDelay = getTimeUntilMidnight();
        scheduler.scheduleAtFixedRate(cleaner, initialDelay, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
    }
    private long getTimeUntilMidnight() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires"));
        ZonedDateTime nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay(now.getZone());
        return Duration.between(now, nextMidnight).getSeconds();
    }
    @PostConstruct
    public void init(){
        start();
    }
}
