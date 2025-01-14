package com.espe.pageimage.controller;

import com.espe.pageimage.dto.LoginDTO;
import com.espe.pageimage.model.User;

import com.espe.pageimage.repository.UserRepository;
import com.espe.pageimage.security.JWTAuthResonseDTO;
import com.espe.pageimage.security.JwtTokenProvider;
import com.espe.pageimage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    //@Autowired
    //private JwtUtil jwtUtil;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        if (userRepository.existsByUsername(user.getUsername())){
            return new ResponseEntity<>("Ese usuario ya existe", HttpStatus.BAD_REQUEST);
        }
        User usuario=new User();
        usuario.setUsername(user.getUsername());
        usuario.setPassword(passwordEncoder.encode(user.getPassword()));
        usuario.setRole(user.getRole());
        userRepository.save(usuario);
        return new ResponseEntity<>("Usuario registrado existosamente",HttpStatus.OK);

    }

    @PostMapping("/login")
    public ResponseEntity<JWTAuthResonseDTO> loginUser(@RequestBody LoginDTO loginDTO) {
        Authentication authentication =authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getUsername(),loginDTO.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //User user = userService.findByUsername(username);
        //obtenemos el token de jwtTokenprovider
        String token=jwtTokenProvider.generarToken(authentication);
       //return new ResponseEntity<>("Ha iniciado sesion con exito", HttpStatusCode.valueOf(200));
        return  ResponseEntity.ok(new JWTAuthResonseDTO(token));

    }
}
