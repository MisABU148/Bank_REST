package com.example.bankcards.service;

import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    private User dtoToEntity(UserDto dto) {
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setAge(dto.getAge());
        if (dto.getRole() != null) {
            user.setRole(Role.fromString(dto.getRole()));
        }
        return user;
    }

    private UserDto entityToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setAge(user.getAge());
        dto.setRole(user.getRole().toValue());
        return dto;
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

        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
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
}
