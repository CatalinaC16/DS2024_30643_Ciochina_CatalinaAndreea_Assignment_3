package com.userMS.UserMicroService.dtos.mappers;

import com.userMS.UserMicroService.dtos.authDTOs.AuthResponseDTO;
import com.userMS.UserMicroService.dtos.authDTOs.RegisterRequestDTO;
import com.userMS.UserMicroService.entities.Role;
import com.userMS.UserMicroService.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthMapper implements Mapper<User, RegisterRequestDTO> {

    private final PasswordEncoder passwordEncoder;

    @Override
    public RegisterRequestDTO convertToDTO(User user) {
        if (user != null) {
            return RegisterRequestDTO.builder()
                    .firstName(user.getFirstName())
                    .secondName(user.getSecondName())
                    .email(user.getEmail())
                    .password(user.getPassword())
                    .build();
        }
        return null;
    }

    @Override
    public User convertToEntity(RegisterRequestDTO registerRequestDTO) {
        if (registerRequestDTO != null) {
            return User.builder()
                    .firstName(registerRequestDTO.getFirstName())
                    .secondName(registerRequestDTO.getSecondName())
                    .email(registerRequestDTO.getEmail())
                    .password(passwordEncoder.encode(registerRequestDTO.getPassword()))
                    .role(Role.USER)
                    .build();
        }
        return null;
    }

    public AuthResponseDTO convertToDTOResp(String jwtToken) {
        if (jwtToken != null) {
            return AuthResponseDTO.builder()
                    .token(jwtToken)
                    .build();
        }
        return null;
    }
}
