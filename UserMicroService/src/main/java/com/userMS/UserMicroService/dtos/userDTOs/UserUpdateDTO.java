package com.userMS.UserMicroService.dtos.userDTOs;

import com.userMS.UserMicroService.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {
    private String firstName;
    private String secondName;
    private String password;
    private String email;
    private Role role;
}
