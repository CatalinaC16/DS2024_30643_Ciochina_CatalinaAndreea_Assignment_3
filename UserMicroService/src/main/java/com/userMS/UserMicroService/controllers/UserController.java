package com.userMS.UserMicroService.controllers;

import com.userMS.UserMicroService.dtos.userDTOs.UserDTO;
import com.userMS.UserMicroService.dtos.userDTOs.UserUpdateDTO;
import com.userMS.UserMicroService.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/getAll")
    public ResponseEntity<List<UserDTO>> getAllUsers() {

        return ResponseEntity.ok(this.userService.getAllUsers());
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(this.userService.getUserById(id));
    }

    @GetMapping("/getByEmail/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable("email") String email) {
        return ResponseEntity.ok(this.userService.getUserByEmail(email));
    }

    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(this.userService.deleteUserById(id));
    }

    @DeleteMapping("/deleteByEmail/{email}")
    public ResponseEntity<?> deleteUserByEmail(@PathVariable("email") String email) {
        return ResponseEntity.ok(this.userService.deleteUserByEmail(email));
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<?> deleteAllUsers() {
        return ResponseEntity.ok(this.userService.deleteAll());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUserById(@PathVariable("id") UUID id, @RequestBody UserUpdateDTO userUpdateDTO) {
        return ResponseEntity.ok(this.userService.updateUserById(id, userUpdateDTO));
    }
}
