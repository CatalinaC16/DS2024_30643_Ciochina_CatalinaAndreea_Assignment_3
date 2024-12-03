package com.userMS.UserMicroService.dtos.userDTOs;

import com.userMS.UserMicroService.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private UUID id;
    private String firstName;
    private String secondName;
    private String email;
    private Role role;
}
