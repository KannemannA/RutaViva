package com.bookingProject.tour.exp.integrationTest;

import com.bookingProject.tour.exp.entity.UserEntity;
import com.bookingProject.tour.exp.entity.dto.userEntity.LoginUser;
import com.bookingProject.tour.exp.entity.dto.userEntity.SaveUser;
import com.bookingProject.tour.exp.entity.dto.userEntity.UserEntityDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@WebAppConfiguration
class UserEntityControllerTestI {
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
    void login() throws Exception {
        String bodyDefault = mapper.writeValueAsString(saveUser);
        mvc.perform(post("/api/user/public/guardar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyDefault));
        LoginUser loginUser = new LoginUser();
        loginUser.setEmail(saveUser.getEmail());
        loginUser.setPassword(saveUser.getPassword());
        String body = mapper.writeValueAsString(loginUser);
        mvc.perform(post("/api/user/public/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void traerTodo() throws Exception {
        String bodyDefault = mapper.writeValueAsString(saveUser);
        mvc.perform(post("/api/user/public/guardar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyDefault));
        MvcResult mvcResult= mvc.perform(get("/api/user/admin/traerTodo"))
                .andDo(print())
                .andExpect((ResultMatcher) content())
                .andExpect(jsonPath("$",hasSize(greaterThan(0))))
                .andReturn();
        String result= mvcResult.getResponse().getContentAsString();
        userEntityDTO= mapper.readValue(result, new TypeReference<List<UserEntityDTO>>() {
        }).get(0);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void detalleUser() throws Exception {
        String bodyDefault = mapper.writeValueAsString(saveUser);
        mvc.perform(post("/api/user/public/guardar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyDefault));
        mvc.perform(get("/api/user/admin/detalle/"+userEntityDTO.getId()))
                .andDo(print())
                .andExpect((ResultMatcher) content())
                .andExpect((ResultMatcher) jsonPath("$.name",userEntityDTO.getName()));
    }

    @Test
    @WithMockUser
    void modificar() {
    }

    @Test
    @WithMockUser
    void parcialMod() {
    }

    @Test
    @WithMockUser
    void delete() {
    }
}