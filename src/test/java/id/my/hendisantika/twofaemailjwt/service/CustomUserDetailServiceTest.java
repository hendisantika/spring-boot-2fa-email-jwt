package id.my.hendisantika.twofaemailjwt.service;

import id.my.hendisantika.twofaemailjwt.entity.User;
import id.my.hendisantika.twofaemailjwt.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailService customUserDetailService;

    @Test
    void loadUserByUsername_WhenUserExists_ShouldReturnUserDetails() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String userIdString = userId.toString();

        User user = new User();
        user.setId(userId);
        user.setEmailId("test@example.com");
        user.setPassword("encoded_password");
        user.setEmailVerified(true);
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now(ZoneId.of("+07:00")));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = customUserDetailService.loadUserByUsername(userIdString);

        // Assert
        assertNotNull(userDetails);
        assertEquals(user.getEmailId(), userDetails.getUsername());
        assertEquals(user.getPassword(), userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().isEmpty());
    }

    @Test
    void loadUserByUsername_WhenUserDoesNotExist_ShouldThrowException() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String userIdString = userId.toString();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailService.loadUserByUsername(userIdString);
        });

        assertEquals("Bad Credentials", exception.getMessage());
    }

}