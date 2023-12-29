package com.bookingProject.tour.exp.unitTest.controller;

import com.bookingProject.tour.exp.auth.JwtUtils;
import com.bookingProject.tour.exp.controller.PoliticController;
import com.bookingProject.tour.exp.entity.Politic;
import com.bookingProject.tour.exp.entity.dto.politic.PoliticDTO;
import com.bookingProject.tour.exp.entity.dto.politic.SavePolitic;
import com.bookingProject.tour.exp.service.imp.PoliticService;
import com.bookingProject.tour.exp.service.imp.UserEntityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PoliticController.class)
class PoliticControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private PoliticService service;
    @MockBean
    private JwtUtils jwt;
    @MockBean
    private UserEntityService auth;
    private Politic politic;
    private PoliticDTO politicDTO;
    private SavePolitic savePolitic;
    private ObjectMapper mapper= new ObjectMapper();

    @BeforeEach
    void setUp() {
        politic= Politic.builder()
                .title("").id(1L)
                .description("").build();
        savePolitic= SavePolitic.builder().title("wepa")
                .description("wertyuiopñlkjmnhbgvf").build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @WithMockUser
    void guardarPoliticSuccessful() throws Exception {
        String body=mapper.writeValueAsString(savePolitic);
        doReturn(new ResponseEntity<>(politic, HttpStatus.CREATED)).when(service).crearPolitica(any(SavePolitic.class));
        mvc.perform(post("/api/politic/admin/guardar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)).andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void guardarPoliticTitleUnsuccessful() throws Exception {
        doReturn(new ResponseEntity<>(politic, HttpStatus.CREATED)).when(service).crearPolitica(any(SavePolitic.class));
        mvc.perform(get("/api/user/admin/detalle/1")).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void guardarPoliticDescriptionUnsuccessful() throws Exception {
        doReturn(new ResponseEntity<>(politic, HttpStatus.CREATED)).when(service).crearPolitica(any(SavePolitic.class));
        mvc.perform(get("/api/user/admin/detalle/1")).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void modificarIdUnsuccessful() {
    }

    @Test
    void modificarSuccessful() {
    }

    @Test
    void parcialModSuccessful() {
    }

    @Test
    @WithMockUser
    void eliminarPoliticSuccessful() throws Exception {
        doReturn(new ResponseEntity<>(HttpStatus.OK)).when(service).eliminarPolitica(anyLong());
        mvc.perform(delete("/api/politic/admin/eliminar/88")).andDo(print())
                .andExpect(status().isOk());
    }
}