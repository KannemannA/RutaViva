package com.bookingProject.tour.exp.unitTest.controller;

import com.bookingProject.tour.exp.auth.JwtUtils;
import com.bookingProject.tour.exp.config.SecurityConfig;
import com.bookingProject.tour.exp.controller.UserEntityController;
import com.bookingProject.tour.exp.entity.dto.userEntity.LoginUser;
import com.bookingProject.tour.exp.entity.dto.userEntity.SaveUser;
import com.bookingProject.tour.exp.entity.dto.userEntity.UserEntityDTO;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserEntityController.class)
@Import(SecurityConfig.class)
class UserEntityControllerTest {
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

    @BeforeEach
    void setUp() {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        user1= builder();
        saveUser= mapper.convertValue(user1,SaveUser.class);
        userEntityDTO= mapper.convertValue(user1,UserEntityDTO.class);
        user1.setPassword(new BCryptPasswordEncoder().encode("superSecret"));
    }

    public UserEntity builder(){
        return UserEntity.builder()
                .name("juan").lastName("pepe").id(2L)
                .email("ballena@hotmail.com.ar").password("superSecret")
                .role(ERole.USER).build();
    }

    @Test
    void login_user_successful_test() throws Exception {
        LoginUser login= new LoginUser(user1.getEmail(),user1.getPassword());
        String loginUser= mapper.writeValueAsString(login);
        doReturn(new ResponseEntity<>("token",HttpStatus.OK)).when(service).login(any(LoginUser.class));
        mvc.perform(post("/user/public/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginUser)).andDo(print())
                .andExpect(status().isOk());
        verify(service,times(1)).login(login);
    }

    @Test
    void register_user_successful_test() throws Exception {
        String body = mapper.writeValueAsString(saveUser);
        doReturn(new ResponseEntity<>(user1, HttpStatus.CREATED)).when(service).registrarUsuario(any(SaveUser.class));
        mvc.perform(post("/user/public/guardar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)).andDo(print())
                .andExpect(status().isCreated());
        verify(service,times(1)).registrarUsuario(saveUser);
    }

    @Test
    void register_user_unsuccessful_with_wrong_email_test() throws Exception {
        saveUser.setEmail("a@a.c");
        //saveUser.setEmail("a@a.ct"); esto pasa la validacion.
        String body = mapper.writeValueAsString(saveUser);
        doReturn(new ResponseEntity<>(user1, HttpStatus.CREATED)).when(service).registrarUsuario(any(SaveUser.class));
        MvcResult result=mvc.perform(post("/user/public/guardar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)).andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
        assertThat(result.getResponse().getContentAsString()).isEqualTo("Introduzca un correo con formato valido. ej example@example.com");
        verify(service,times(0)).registrarUsuario(any(SaveUser.class));
    }

    @Test
    void register_user_unsuccessful_with_wrong_name_test() throws Exception {
        saveUser.setName("aer");
        String body = mapper.writeValueAsString(saveUser);
        doReturn(new ResponseEntity<>(user1, HttpStatus.CREATED)).when(service).registrarUsuario(any(SaveUser.class));
        MvcResult result=mvc.perform(post("/user/public/guardar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)).andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
        assertThat(result.getResponse().getContentAsString()).isEqualTo("Solo aceptamos letras o espacios para los campos nombre y apellido, cada uno con un minimo de 4 caracteres. (no cuentan como caracter los espacios)");
        verify(service,times(0)).registrarUsuario(any(SaveUser.class));
    }

    @Test
    void register_user_unsuccessful_with_wrong_last_name_test() throws Exception {
        saveUser.setLastName("aer");
        String body = mapper.writeValueAsString(saveUser);
        doReturn(new ResponseEntity<>(user1, HttpStatus.CREATED)).when(service).registrarUsuario(any(SaveUser.class));
        MvcResult result=mvc.perform(post("/user/public/guardar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)).andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
        assertThat(result.getResponse().getContentAsString()).isEqualTo("Solo aceptamos letras o espacios para los campos nombre y apellido, cada uno con un minimo de 4 caracteres. (no cuentan como caracter los espacios)");
        verify(service,times(0)).registrarUsuario(any(SaveUser.class));
    }

    @Test
    void register_user_unsuccessful_with_wrong_password_test() throws Exception {
        saveUser.setPassword("aer");
        String body = mapper.writeValueAsString(saveUser);
        doReturn(new ResponseEntity<>(user1, HttpStatus.CREATED)).when(service).registrarUsuario(any(SaveUser.class));
        MvcResult result=mvc.perform(post("/user/public/guardar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)).andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
        assertThat(result.getResponse().getContentAsString()).isEqualTo("La contraseña necesita de un minimo de 4 caracteres.");
        verify(service,times(0)).registrarUsuario(any(SaveUser.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void modify_user_unsuccessful_with_wrong_id_test() throws Exception {
        userEntityDTO.setId(1L);
        String body = mapper.writeValueAsString(userEntityDTO);
        doReturn(new ResponseEntity<>(user1, HttpStatus.OK)).when(service).modificarUsuario(any(UserEntityDTO.class));
        MvcResult result=mvc.perform(put("/user/admin/modificar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)).andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
        assertThat(result.getResponse().getContentAsString()).isEqualTo("Introduzca un numero mayor a uno.");
        verify(service,times(0)).modificarUsuario(any(UserEntityDTO.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void modify_user_successful_test() throws Exception {
        String body = mapper.writeValueAsString(userEntityDTO);
        doReturn(new ResponseEntity<>(user1, HttpStatus.OK)).when(service).modificarUsuario(any(UserEntityDTO.class));
        MvcResult result=mvc.perform(put("/user/admin/modificar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)).andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        UserEntityDTO bodyResult= mapper.readValue(result.getResponse().getContentAsString(),UserEntityDTO.class);
        assertThat(bodyResult.getId()).isEqualTo(user1.getId());
        assertThat(bodyResult.getName()).isEqualTo(user1.getName());
        assertThat(bodyResult.getLastName()).isEqualTo(user1.getLastName());
        assertThat(bodyResult.getEmail()).isEqualTo(user1.getEmail());
        assertThat(bodyResult.getRole()).isEqualTo(user1.getRole());
        assertThat(bodyResult.getPassword()).isEqualTo(user1.getPassword());
        verify(service,times(1)).modificarUsuario(userEntityDTO);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void partial_modify_user_successful_test()  throws Exception{
        String body = mapper.writeValueAsString(userEntityDTO);
        doReturn(new ResponseEntity<>(user1, HttpStatus.OK)).when(service).patchMod(any(UserEntityDTO.class));
        MvcResult result=mvc.perform(patch("/user/admin/parcialMod")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)).andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        UserEntityDTO bodyResult= mapper.readValue(result.getResponse().getContentAsString(),UserEntityDTO.class);
        assertThat(bodyResult.getId()).isEqualTo(user1.getId());
        assertThat(bodyResult.getName()).isEqualTo(user1.getName());
        assertThat(bodyResult.getLastName()).isEqualTo(user1.getLastName());
        assertThat(bodyResult.getEmail()).isEqualTo(user1.getEmail());
        assertThat(bodyResult.getRole()).isEqualTo(user1.getRole());
        assertThat(bodyResult.getPassword()).isEqualTo(user1.getPassword());
        verify(service,times(1)).patchMod(userEntityDTO);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void get_all_user_successful_test() throws Exception {
        doReturn(new ArrayList<>(Collections.singletonList(user1))).when(service).traerTodo();
        MvcResult result=mvc.perform(get("/user/admin/traerTodo")).andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        ArrayList list=mapper.readValue(result.getResponse().getContentAsString(),ArrayList.class);
        assertThat(list.isEmpty()).isFalse();
        verify(service,times(1)).traerTodo();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void get_details_user_successful_test() throws Exception {
        doReturn(new ResponseEntity<>(user1, HttpStatus.OK)).when(service).traerId(anyLong());
        MvcResult result=mvc.perform(get("/user/admin/detalle/1")).andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        UserEntityDTO body= mapper.readValue(result.getResponse().getContentAsString(),UserEntityDTO.class);
        assertThat(body.getId()).isEqualTo(user1.getId());
        assertThat(body.getName()).isEqualTo(user1.getName());
        assertThat(body.getLastName()).isEqualTo(user1.getLastName());
        assertThat(body.getEmail()).isEqualTo(user1.getEmail());
        assertThat(body.getRole()).isEqualTo(user1.getRole());
        assertThat(body.getPassword()).isEqualTo(user1.getPassword());
        verify(service,times(1)).traerId(1L);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void delete_user_successful_test() throws Exception {
        doReturn(new ResponseEntity<>(HttpStatus.OK)).when(service).borrarUsuario(anyLong());
        mvc.perform(delete("/user/admin/eliminar/2"))
                .andExpect(status().isOk());
        verify(service,times(1)).borrarUsuario(anyLong());
    }
}