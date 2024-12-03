package com.userMS.UserMicroService.services;

import com.userMS.UserMicroService.dtos.authDTOs.AuthRequestDTO;
import com.userMS.UserMicroService.dtos.authDTOs.AuthResponseDTO;
import com.userMS.UserMicroService.dtos.authDTOs.RegisterRequestDTO;
import com.userMS.UserMicroService.dtos.mappers.AuthMapper;
import com.userMS.UserMicroService.entities.User;
import com.userMS.UserMicroService.exceptions.InvalidCredentialsException;
import com.userMS.UserMicroService.exceptions.UserAlreadyExistsException;
import com.userMS.UserMicroService.jwt.JwtService;
import com.userMS.UserMicroService.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private final AuthMapper authMapper;

    private final SyncService syncService;

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    public AuthResponseDTO register(RegisterRequestDTO registerRequestDTO) throws UserAlreadyExistsException {

        if (this.userRepository.findByEmail(registerRequestDTO.getEmail()).isPresent()) {
            logger.error("Invalid register attempt, user exists: {}", registerRequestDTO.getEmail());
            throw new UserAlreadyExistsException("An user already exists with this email");
        }

        User user = this.authMapper.convertToEntity(registerRequestDTO);
        this.userRepository.save(user);
        String jwtToken = jwtService.generateToken(user);

        logger.info("User {} registered successfully", registerRequestDTO.getEmail());
        ResponseEntity<String> response = this.syncService.createUserInDeviceMS(user.getId(), jwtToken);
        logger.info("User {} was added in the device MS also", response.getBody());
        return this.authMapper.convertToDTOResp(jwtToken);
    }

    public AuthResponseDTO login(AuthRequestDTO authRequestDTO) throws InvalidCredentialsException {

        this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequestDTO.getEmail(), authRequestDTO.getPassword())
        );

        User user = this.userRepository.findByEmail(authRequestDTO.getEmail())
                .orElseThrow(() -> {
                    logger.error("Invalid login attempt for email: {}", authRequestDTO.getEmail());
                    return new InvalidCredentialsException("Invalid email or password");
                });

        String jwtToken = jwtService.generateToken(user);

        logger.info("User {} authenticated successfully", authRequestDTO.getEmail());
        return this.authMapper.convertToDTOResp(jwtToken);
    }
}
