package com.bookingProject.tour.exp.service.imp;

import com.bookingProject.tour.exp.auth.JwtUtils;
import com.bookingProject.tour.exp.entity.ERole;
import com.bookingProject.tour.exp.entity.UserEntity;
import com.bookingProject.tour.exp.entity.dto.userEntity.SaveUser;
import com.bookingProject.tour.exp.entity.dto.userEntity.UserEntityDTO;
import com.bookingProject.tour.exp.repository.IUserEntityRepository;
import com.bookingProject.tour.exp.service.IEmailService;
import com.bookingProject.tour.exp.service.IUserEntityService;
import com.bookingProject.tour.exp.template.EmailTemplate;
import com.bookingProject.tour.exp.entity.dto.userEntity.LoginUser;
import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class UserEntityService implements IUserEntityService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private IUserEntityRepository userEntityRepository;
    @Autowired
    private IEmailService emailService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtils jwt;

    @Override
    public ResponseEntity<?> registrarUsuario(SaveUser saveUser) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, JOSEException {
        UserEntity userEntity= UserEntity.builder()
            .name(saveUser.getName())
            .lastName(saveUser.getLastName())
            .email(saveUser.getEmail())
            .password(passwordEncoder.encode(saveUser.getPassword()))
            .role(ERole.USER).build();
        userEntity= userEntityRepository.save(userEntity);
        String token= jwt.generarTokenkey(userEntity);
        emailService.sendEmail(userEntity.getEmail(),"Registro de usuario - Ruta Viva", new EmailTemplate().singUpTemplate(userEntity.getName(),userEntity.getEmail()));
        return new ResponseEntity<>(token,HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> login(LoginUser login) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, JOSEException {
        UserEntity user= userEntityRepository.findByEmail(login.getEmail()).orElseThrow(()->new BadCredentialsException(""));
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login.getEmail(),login.getPassword()));
        String token= jwt.generarTokenkey(user);
        return new ResponseEntity<>(token,HttpStatus.OK);
    }

    @Override
    public List<UserEntity> traerTodo() {
        return userEntityRepository.findAll();
    }

    @Override
    public ResponseEntity<?> traerId(Long id) {
        Optional<UserEntity> find= userEntityRepository.findById(id);
        if (find.isPresent()) return new ResponseEntity<>(find.get(),HttpStatus.OK);
        return new ResponseEntity<>("No se encontró el id en la base de datos", HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> modificarUsuario(UserEntityDTO userEntityDTO) {
        Optional<UserEntity> find=userEntityRepository.findById(userEntityDTO.getId());
        if (find.isPresent()) {
            UserEntity userEntity = find.get();
            userEntity.setName(userEntityDTO.getName());
            userEntity.setLastName(userEntityDTO.getLastName());
            userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
            userEntity.setRole(userEntityDTO.getRole());
            userEntityRepository.save(userEntity);
            return new ResponseEntity<>(userEntity, HttpStatus.OK);
        } return new ResponseEntity<>("No se encontró el id en la base de datos",HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> patchMod(UserEntityDTO userEntityDTO) {
        Optional<UserEntity> find=userEntityRepository.findById(userEntityDTO.getId());
        if (find.isPresent()) {
            UserEntity user= find.get();
            if (userEntityDTO.getName()!=null) user.setName(userEntityDTO.getName());
            if (userEntityDTO.getLastName()!=null) user.setLastName(userEntityDTO.getLastName());
            if (userEntityDTO.getRole()!=null) user.setRole(userEntityDTO.getRole());
            if (userEntityDTO.getPassword()!=null) user.setPassword(passwordEncoder.encode(userEntityDTO.getPassword()));
            userEntityRepository.save(user);
            return new ResponseEntity<>(user,HttpStatus.OK);
        } return new ResponseEntity<>("No se encontró el id en la base de datos",HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> borrarUsuario(Long id) {
        Optional<UserEntity> find = userEntityRepository.findById(id);
        if (find.isPresent()){
            userEntityRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>("No se encontró el id en nuestra base de datos", HttpStatus.NOT_FOUND);
    }
}
