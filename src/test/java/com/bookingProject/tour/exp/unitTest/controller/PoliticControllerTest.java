package com.bookingProject.tour.exp.unitTest.controller;

import com.bookingProject.tour.exp.auth.JwtUtils;
import com.bookingProject.tour.exp.config.SecurityConfig;
import com.bookingProject.tour.exp.controller.PoliticController;
import com.bookingProject.tour.exp.entity.Politic;
import com.bookingProject.tour.exp.entity.dto.politic.PoliticDTO;
import com.bookingProject.tour.exp.entity.dto.politic.SavePolitic;
import com.bookingProject.tour.exp.service.imp.PoliticService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PoliticController.class)
@Import(SecurityConfig.class)
class PoliticControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private PoliticService service;
    @MockBean
    private JwtUtils jwt;
    @MockBean
    private AuthenticationProvider provider;
    private Politic politic;
    private PoliticDTO politicDTO;
    private SavePolitic savePolitic;
    private final ObjectMapper mapper= new ObjectMapper();

    @BeforeEach
    void setUp() {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        politic= builder();
        savePolitic= mapper.convertValue(politic, SavePolitic.class);
        politicDTO= mapper.convertValue(politic,PoliticDTO.class);
    }

    public Politic builder(){
        return Politic.builder()
                .title("solo").id(87L)
                .description("solosoyunhombresolitario").build();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void save_politic_successful_test() throws Exception {
        String body=mapper.writeValueAsString(savePolitic);
        doReturn(new ResponseEntity<>(politic, HttpStatus.CREATED)).when(service).crearPolitica(any(SavePolitic.class));
        MvcResult result=mvc.perform(post("/api/politic/admin/guardar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)).andDo(print())
                .andExpect(status().isCreated())
                .andReturn();
        Politic bodyResult= mapper.readValue(result.getResponse().getContentAsString(),Politic.class);
        assertThat(bodyResult.getId()).isEqualTo(politic.getId());
        assertThat(bodyResult.getTitle()).isEqualTo(politic.getTitle());
        assertThat(bodyResult.getDescription()).isEqualTo(politic.getDescription());
        verify(service,times(1)).crearPolitica(any(SavePolitic.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void save_politic_unsuccessful_with_wrong_title_test() throws Exception {
        savePolitic.setTitle("ojo");
        String body=mapper.writeValueAsString(savePolitic);
        doReturn(new ResponseEntity<>(politic, HttpStatus.CREATED)).when(service).crearPolitica(any(SavePolitic.class));
        MvcResult result=mvc.perform(post("/api/politic/admin/guardar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)).andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
        assertThat(result.getResponse().getContentAsString()).isEqualTo("Se necesita un nombre para la politica de minimo 4 caracteres. Solo aceptamos letras o espacios. (no cuentan como caracter los espacios)");
        verify(service,times(0)).crearPolitica(any(SavePolitic.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void save_politic_unsuccessful_with_wrong_description_test() throws Exception {
        savePolitic.setDescription("manantialdeoro");
        String body=mapper.writeValueAsString(savePolitic);
        doReturn(new ResponseEntity<>(politic, HttpStatus.CREATED)).when(service).crearPolitica(any(SavePolitic.class));
        MvcResult result=mvc.perform(post("/api/politic/admin/guardar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)).andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
        assertThat(result.getResponse().getContentAsString()).isEqualTo("Se necesita una descripcion para la politica de minimo 20 caracteres. (no cuentan como caracter los espacios)");
        verify(service,times(0)).crearPolitica(any(SavePolitic.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void modify_politic_unsuccessful_with_wrong_id_test() throws Exception {
        politicDTO.setId(1L);
        String body=mapper.writeValueAsString(politicDTO);
        doReturn(new ResponseEntity<>(politic, HttpStatus.OK)).when(service).modificarPolitica(any(PoliticDTO.class));
        MvcResult result=mvc.perform(put("/api/politic/admin/modificar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)).andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
        assertThat(result.getResponse().getContentAsString()).isEqualTo("Introduzca un numero mayor a 86.");
        verify(service,times(0)).modificarPolitica(any(PoliticDTO.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void modify_politic_successful_test() throws Exception {
        String body=mapper.writeValueAsString(politicDTO);
        doReturn(new ResponseEntity<>(politic,HttpStatus.OK)).when(service).modificarPolitica(any(PoliticDTO.class));
        MvcResult result=mvc.perform(put("/api/politic/admin/modificar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)).andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        Politic bodyResult= mapper.readValue(result.getResponse().getContentAsString(),Politic.class);
        assertThat(bodyResult.getId()).isEqualTo(politic.getId());
        assertThat(bodyResult.getTitle()).isEqualTo(politic.getTitle());
        assertThat(bodyResult.getDescription()).isEqualTo(politic.getDescription());
        verify(service,times(1)).modificarPolitica(any(PoliticDTO.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void partial_modify_politic_successful_test() throws Exception {
        String body=mapper.writeValueAsString(politicDTO);
        doReturn(new ResponseEntity<>(politic,HttpStatus.OK)).when(service).parcialMod(any(PoliticDTO.class));
        MvcResult result=mvc.perform(patch("/api/politic/admin/parcialMod")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)).andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        Politic bodyResult= mapper.readValue(result.getResponse().getContentAsString(),Politic.class);
        assertThat(bodyResult.getId()).isEqualTo(politic.getId());
        assertThat(bodyResult.getTitle()).isEqualTo(politic.getTitle());
        assertThat(bodyResult.getDescription()).isEqualTo(politic.getDescription());
        verify(service,times(1)).parcialMod(any(PoliticDTO.class));
    }

    @Test
    void get_all_politic_successful_test() throws Exception {
        doReturn(new ArrayList<>(Collections.singletonList(politic))).when(service).traerPolitica();
        MvcResult result=mvc.perform(get("/api/politic/public/traerTodo")).andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        ArrayList bodyResult= mapper.readValue(result.getResponse().getContentAsString(),ArrayList.class);
        assertThat(bodyResult.isEmpty()).isFalse();
        verify(service,times(1)).traerPolitica();
    }

    @Test
    void get_details_politic_successful_test() throws Exception {
        doReturn(new ResponseEntity<>(politic, HttpStatus.OK)).when(service).traerId(anyLong());
        MvcResult result=mvc.perform(get("/api/politic/public/detalle/1")).andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        Politic bodyResult= mapper.readValue(result.getResponse().getContentAsString(),Politic.class);
        assertThat(bodyResult.getId()).isEqualTo(politic.getId());
        assertThat(bodyResult.getTitle()).isEqualTo(politic.getTitle());
        assertThat(bodyResult.getDescription()).isEqualTo(politic.getDescription());
        verify(service,times(1)).traerId(anyLong());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void delete_politic_successful_test() throws Exception {
        doReturn(new ResponseEntity<>(HttpStatus.OK)).when(service).eliminarPolitica(anyLong());
        mvc.perform(delete("/api/politic/admin/eliminar/88")).andDo(print())
                .andExpect(status().isOk());
        verify(service, times(1)).eliminarPolitica(anyLong());
    }
}