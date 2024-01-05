package com.bookingProject.tour.exp.unitTest.service.imp;

import com.bookingProject.tour.exp.auth.JwtUtils;
import com.bookingProject.tour.exp.entity.ERole;
import com.bookingProject.tour.exp.entity.UserEntity;
import com.bookingProject.tour.exp.entity.dto.userEntity.LoginUser;
import com.bookingProject.tour.exp.entity.dto.userEntity.SaveUser;
import com.bookingProject.tour.exp.entity.dto.userEntity.UserEntityDTO;
import com.bookingProject.tour.exp.repository.IUserEntityRepository;
import com.bookingProject.tour.exp.service.IEmailService;
import com.bookingProject.tour.exp.service.imp.UserEntityService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserEntityServiceTest {
    @Mock
    private final PasswordEncoder passwordEncoder=new BCryptPasswordEncoder();
    @Mock
    private IEmailService emailService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtils jwt;
    @Mock
    private IUserEntityRepository repository;
    @InjectMocks
    private UserEntityService service;
    AutoCloseable autoCloseable;
    private final ObjectMapper mapper=new ObjectMapper();
    private UserEntity user;
    private UserEntityDTO userEntityDTO;
    private ResponseEntity<?> result;

    @BeforeEach
    void setUp() {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        autoCloseable= MockitoAnnotations.openMocks(this);
        user=builder();
        userEntityDTO=mapper.convertValue(user,UserEntityDTO.class);
        user.setPassword(passwordEncoder.encode("password"));
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    public UserEntity builder(){
        return UserEntity.builder()
                .id(1L).name("name").lastName("last name")
                .email("email@mail.com").password("password")
                .role(ERole.ADMIN).build();
    }

    @Test
    void save_user_successful_test() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, JOSEException {
        SaveUser saveUser=mapper.convertValue(user,SaveUser.class);
        when(repository.save(any(UserEntity.class))).thenReturn(user);
        when(jwt.generarTokenkey(any(UserEntity.class))).thenReturn("token");
        result= service.registrarUsuario(saveUser);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isInstanceOf(String.class);
        verify(repository,times(1)).save(any(UserEntity.class));
    }

    @Test
    void login_user_successful_test() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, JOSEException {
        LoginUser loginUser= new LoginUser(user.getEmail(),user.getPassword());
        when(repository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(jwt.generarTokenkey(any(UserEntity.class))).thenReturn("token");
        result= service.login(loginUser);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isInstanceOf(String.class);
        verify(repository,times(1)).findByEmail(anyString());
    }

    @Test
    void get_all_user_successful_test() {
        when(repository.findAll()).thenReturn(new ArrayList<>(Collections.singletonList(user)));
        assertThat(service.traerTodo().isEmpty()).isFalse();
        verify(repository,times(1)).findAll();
    }

    @Test
    void get_details_user_successful_test() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(user));
        result=service.traerId(1L);
        UserEntityDTO resultUser= mapper.convertValue(result.getBody(), UserEntityDTO.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resultUser.getId()).isEqualTo(user.getId());
        assertThat(resultUser.getName()).isEqualTo(user.getName());
        assertThat(resultUser.getLastName()).isEqualTo(user.getLastName());
        assertThat(resultUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(resultUser.getRole()).isEqualTo(user.getRole());
        assertThat(resultUser.getPassword()).isEqualTo(user.getPassword());
        verify(repository,times(1)).findById(anyLong());
    }

    @Test
    void get_details_user_unsuccessful_with_wrong_id_test() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());
        result=service.traerId(1L);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(result.getBody()).isEqualTo("No se encontró el id en la base de datos");
        verify(repository,times(1)).findById(anyLong());
    }

    @Test
    void modify_user_successful_test() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(user));
        result= service.modificarUsuario(userEntityDTO);
        UserEntityDTO resultUser= mapper.convertValue(result.getBody(), UserEntityDTO.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resultUser.getId()).isEqualTo(user.getId());
        assertThat(resultUser.getName()).isEqualTo(user.getName());
        assertThat(resultUser.getLastName()).isEqualTo(user.getLastName());
        assertThat(resultUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(resultUser.getRole()).isEqualTo(user.getRole());
        assertThat(resultUser.getPassword()).isEqualTo(user.getPassword());
        verify(repository,times(1)).findById(anyLong());
        verify(repository,times(1)).save(any(UserEntity.class));
    }

    @Test
    void modify_user_unsuccessful_with_wrong_id_test() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());
        result= service.modificarUsuario(userEntityDTO);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(result.getBody()).isEqualTo("No se encontró el id en la base de datos");
        verify(repository,times(1)).findById(anyLong());
        verify(repository,times(0)).save(any(UserEntity.class));
    }

    @Test
    void partial_modify_user_successful_test() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(user));
        result=service.patchMod(userEntityDTO);
        UserEntityDTO resultUser= mapper.convertValue(result.getBody(), UserEntityDTO.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resultUser.getId()).isEqualTo(user.getId());
        assertThat(resultUser.getName()).isEqualTo(user.getName());
        assertThat(resultUser.getLastName()).isEqualTo(user.getLastName());
        assertThat(resultUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(resultUser.getRole()).isEqualTo(user.getRole());
        assertThat(resultUser.getPassword()).isEqualTo(user.getPassword());
        verify(repository,times(1)).findById(anyLong());
    }

    @Test
    void partial_modify_user_unsuccessful_with_wrong_id_test() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());
        result=service.patchMod(userEntityDTO);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(result.getBody()).isEqualTo("No se encontró el id en la base de datos");
        verify(repository,times(1)).findById(anyLong());
        verify(repository,times(0)).save(any(UserEntity.class));
    }

    @Test
    void delete_user_successful_test() {
        mock(IUserEntityRepository.class, CALLS_REAL_METHODS);
        when(repository.findById(anyLong())).thenReturn(Optional.of(user));
        doAnswer(Answers.CALLS_REAL_METHODS).when(repository).deleteById(anyLong());
        result= service.borrarUsuario(1L);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNull();
        verify(repository,times(1)).findById(anyLong());
        verify(repository,times(1)).deleteById(anyLong());
    }

    @Test
    void delete_user_unsuccessful_with_wrong_id_test() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());
        result= service.borrarUsuario(1L);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(result.getBody()).isEqualTo("No se encontró el id en nuestra base de datos");
        verify(repository,times(1)).findById(anyLong());
        verify(repository,times(0)).deleteById(anyLong());
    }
}