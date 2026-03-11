package com.example.bankcards.service;

import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.security.JwtAuthenticationDto;
import com.example.bankcards.dto.security.RefreshTokenDto;
import com.example.bankcards.dto.security.UserCredentialsDto;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    private User dtoToEntity(UserDto dto) {
        User user = new User();
        user.setUserName(dto.getUserName());
        user.setPassword(dto.getPassword());
        user.setAge(dto.getAge());
        if (dto.getRole() != null) {
            user.setRole(Role.fromString(dto.getRole()));
        }
        return user;
    }

    private UserDto entityToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUserName(user.getUserName());
        dto.setPassword(passwordEncoder.encode(user.getPassword()));
        dto.setAge(user.getAge());
        dto.setRole(user.getRole().toValue());
        return dto;
    }

    private User findByCredentials(UserCredentialsDto userCredentialsDto) throws AuthenticationException {
        Optional<User> optionalUser = userRepository.findByUserName(userCredentialsDto.getUserName());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (passwordEncoder.matches(userCredentialsDto.getPassword(), user.getPassword())) {
                return user;
            }
        }
        throw new AuthenticationException("UserName or password not correct");
    }

    private User findByUserName(String userName) throws Exception {
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new Exception(String.format("User not found with name " + userName)));
    }

    public void createUser(UserDto userDto) {
        log.info("Start creating user");
        User user = dtoToEntity(userDto);
        User savedUser = userRepository.save(user);
        entityToDto(savedUser);
    }

    public UserDto getUserById(Long id) {
        log.info("Get user by id: {}", id);
        return userRepository.findById(id)
                .map(this::entityToDto)
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }

    public List<UserDto> getAllUsers(int page, int pageSize) {
        log.info("Get all users");
        return userRepository.findAll(PageRequest.of(page, pageSize)).stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    public void updateUser(Long id, UserDto userDto) {
        log.info("Updating user with id {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));

        user.setUserName(userDto.getUserName());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setAge(userDto.getAge());

        if (userDto.getRole() != null) {
            user.setRole(Role.fromString(userDto.getRole()));
        }

        userRepository.save(user);
    }

    public void deleteUser(Long id) {
        log.info("Deleting user with id {}", id);

        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id " + id);
        }

        userRepository.deleteById(id);
    }


    public JwtAuthenticationDto signIn(UserCredentialsDto userCredentialsDto) throws AuthenticationException {
        User user = findByCredentials(userCredentialsDto);
        return jwtService.generateAuthToken(user.getUserName());
    }

    public JwtAuthenticationDto refreshToken(RefreshTokenDto refreshTokenDto) throws Exception {
        String refreshToken = refreshTokenDto.getRefreshToken();
        if (refreshToken != null && jwtService.validateJwtToken(refreshToken)) {
            User user = findByUserName(jwtService.getUserNameFromToken(refreshToken));
            return jwtService.refreshBaseToken(user.getUserName(), refreshToken);
        }
        throw new AuthenticationException("Invalid refresh token");
    }
}
