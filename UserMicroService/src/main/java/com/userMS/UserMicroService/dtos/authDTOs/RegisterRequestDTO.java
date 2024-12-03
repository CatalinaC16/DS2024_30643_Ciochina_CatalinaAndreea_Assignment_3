package com.userMS.UserMicroService.dtos.authDTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {

    private String firstName;
    private String secondName;
    private String email;
    private String password;
}
