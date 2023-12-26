package com.bookingProject.tour.exp.unitTest.service.imp;

import com.bookingProject.tour.exp.entity.Politic;
import com.bookingProject.tour.exp.entity.dto.politic.PoliticDTO;
import com.bookingProject.tour.exp.entity.dto.politic.SavePolitic;
import com.bookingProject.tour.exp.repository.IPoliticRepository;
import com.bookingProject.tour.exp.service.imp.PoliticService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PoliticServiceTest {
    @Mock
    private IPoliticRepository repository;
    @InjectMocks
    private PoliticService service;
    AutoCloseable autoCloseable;
    private ObjectMapper mapper;
    private Politic politic;
    private PoliticDTO politicDTO;
    private ResponseEntity<?> result;

    @BeforeEach
    void setUp() {
        mapper= new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        autoCloseable= MockitoAnnotations.openMocks(this);
        politicDTO= crearPoliticDto();
        politic= mapper.convertValue(politicDTO, Politic.class);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    public PoliticDTO crearPoliticDto(){
        return PoliticDTO.builder()
                .id(1L)
                .title("someTitle")
                .description("someDescription").build();
    }

    @Test
    void crearPoliticaTest() {
        SavePolitic savePolitic= mapper.convertValue(crearPoliticDto(), SavePolitic.class);
        when(repository.save(any(Politic.class))).thenReturn(politic);
        result= service.crearPolitica(savePolitic);
        assertThat(result.getStatusCode().value()).isEqualTo(201);
        assertThat(result.getBody()).isInstanceOf(Politic.class);
    }

    @Test
    void traerPoliticaTest() {
        when(repository.findAll()).thenReturn(new ArrayList<>(Collections.singletonList(politic)));
        assertThat(service.traerPolitica().isEmpty()).isFalse();
    }

    @Test
    void traerIdTestSuccessful() {
        when(repository.findById(1L)).thenReturn(Optional.of(politic));
        result= service.traerId(1L);
        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).isInstanceOf(Politic.class);
    }

    @Test
    void traerIdTestUnsuccessful() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        result= service.traerId(1L);
        assertThat(result.getStatusCode().value()).isEqualTo(404);
        assertThat(result.getBody()).isInstanceOf(String.class);
    }

    @Test
    void modificarPoliticaTestSuccessful() {
        when(repository.findById(politicDTO.getId())).thenReturn(Optional.of(politic));
        when(repository.save(any(Politic.class))).thenReturn(politic);
        result=service.modificarPolitica(politicDTO);
        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).isInstanceOf(Politic.class);
    }

    @Test
    void modificarPoliticaTestUnsuccessful() {
        when(repository.findById(politicDTO.getId())).thenReturn(Optional.empty());
        result=service.modificarPolitica(politicDTO);
        assertThat(result.getStatusCode().value()).isEqualTo(404);
        assertThat(result.getBody()).isInstanceOf(String.class);
    }

    @Test
    void parcialModTestSuccessful() {
        when(repository.findById(politicDTO.getId())).thenReturn(Optional.of(politic));
        when(repository.save(any(Politic.class))).thenReturn(politic);
        result=service.parcialMod(politicDTO);
        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).isInstanceOf(Politic.class);
    }

    @Test
    void parcialModTestUnsuccessful() {
        when(repository.findById(politicDTO.getId())).thenReturn(Optional.empty());
        result=service.parcialMod(politicDTO);
        assertThat(result.getStatusCode().value()).isEqualTo(404);
        assertThat(result.getBody()).isInstanceOf(String.class);
    }

    @Test
    void eliminarPoliticaTestSuccessful() {
        mock(IPoliticRepository.class, Mockito.CALLS_REAL_METHODS);
        when(repository.findById(1L)).thenReturn(Optional.of(politic));
        doAnswer(Answers.CALLS_REAL_METHODS).when(repository).deleteById(any());
        result= service.eliminarPolitica(1L);
        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(null);
    }

    @Test
    void eliminarPoliticaTestUnsuccessful() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        result= service.eliminarPolitica(1L);
        assertThat(result.getStatusCode().value()).isEqualTo(404);
        assertThat(result.getBody()).isInstanceOf(String.class);
    }
}