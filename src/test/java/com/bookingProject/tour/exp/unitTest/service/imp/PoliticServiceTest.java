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
import org.springframework.http.HttpStatus;
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
    private final ObjectMapper mapper=new ObjectMapper();
    private Politic politic;
    private PoliticDTO politicDTO;
    private ResponseEntity<?> result;

    @BeforeEach
    void setUp() {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        autoCloseable= MockitoAnnotations.openMocks(this);
        politic= builder();
        politicDTO= mapper.convertValue(politic, PoliticDTO.class);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    public Politic builder(){
        return Politic.builder()
                .id(1L)
                .title("someTitle")
                .description("someDescription").build();
    }

    @Test
    void save_politic_successful_test() {
        SavePolitic savePolitic= mapper.convertValue(politic, SavePolitic.class);
        when(repository.save(any(Politic.class))).thenReturn(politic);
        result= service.crearPolitica(savePolitic);
        Politic resultPolitic= mapper.convertValue(result.getBody(),Politic.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resultPolitic.getId()).isEqualTo(politic.getId());
        assertThat(resultPolitic.getTitle()).isEqualTo(politic.getTitle());
        assertThat(resultPolitic.getDescription()).isEqualTo(politic.getDescription());
        verify(repository,times(1)).save(any(Politic.class));
    }

    @Test
    void get_all_politic_successful_test() {
        when(repository.findAll()).thenReturn(new ArrayList<>(Collections.singletonList(politic)));
        assertThat(service.traerPolitica().isEmpty()).isFalse();
        verify(repository,times(1)).findAll();
    }

    @Test
    void get_details_politic_successful_test() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(politic));
        result= service.traerId(1L);
        Politic resultPolitic= mapper.convertValue(result.getBody(),Politic.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resultPolitic.getId()).isEqualTo(politic.getId());
        assertThat(resultPolitic.getTitle()).isEqualTo(politic.getTitle());
        assertThat(resultPolitic.getDescription()).isEqualTo(politic.getDescription());
        verify(repository,times(1)).findById(anyLong());
    }

    @Test
    void get_details_politic_unsuccessful_with_wrong_id_test() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());
        result= service.traerId(1L);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(result.getBody()).isEqualTo("No se encontró la politica en la base de datos.");
        verify(repository,times(1)).findById(anyLong());
    }

    @Test
    void modify_politic_successful_test() {
        when(repository.findById(politicDTO.getId())).thenReturn(Optional.of(politic));
        result=service.modificarPolitica(politicDTO);
        Politic resultPolitic= mapper.convertValue(result.getBody(),Politic.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resultPolitic.getId()).isEqualTo(politic.getId());
        assertThat(resultPolitic.getTitle()).isEqualTo(politic.getTitle());
        assertThat(resultPolitic.getDescription()).isEqualTo(politic.getDescription());
        verify(repository,times(1)).save(any(Politic.class));
        verify(repository,times(1)).findById(anyLong());
    }

    @Test
    void modify_politic_unsuccessful_with_wrong_id_test() {
        when(repository.findById(politicDTO.getId())).thenReturn(Optional.empty());
        result=service.modificarPolitica(politicDTO);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(result.getBody()).isEqualTo("No se encontró la politica en la base de datos.");
        verify(repository,times(1)).findById(anyLong());
        verify(repository,times(0)).save(any(Politic.class));
    }

    @Test
    void partial_modify_politic_successful_test() {
        when(repository.findById(politicDTO.getId())).thenReturn(Optional.of(politic));
        result=service.parcialMod(politicDTO);
        Politic resultPolitic=mapper.convertValue(result.getBody(),Politic.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resultPolitic.getId()).isEqualTo(politic.getId());
        assertThat(resultPolitic.getTitle()).isEqualTo(politic.getTitle());
        assertThat(resultPolitic.getDescription()).isEqualTo(politic.getDescription());
        verify(repository,times(1)).findById(anyLong());
    }

    @Test
    void partial_modify_politic_unsuccessful_with_wrong_id_test() {
        when(repository.findById(politicDTO.getId())).thenReturn(Optional.empty());
        result=service.parcialMod(politicDTO);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(result.getBody()).isEqualTo("No se encontró la politica en la base de datos.");
        verify(repository,times(1)).findById(anyLong());
        verify(repository,times(0)).save(any(Politic.class));
    }

    @Test
    void delete_politic_successful_test() {
        mock(IPoliticRepository.class, Mockito.CALLS_REAL_METHODS);
        when(repository.findById(anyLong())).thenReturn(Optional.of(politic));
        doAnswer(Answers.CALLS_REAL_METHODS).when(repository).deleteById(anyLong());
        result= service.eliminarPolitica(1L);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNull();
        verify(repository,times(1)).findById(anyLong());
        verify(repository,times(1)).deleteById(anyLong());
    }

    @Test
    void delete_politic_unsuccessful_with_wrong_id_test() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());
        result= service.eliminarPolitica(1L);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(result.getBody()).isEqualTo("No se encontró la politica en la base de datos.");
        verify(repository,times(1)).findById(anyLong());
        verify(repository,times(0)).deleteById(anyLong());
    }
}