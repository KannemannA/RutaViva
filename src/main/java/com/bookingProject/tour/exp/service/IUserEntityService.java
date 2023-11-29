package com.bookingProject.tour.exp.service;

import com.bookingProject.tour.exp.entity.UserEntity;
import com.bookingProject.tour.exp.entity.dto.userEntity.SaveUser;
import com.bookingProject.tour.exp.entity.dto.userEntity.UserEntityDTO;
import com.bookingProject.tour.exp.entity.dto.userEntity.LoginUser;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IUserEntityService {
    public ResponseEntity<?> registrarUsuario(SaveUser user);
    public List<UserEntity> traerTodo();
    public ResponseEntity<?> traerId(Long id);
    public ResponseEntity<?> modificarUsuario(UserEntityDTO userEntityDTO);
    public ResponseEntity<?> patchMod(UserEntityDTO userEntityDTO);
    public ResponseEntity<?> borrarUsuario(Long id);
    public ResponseEntity<?> login(LoginUser login);
}
