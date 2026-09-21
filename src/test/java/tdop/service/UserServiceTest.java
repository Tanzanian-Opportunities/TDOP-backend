package tdop.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tdop.entity.User;
import tdop.entity.enums.UserRole;
import tdop.exception.ResourceNotFoundException;
import tdop.repository.UserRepository;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testGetUserIdByEmail() {
        User user = User.builder().id(1L).email("test@test.com").fullName("Test").role(UserRole.SEEKER).build();
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        Long result = userService.getUserIdByEmail("test@test.com");
        assertEquals(1L, result);
    }

    @Test
    void testGetUserIdByEmailNotFound() {
        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
            userService.getUserIdByEmail("unknown@test.com"));
    }
}
