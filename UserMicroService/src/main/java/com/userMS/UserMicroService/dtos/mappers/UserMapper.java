package com.userMS.UserMicroService.dtos.mappers;

import com.userMS.UserMicroService.dtos.userDTOs.UserDTO;
import com.userMS.UserMicroService.entities.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper implements Mapper<User, UserDTO> {

    @Override
    public UserDTO convertToDTO(User user) {
        if (user != null) {
            return UserDTO.builder()
                    .id(user.getId())
                    .firstName(user.getFirstName())
                    .secondName(user.getSecondName())
                    .email(user.getEmail())
                    .role(user.getRole())
                    .build();
        }
        return null;
    }

    @Override
    public User convertToEntity(UserDTO userDTO) {
        if (userDTO != null) {
            return User.builder()
                    .firstName(userDTO.getFirstName())
                    .secondName(userDTO.getSecondName())
                    .email(userDTO.getEmail())
                    .role(userDTO.getRole())
                    .build();
        }
        return null;
    }
}
