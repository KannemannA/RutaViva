package com.bookingProject.tour.exp.integrationTest.controller;

import com.bookingProject.tour.exp.auth.JwtUtils;
import com.bookingProject.tour.exp.entity.Politic;
import com.bookingProject.tour.exp.entity.dto.politic.PoliticDTO;
import com.bookingProject.tour.exp.entity.dto.politic.SavePolitic;
import com.bookingProject.tour.exp.integrationTest.repository.ITestPoliticRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.parameters.P;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@WebAppConfiguration
class PoliticControllerTestWithH2 {
    private final String baseUrl= "/api/politic";
    @Autowired
    private WebApplicationContext context;
    private MockMvc mvc;
    @Autowired
    private ITestPoliticRepository repository;
    @Autowired
    private ObjectMapper mapper;
    private SavePolitic savePolitic;
    private PoliticDTO politicDTO;
    private Politic politic;

    @BeforeEach
    void setUp() {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
        politicDTO=builder();
        politic= mapper.convertValue(politicDTO,Politic.class);
        savePolitic= mapper.convertValue(politic, SavePolitic.class);
    }

    private PoliticDTO builder(){
        return PoliticDTO.builder()
                .id(87L)
                .title("Some title here")
                .description("an description with Lorem? really?")
                .build();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void create_politic_successful_test() throws Exception {
        String body= mapper.writeValueAsString(savePolitic);
        MvcResult result=mvc.perform(post(baseUrl+"/admin/guardar")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(body)).andDo(print())
                .andExpect(status().isCreated())
                .andReturn();
        Politic politic1= mapper.readValue(result.getResponse().getContentAsString(),Politic.class);
        Politic resultRepo= repository.findById(politic1.getId()).get();
        assertThat(politic1.getDescription()).isEqualTo(resultRepo.getDescription());
        assertThat(politic1.getTitle()).isEqualTo(resultRepo.getTitle());
    }

    @Test
    @Sql(statements = "INSERT INTO politic(id, description, title, id_product) VALUES (87, 'an description with Lorem? really?', 'Some title here', null);", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM politic WHERE id=87;", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void get_all_politic_successful_test() throws Exception {
        MvcResult result= mvc.perform(get(baseUrl+"/public/traerTodo"))
                .andDo(print()).andExpect(status().isOk()).andReturn();
        ArrayList list = mapper.readValue(result.getResponse().getContentAsString(), ArrayList.class);
        assertThat(list.size()).isEqualTo(repository.findAll().size());
    }

    @Test
    @Sql(statements = "INSERT INTO politic(id, description, title, id_product) VALUES (87, 'an description with Lorem? really?', 'Some title here', null);", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM politic WHERE id=87;", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void get_details_politic_successful_test() throws Exception {
        MvcResult result= mvc.perform(get(baseUrl+"/public/detalle/{id}",87))
                .andDo(print()).andExpect(status().isOk()).andReturn();
        Politic politic1=mapper.readValue(result.getResponse().getContentAsString(),Politic.class);
        Politic politicRepo= repository.findById(87L).get();
        assertThat(politic1.getTitle()).isEqualTo(politicRepo.getTitle());
        assertThat(politic1.getDescription()).isEqualTo(politicRepo.getDescription());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @Sql(statements = "INSERT INTO politic(id, description, title, id_product) VALUES (87, 'an description with Lorem? really?', 'Some title here', null);", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM politic WHERE id=87;", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void modify_politic_successful_test() throws Exception {
        politicDTO.setTitle("Title Modify");
        politicDTO.setDescription("Description text modify for test integration. Ok?");
        String body= mapper.writeValueAsString(politicDTO);
        MvcResult result= mvc.perform(put(baseUrl+"/admin/modificar")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(body)).andDo(print())
                .andExpect(status().isOk()).andReturn();
        Politic politic1= mapper.readValue(result.getResponse().getContentAsString(), Politic.class);
        Politic politicRepo= repository.findById(politicDTO.getId()).get();
        assertThat(politic1.getDescription()).isEqualTo(politicRepo.getDescription());
        assertThat(politic1.getTitle()).isEqualTo(politicRepo.getTitle());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @Sql(statements = "INSERT INTO politic(id, description, title, id_product) VALUES (87, 'an description with Lorem? really?', 'Some title here', null);", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM politic WHERE id=87;", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void partial_modify_politic_successful_test() throws Exception {
        politicDTO.setDescription("Description text modify for test integration. Ok?");
        String body= mapper.writeValueAsString(politicDTO);
        MvcResult result= mvc.perform(patch(baseUrl+"/admin/parcialMod")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(body)).andDo(print())
                .andExpect(status().isOk()).andReturn();
        Politic politic1= mapper.readValue(result.getResponse().getContentAsString(), Politic.class);
        Politic politicRepo= repository.findById(politicDTO.getId()).get();
        assertThat(politic1.getDescription()).isEqualTo(politicRepo.getDescription());
        assertThat(politic1.getTitle()).isEqualTo(politicRepo.getTitle());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @Sql(statements = "INSERT INTO politic(id, description, title, id_product) VALUES (87, 'an description with Lorem? really?', 'Some title here', null);", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void delete_politic_successful_test() throws Exception {
        MvcResult result= mvc.perform(delete(baseUrl+"/admin/eliminar/{id}",87))
                .andExpect(status().isOk()).andReturn();
        assertThat(result.getResponse().getContentAsString()).isEmpty();
    }
}
