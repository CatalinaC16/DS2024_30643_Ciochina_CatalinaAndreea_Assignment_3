package com.userMS.UserMicroService.services;

import com.userMS.UserMicroService.dtos.mappers.UserMapper;
import com.userMS.UserMicroService.dtos.userDTOs.UserDTO;
import com.userMS.UserMicroService.dtos.userDTOs.UserUpdateDTO;
import com.userMS.UserMicroService.entities.User;
import com.userMS.UserMicroService.exceptions.UserDoesNotExistException;
import com.userMS.UserMicroService.jwt.JwtService;
import com.userMS.UserMicroService.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    private final SyncService syncService;

    private final JwtService jwtService;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public List<UserDTO> getAllUsers() {
        List<User> users = this.userRepository.findAll();
        List<UserDTO> userDTOS = new ArrayList<>();
        if (!users.isEmpty()) userDTOS = users.stream().map(userMapper::convertToDTO).collect(Collectors.toList());
        logger.info("The list of all users was requested");
        return userDTOS;
    }

    public UserDTO getUserById(UUID id) throws UserDoesNotExistException {
        Optional<User> userOptional = this.userRepository.findById(id);
        if (userOptional.isEmpty()) {
            logger.error("User with id={} was not found", id);
            throw new UserDoesNotExistException("The requested user does not exist");
        }
        logger.info("User with id={} was retrieved", id);
        User user = userOptional.get();
        return userMapper.convertToDTO(user);
    }

    public UserDTO getUserByEmail(String email) throws UserDoesNotExistException {
        Optional<User> userOptional = this.userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            logger.error("User with email={} was not found", email);
            throw new UserDoesNotExistException("The requested user does not exist");
        }
        logger.info("User with email={} was retrieved", email);
        User user = userOptional.get();
        return userMapper.convertToDTO(user);
    }

    @Transactional
    public String deleteUserById(UUID id) throws UserDoesNotExistException {
        Optional<User> userOptional = this.userRepository.findById(id);
        if (userOptional.isEmpty()) {
            logger.error("User with id={} was not found in order to be deleted", id);
            throw new UserDoesNotExistException("The requested user does not exist");
        }
        User user = userOptional.get();
        logger.info("User with id={} was deleted", id);
        String jwtToken = jwtService.generateToken(user);
        ResponseEntity<String> response = this.syncService.deleteUserInDevicesMS(user.getId(), jwtToken);
        logger.info("{}", response.getBody());
        this.userRepository.delete(user);
        return "User with id= " + id + " was deleted successfully!";
    }

    @Transactional
    public String deleteUserByEmail(String email) throws UserDoesNotExistException {
        Optional<User> userOptional = this.userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            logger.error("User with email={} was not found in order to be deleted", email);
            throw new UserDoesNotExistException("The requested user does not exist");
        }
        User user = userOptional.get();
        this.userRepository.delete(user);
        logger.info("The user with email={} was deleted", email);
        return "User with email=" + email + " was deleted successfully!";
    }

    @Transactional
    public String deleteAll() throws UserDoesNotExistException {
        List<User> users = this.userRepository.findAll();
        if (users.isEmpty()) {
            logger.error("No users were found in the db");
            throw new UserDoesNotExistException("There are no users to be deleted");
        }
        this.userRepository.deleteAll();
        logger.info("All users were deleted");
        return "All users were deleted sucessfully";
    }

    @Transactional
    public UserDTO updateUserById(UUID id, UserUpdateDTO userDTO) {
        Optional<User> userOptional = this.userRepository.findById(id);
        if (userOptional.isEmpty()) {
            logger.error("User with id={} was not found", id);
            throw new UserDoesNotExistException("The requested user does not exist");
        }
        User user = userOptional.get();
        user = this.updateFieldsOfUser(user, userDTO);
        this.userRepository.save(user);
        logger.info("The user with id={} was updated", id);
        return userMapper.convertToDTO(user);
    }

    public User updateFieldsOfUser(User user, UserUpdateDTO userUpdateDTO) {
        if (userUpdateDTO.getFirstName() != null && !userUpdateDTO.getFirstName().isEmpty()) {
            user.setFirstName(userUpdateDTO.getFirstName());
        }

        if (userUpdateDTO.getSecondName() != null && !userUpdateDTO.getSecondName().isEmpty()) {
            user.setSecondName(userUpdateDTO.getSecondName());
        }

        if (userUpdateDTO.getEmail() != null && !userUpdateDTO.getEmail().isEmpty()) {
            user.setEmail(userUpdateDTO.getEmail());
        }

        if (userUpdateDTO.getPassword() != null && !userUpdateDTO.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(userUpdateDTO.getPassword());
            user.setPassword(encodedPassword);
        }

        if (userUpdateDTO.getRole() != null) {
            user.setRole(userUpdateDTO.getRole());
        }
        return user;
    }
}
