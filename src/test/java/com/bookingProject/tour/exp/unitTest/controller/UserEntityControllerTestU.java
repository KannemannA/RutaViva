package com.bookingProject.tour.exp.unitTest.controller;

import com.bookingProject.tour.exp.auth.JwtUtils;
import com.bookingProject.tour.exp.config.SecurityConfig;
import com.bookingProject.tour.exp.controller.UserEntityController;
import com.bookingProject.tour.exp.entity.dto.userEntity.LoginUser;
import com.bookingProject.tour.exp.entity.dto.userEntity.SaveUser;
import com.bookingProject.tour.exp.entity.dto.userEntity.UserEntityDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.bookingProject.tour.exp.service.imp.UserEntityService;
import com.bookingProject.tour.exp.entity.UserEntity;
import com.bookingProject.tour.exp.entity.ERole;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserEntityController.class)
@Import(SecurityConfig.class)
class UserEntityControllerTestU {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private UserEntityService service;
    @MockBean
    private JwtUtils jwt;
    @MockBean
    private AuthenticationProvider provider;
    private UserEntity user1;
    private SaveUser saveUser;
    private UserEntityDTO userEntityDTO;
    private final ObjectMapper mapper= new ObjectMapper();

    @BeforeAll
    static void beforeAll() {

    }

    @BeforeEach
    void setUp() {
        user1= UserEntity.builder()
                .name("juan").lastName("pepe")
                .email("ballena@hotmail.com.ar").password(new BCryptPasswordEncoder().encode("superSecret"))
                .role(ERole.USER).build();
        saveUser= SaveUser.builder()
                .name("juan").lastName("pepe").email("ballena@hotmail.com.ar")
                .password("superSecret")
                .build();
        userEntityDTO= UserEntityDTO.builder()
                .name("juan").lastName("pepe")
                .email("ballena@hotmail.com.ar")
                .password("superSecret").id(2L)
                .build();
    }

    @Test
    void createUserSuccessful() throws Exception {
        String body = mapper.writeValueAsString(saveUser);
        doReturn(new ResponseEntity<>(user1, HttpStatus.CREATED)).when(service).registrarUsuario(any(SaveUser.class));
        mvc.perform(post("/api/user/public/guardar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)).andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void createUserWrongEmail() throws Exception {
        saveUser.setEmail("a@a.c");
        //saveUser.setEmail("a@a.ct"); esto pasa la validacion.
        String body = mapper.writeValueAsString(saveUser);
        doReturn(new ResponseEntity<>(user1, HttpStatus.CREATED)).when(service).registrarUsuario(any(SaveUser.class));
        mvc.perform(post("/api/user/public/guardar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)).andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUserWrongName() throws Exception {
        saveUser.setName("aer");
        String body = mapper.writeValueAsString(saveUser);
        doReturn(new ResponseEntity<>(user1, HttpStatus.CREATED)).when(service).registrarUsuario(any(SaveUser.class));
        mvc.perform(post("/api/user/public/guardar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)).andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUserWrongLastName() throws Exception {
        saveUser.setLastName("aer");
        String body = mapper.writeValueAsString(saveUser);
        doReturn(new ResponseEntity<>(user1, HttpStatus.CREATED)).when(service).registrarUsuario(any(SaveUser.class));
        mvc.perform(post("/api/user/public/guardar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)).andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUserWrongPassword() throws Exception {
        saveUser.setPassword("aer");
        String body = mapper.writeValueAsString(saveUser);
        doReturn(new ResponseEntity<>(user1, HttpStatus.CREATED)).when(service).registrarUsuario(any(SaveUser.class));
        mvc.perform(post("/api/user/public/guardar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)).andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void login() throws Exception {
        LoginUser login= new LoginUser(user1.getEmail(),user1.getPassword());
        String loginUser= mapper.writeValueAsString(login);
        doReturn(new ResponseEntity<>("token",HttpStatus.OK)).when(service).login(any(LoginUser.class));
        mvc.perform(post("/api/user/public/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginUser)).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void traerTodo() throws Exception {
        doReturn(new ArrayList<>(List.of(user1))).when(service).traerTodo();
        mvc.perform(get("/api/user/admin/traerTodo")).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void detalleUser() throws Exception {
        doReturn(new ResponseEntity<>(user1, HttpStatus.OK)).when(service).traerId(anyLong());
        mvc.perform(get("/api/user/admin/detalle/1")).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void modificar() throws Exception {
        String body = mapper.writeValueAsString(userEntityDTO);
        doReturn(new ResponseEntity<>(user1, HttpStatus.OK)).when(service).modificarUsuario(any(UserEntityDTO.class));
        mvc.perform(put("/api/user/admin/modificar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void parcialMod()  throws Exception{
        String body = mapper.writeValueAsString(userEntityDTO);
        doReturn(new ResponseEntity<>(user1, HttpStatus.OK)).when(service).patchMod(any(UserEntityDTO.class));
        mvc.perform(patch("/api/user/admin/parcialMod")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void delete() throws Exception {
    }
}