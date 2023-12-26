package com.bookingProject.tour.exp.unitTest.controller;

import com.bookingProject.tour.exp.auth.JwtUtils;
import com.bookingProject.tour.exp.controller.UserEntityController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.bookingProject.tour.exp.service.imp.UserEntityService;
import com.bookingProject.tour.exp.entity.UserEntity;
import com.bookingProject.tour.exp.entity.ERole;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserEntityController.class)
class UserEntityControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private UserEntityService service;
    @MockBean
    private JwtUtils jwt;
    private UserEntity user1;

    @BeforeEach
    void setUp() {
        user1= UserEntity.builder()
                .name("juan").lastName("pepe")
                .email("ballena@hotmail.com.ar").password(new BCryptPasswordEncoder().encode("superSecret"))
                .role(ERole.USER).build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void createUser() {
    }

    @Test
    void login() {
    }

    @Test
    void traerTodo() {
    }

    @Test
    @WithMockUser
    void detalleUser() throws Exception {
        doReturn(new ResponseEntity<>(user1, HttpStatus.OK)).when(service).traerId(anyLong());
        mvc.perform(get("/api/user/admin/detalle/1")).andDo(print())
                .andExpect(status().isOk());
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