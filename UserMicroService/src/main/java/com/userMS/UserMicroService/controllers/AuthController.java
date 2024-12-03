package com.userMS.UserMicroService.controllers;

import com.userMS.UserMicroService.dtos.authDTOs.AuthRequestDTO;
import com.userMS.UserMicroService.dtos.authDTOs.AuthResponseDTO;
import com.userMS.UserMicroService.dtos.authDTOs.RegisterRequestDTO;
import com.userMS.UserMicroService.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody RegisterRequestDTO registerRequestDTO) {
        return ResponseEntity.ok(this.authService.register(registerRequestDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO authRequestDTO) {
        return ResponseEntity.ok(this.authService.login(authRequestDTO));
    }
}
