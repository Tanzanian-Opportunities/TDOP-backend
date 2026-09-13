package tdop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tdop.entity.User;
import tdop.entity.enums.UserRole;
import tdop.exception.ResourceNotFoundException;
import tdop.repository.UserRepository;
import tdop.dto.response.UserResponse;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return toResponse(user);
    }

    public Long getUserIdByEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return user.getId();
    }

    public UserResponse suspendUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setEnabled(false);
        userRepository.save(user);
        return toResponse(user);
    }

    public UserResponse updateRole(Long id, UserRole role) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setRole(role);
        userRepository.save(user);
        return toResponse(user);
    }

    public UserResponse toResponse(User user) {
        return UserResponse.builder().id(user.getId()).email(user.getEmail())
            .fullName(user.getFullName()).phone(user.getPhone())
            .role(user.getRole().name()).enabled(user.isEnabled())
            .verified(user.isVerified()).build();
    }
}
