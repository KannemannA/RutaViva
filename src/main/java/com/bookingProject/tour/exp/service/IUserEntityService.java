package com.bookingProject.tour.exp.service;

import com.bookingProject.tour.exp.entity.UserEntity;
import com.bookingProject.tour.exp.entity.dto.userEntity.SaveUser;
import com.bookingProject.tour.exp.entity.dto.userEntity.UserEntityDTO;
import com.bookingProject.tour.exp.entity.dto.userEntity.LoginUser;
import com.nimbusds.jose.JOSEException;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

public interface IUserEntityService {
    public ResponseEntity<?> registrarUsuario(SaveUser user) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, JOSEException;
    public List<UserEntity> traerTodo();
    public ResponseEntity<?> traerId(Long id);
    public ResponseEntity<?> modificarUsuario(UserEntityDTO userEntityDTO);
    public ResponseEntity<?> patchMod(UserEntityDTO userEntityDTO);
    public ResponseEntity<?> borrarUsuario(Long id);
    public ResponseEntity<?> login(LoginUser login) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, JOSEException;
}
