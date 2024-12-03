package com.example.deviceMS.dtos.mappers;

import com.example.deviceMS.dtos.userDTOs.UserDTO;
import com.example.deviceMS.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper implements Mapper<User, UserDTO> {

    @Override
    public UserDTO convertToDTO(User user) {
        if (user != null) {
            return UserDTO.builder()
                    .id(user.getId())
                    .devices(user.getDevices())
                    .build();
        }
        return null;
    }

    @Override
    public User convertToEntity(UserDTO userDTO) {
        if (userDTO != null) {
            return User.builder()
                    .id(userDTO.getId())
                    .build();
        }
        return null;
    }
}
