package com.bookingProject.tour.exp.integrationTest;

import com.bookingProject.tour.exp.entity.UserEntity;
import com.bookingProject.tour.exp.entity.dto.userEntity.SaveUser;
import com.bookingProject.tour.exp.entity.dto.userEntity.UserEntityDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@WebAppConfiguration
class UserEntityControllerTest {
    @Autowired
    private WebApplicationContext context;
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;

    private SaveUser saveUser;
    private UserEntity user;
    private UserEntityDTO userEntityDTO;

    @BeforeEach
    void setUp() {
        mvc= MockMvcBuilders.webAppContextSetup(context).build();
        saveUser= SaveUser.builder()
                .name("juan").lastName("pepe").email("ballena@hotmail.com.ar")
                .password("superSecret")
                .build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void createUser() throws Exception {
        String body = mapper.writeValueAsString(saveUser);
        mvc.perform(post("/api/user/public/guardar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)).andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void login() {
    }

    @Test
    void traerTodo() {
    }

    @Test
    void detalleUser() {
    }

    @Test
    void modificar() {
    }

    @Test
    void parcialMod() {
    }

    @Test
    void delete() {
    }
}