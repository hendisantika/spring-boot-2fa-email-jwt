package id.my.hendisantika.twofaemailjwt.service;

import com.google.common.cache.LoadingCache;
import id.my.hendisantika.twofaemailjwt.dto.LoginRequestDto;
import id.my.hendisantika.twofaemailjwt.dto.SignupRequestDto;
import id.my.hendisantika.twofaemailjwt.entity.User;
import id.my.hendisantika.twofaemailjwt.mail.EmailService;
import id.my.hendisantika.twofaemailjwt.repository.UserRepository;
import id.my.hendisantika.twofaemailjwt.utility.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "password";
    private static final int TEST_OTP = 123456;
    private static final String TEST_ACCESS_TOKEN = "access_token";
    private static final String TEST_REFRESH_TOKEN = "refresh_token";

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private LoadingCache<String, Integer> oneTimePasswordCache;

    @Mock
    private EmailService emailService;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private User testUser;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testUser = new User();
        testUser.setId(testUserId);
        testUser.setEmailId(TEST_EMAIL);
        testUser.setPassword("encoded_password");
        testUser.setEmailVerified(true);
        testUser.setActive(true);
        testUser.setCreatedAt(LocalDateTime.now(ZoneId.of("+07:00")));
    }

    @Test
    void createAccount_WhenEmailDoesNotExist_ShouldCreateUserAndSendOtp() {
        // Arrange
        SignupRequestDto requestDto = SignupRequestDto.builder()
                .emailId(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .build();

        when(userRepository.existsByEmailId(TEST_EMAIL)).thenReturn(false);
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        ResponseEntity<?> response = userService.createAccount(requestDto);

        // Assert
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals(TEST_EMAIL, savedUser.getEmailId());
        assertEquals("encoded_password", savedUser.getPassword());
        assertFalse(savedUser.isEmailVerified());
        assertTrue(savedUser.isActive());

        verify(oneTimePasswordCache).invalidate(TEST_EMAIL);
        verify(oneTimePasswordCache).put(eq(TEST_EMAIL), anyInt());
        verify(emailService).sendEmail(eq(TEST_EMAIL), eq("Verify your account"), anyString());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertTrue(responseBody.containsKey("message"));
    }

    @Test
    void createAccount_WhenEmailExists_ShouldThrowException() {
        // Arrange
        SignupRequestDto requestDto = SignupRequestDto.builder()
                .emailId(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .build();

        when(userRepository.existsByEmailId(TEST_EMAIL)).thenReturn(true);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userService.createAccount(requestDto));
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertTrue(exception.getReason().contains("User account already exists"));

        verify(userRepository, never()).save(any(User.class));
        verify(oneTimePasswordCache, never()).invalidate(anyString());
        verify(oneTimePasswordCache, never()).put(anyString(), anyInt());
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void login_WhenCredentialsAreValid_ShouldSendOtp() {
        // Arrange
        LoginRequestDto requestDto = LoginRequestDto.builder()
                .emailId(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .build();

        when(userRepository.findByEmailId(TEST_EMAIL)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(TEST_PASSWORD, testUser.getPassword())).thenReturn(true);

        // Act
        ResponseEntity<?> response = userService.login(requestDto);

        // Assert
        verify(oneTimePasswordCache).invalidate(TEST_EMAIL);
        verify(oneTimePasswordCache).put(eq(TEST_EMAIL), anyInt());
        verify(emailService).sendEmail(eq(TEST_EMAIL), eq("2FA: Request to log in to your account"), anyString());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertTrue(responseBody.containsKey("message"));
    }

    @Test
    void login_WhenUserDoesNotExist_ShouldThrowException() {
        // Arrange
        LoginRequestDto requestDto = LoginRequestDto.builder()
                .emailId(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .build();

        when(userRepository.findByEmailId(TEST_EMAIL)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userService.login(requestDto));
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Invalid login credentials"));

        verify(oneTimePasswordCache, never()).invalidate(anyString());
        verify(oneTimePasswordCache, never()).put(anyString(), anyInt());
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }
}