package com.bookingProject.tour.exp.integrationTest.controller;

import com.bookingProject.tour.exp.entity.ERole;
import com.bookingProject.tour.exp.entity.UserEntity;
import com.bookingProject.tour.exp.entity.dto.userEntity.LoginUser;
import com.bookingProject.tour.exp.entity.dto.userEntity.SaveUser;
import com.bookingProject.tour.exp.entity.dto.userEntity.UserEntityDTO;
import com.bookingProject.tour.exp.integrationTest.repository.ITestUserEntityRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserEntityControllerTestWithH2 {
    @LocalServerPort
    private int port;
    private String baseUrl="http://localhost";
    private static TestRestTemplate testRestTemplate;
    @Autowired
    private ITestUserEntityRepository repository;
    @Autowired
    private ObjectMapper mapper;
    private static PasswordEncoder encoder;
    private SaveUser saveUser;
    private UserEntity user;
    private UserEntityDTO userEntityDTO;
    private UserEntity userRepo;
    private String tokenAdmin;
    private final LoginUser loginUser=new LoginUser();

    @BeforeAll
    static void beforeAll() {
        testRestTemplate=new TestRestTemplate();
        encoder=new BCryptPasswordEncoder();
    }

    @BeforeEach
    void setUp() {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        baseUrl=baseUrl.concat(":"+port+"/api/user");
        userEntityDTO=builderTest();
        user=mapper.convertValue(userEntityDTO, UserEntity.class);
        user.setPassword(encoder.encode(userEntityDTO.getPassword()));
        tokenAdmin= getTokenAdmin();
    }

    private String getTokenAdmin() {
        userRepo= mapper.convertValue(builderRepoAdmin(), UserEntity.class);
        userRepo.setPassword(encoder.encode(userRepo.getPassword()));
        repository.save(userRepo);
        loginUser.setEmail(userRepo.getEmail());
        loginUser.setPassword("superAdmin");
        return testRestTemplate.postForObject(baseUrl.concat("/public/login"), loginUser, String.class);
    }

    public UserEntityDTO builderTest() {
        return UserEntityDTO.builder().id(2L)
                .name("juan").lastName("pepe").email("ballena@hotmail.com.ar")
                .password("superSecret").role(ERole.USER)
                .build();
    }

    public UserEntityDTO builderRepoAdmin() {
        return UserEntityDTO.builder().id(1L)
                .name("Admin").lastName("Admin").email("admin@mail.com")
                .password("superAdmin").role(ERole.ADMIN)
                .build();
    }
    @Test
    void register_user_successful_test() {
        userEntityDTO.setEmail("www@emial.com");
        saveUser = mapper.convertValue(userEntityDTO, SaveUser.class);
        ResponseEntity<String> response=testRestTemplate.postForEntity(baseUrl.concat("/public/guardar"),saveUser, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void register_user_unsuccessful_with_email_already_exist_test() {
        saveUser = mapper.convertValue(builderRepoAdmin(), SaveUser.class);
        ResponseEntity<String> response=testRestTemplate.postForEntity(baseUrl.concat("/public/guardar"),saveUser, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("El email proporcionado ya se encuentra resgitrado");
    }

    @Test
    void login_user_successful_test() {
        ResponseEntity<String> result = testRestTemplate.postForEntity(baseUrl.concat("/public/login"), loginUser, String.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void login_user_unsuccessful_with_wrong_email_test() {
        loginUser.setEmail("emailNotExist@mail.com");
        ResponseEntity<String> result = testRestTemplate.postForEntity(baseUrl.concat("/public/login"), loginUser, String.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(result.getBody()).isEqualTo("El email o password no son correctas");
    }

    @Test
    void login_user_unsuccessful_with_wrong_password_test() {
        loginUser.setPassword("passwordNotMatch");
        ResponseEntity<String> result = testRestTemplate.postForEntity(baseUrl.concat("/public/login"), loginUser, String.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(result.getBody()).isEqualTo("El email o password no son correctas");
    }

    @Test
    void get_source_protected_unsuccessful_with_expired_token_test() {
        String tokenExpired= "eyJhbGciOiJSUzI1NiJ9.eyJleHAiOjE3MDQwNDIyMTQsInN1YiI6IkFETUlOQGFkbWluLmNvbSIsImlhdCI6MTcwNDAzMjIxNH0.IdviZVw6ruHWWrd7orY6OUgkgj3M-gq9miYLYJ3MQims4ETNOmDl8ADpCPQaPGaqRBSgaOpoinLouRQ2cl_kn80WNa-NGU_xGvi7xJ-ulk71cZnC0HU-6OKdM0j2BJ4q5qQ4kpcwbIx-1uhQWj95rV_V7nU3owvRKUAEmewWE37yHOdWPB72JfUa7aYCpZGfqeR8wfrP1ByIjP3bASA94HAwmYFgBKOcpBlSbG_-saMeF--JaQqyps5OxUJ3RGMcqNaH_hVqOfuDoOFIhlzp68gr2Wavl2VEYXYObnV0hNrtMTzcCtdJKdMv1jH-X1fJKvtENfF4zWifFXZ_609d8w";
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(Objects.requireNonNull(tokenExpired));
        HttpEntity request= new HttpEntity<>(headers);
        ResponseEntity result = testRestTemplate.exchange(baseUrl.concat("/admin/detalle/1"), HttpMethod.GET, request, String.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(result.getBody()).isEqualTo("El token expiró");
    }

    @Test
    void get_source_protected_unsuccessful_with_modify_token_test() {
        String tokenModify="eyJhbGciOiJSUzI1NiJ9.eyJleHAiOjE3MDQwNDIyMTQsInN1YiI6IkFETUlOQGFkbWlULmNvbSIsImlhdCI6MTcwNDAzMjIxNH0.IdviZVw6ruHWWrd7orY6OUgkgj3M-gq9miYLYJ3MQims4ETNOmDl8ADpCPQaPGaqRBSgaOpoinLouRQ2cl_kn80WNa-NGU_xGvi7xJ-ulk71cZnC0HU-6OKdM0j2BJ4q5qQ4kpcwbIx-1uhQWj95rV_V7nU3owvRKUAEmewWE37yHOdWPB72JfUa7aYCpZGfqeR8wfrP1ByIjP3bASA94HAwmYFgBKOcpBlSbG_-saMeF--JaQqyps5OxUJ3RGMcqNaH_hVqOfuDoOFIhlzp68gr2Wavl2VEYXYObnV0hNrtMTzcCtdJKdMv1jH-X1fJKvtENfF4zWifFXZ_609d8w";
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(Objects.requireNonNull(tokenModify));
        HttpEntity request= new HttpEntity<>(headers);
        ResponseEntity result = testRestTemplate.exchange(baseUrl.concat("/admin/detalle/1"), HttpMethod.GET, request, String.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(result.getBody()).isEqualTo("El token parece estar adulterado");
    }

    @Test
    void get_source_protected_unsuccessful_with_wrong_permission_test() {
        repository.save(user);
        loginUser.setEmail(user.getEmail());
        loginUser.setPassword(userEntityDTO.getPassword());
        ResponseEntity<String> resultLogin = testRestTemplate.postForEntity(baseUrl.concat("/public/login"), loginUser, String.class);
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(Objects.requireNonNull(resultLogin.getBody()));
        HttpEntity request= new HttpEntity<>(headers);
        ResponseEntity result = testRestTemplate.exchange(baseUrl.concat("/admin/detalle/1"), HttpMethod.GET, request, String.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(result.getBody()).isEqualTo("No tienes suficientes privilegios para acceder a este recurso");
    }

    @Test
    void get_all_user_successful_test() {
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(tokenAdmin);
        HttpEntity request= new HttpEntity<>(headers);
        ResponseEntity result = testRestTemplate.exchange(baseUrl.concat("/admin/traerTodo"), HttpMethod.GET, request, ArrayList.class);
        ArrayList resultBody=mapper.convertValue(result.getBody(),ArrayList.class);
        assertThat(resultBody.size()).isEqualTo(repository.findAll().size());
    }

    @Test
    void get_details_user_successful_test() throws JsonProcessingException {
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(tokenAdmin);
        HttpEntity request= new HttpEntity<>(headers);
        ResponseEntity result = testRestTemplate.exchange(baseUrl.concat("/admin/detalle/{id}"), HttpMethod.GET, request, String.class,1);
        UserEntityDTO resultBody=mapper.readValue(result.getBody().toString(),UserEntityDTO.class);
        UserEntity repoResult= repository.findById(1L).get();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resultBody.getName()).isEqualTo(repoResult.getName());
        assertThat(resultBody.getLastName()).isEqualTo(repoResult.getLastName());
        assertThat(resultBody.getEmail()).isEqualTo(repoResult.getEmail());
        assertThat(resultBody.getRole()).isEqualTo(repoResult.getRole());
    }

    @Test
    void modify_user_successful_test() throws JsonProcessingException {
        userRepo.setEmail("custom@mail.com");
        userRepo.setId(null);
        repository.save(userRepo);
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(tokenAdmin);
        HttpEntity request= new HttpEntity<>(userEntityDTO,headers);
        ResponseEntity result = testRestTemplate.exchange(baseUrl.concat("/admin/modificar"), HttpMethod.PUT, request, String.class);
        UserEntityDTO resultBody=mapper.readValue(result.getBody().toString(),UserEntityDTO.class);
        UserEntity repoResult= repository.findById(2L).get();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resultBody.getName()).isEqualTo(repoResult.getName());
        assertThat(resultBody.getLastName()).isEqualTo(repoResult.getLastName());
        assertThat(resultBody.getEmail()).isEqualTo(repoResult.getEmail());
        assertThat(resultBody.getRole()).isEqualTo(repoResult.getRole());
    }

    @Test
    void partial_modify_user_successful_test() throws JsonProcessingException {
        userRepo.setEmail("custom@mail.com");
        userRepo.setId(null);
        repository.save(userRepo);
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(tokenAdmin);
        HttpEntity request= new HttpEntity<>(userEntityDTO,headers);
        ResponseEntity result = testRestTemplate.exchange(baseUrl.concat("/admin/parcialMod"), HttpMethod.PATCH, request, String.class);
        UserEntityDTO resultBody=mapper.readValue(result.getBody().toString(),UserEntityDTO.class);
        UserEntity repoResult= repository.findById(2L).get();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resultBody.getName()).isEqualTo(repoResult.getName());
        assertThat(resultBody.getLastName()).isEqualTo(repoResult.getLastName());
        assertThat(resultBody.getEmail()).isEqualTo(repoResult.getEmail());
        assertThat(resultBody.getRole()).isEqualTo(repoResult.getRole());
    }

    @Test
    void delete_user_successful_test() {
        repository.save(user);
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(tokenAdmin);
        HttpEntity request= new HttpEntity<>(headers);
        ResponseEntity result = testRestTemplate.exchange(baseUrl.concat("/admin/eliminar/{id}"), HttpMethod.DELETE, request, String.class,2);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNull();
    }
}