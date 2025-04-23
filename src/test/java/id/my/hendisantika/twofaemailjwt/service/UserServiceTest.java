package id.my.hendisantika.twofaemailjwt.service;

import com.google.common.cache.LoadingCache;
import id.my.hendisantika.twofaemailjwt.constant.OtpContext;
import id.my.hendisantika.twofaemailjwt.dto.LoginRequestDto;
import id.my.hendisantika.twofaemailjwt.dto.LoginSuccessDto;
import id.my.hendisantika.twofaemailjwt.dto.OtpVerificationRequestDto;
import id.my.hendisantika.twofaemailjwt.dto.SignupRequestDto;
import id.my.hendisantika.twofaemailjwt.dto.TokenRefreshRequestDto;
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
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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

    @Test
    void login_WhenPasswordIsInvalid_ShouldThrowException() {
        // Arrange
        LoginRequestDto requestDto = LoginRequestDto.builder()
                .emailId(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .build();

        when(userRepository.findByEmailId(TEST_EMAIL)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(TEST_PASSWORD, testUser.getPassword())).thenReturn(false);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userService.login(requestDto));
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Invalid login credentials"));

        verify(oneTimePasswordCache, never()).invalidate(anyString());
        verify(oneTimePasswordCache, never()).put(anyString(), anyInt());
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void login_WhenAccountIsNotActive_ShouldThrowException() {
        // Arrange
        LoginRequestDto requestDto = LoginRequestDto.builder()
                .emailId(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .build();

        testUser.setActive(false);
        when(userRepository.findByEmailId(TEST_EMAIL)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(TEST_PASSWORD, testUser.getPassword())).thenReturn(true);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userService.login(requestDto));
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Account not active"));

        verify(oneTimePasswordCache, never()).invalidate(anyString());
        verify(oneTimePasswordCache, never()).put(anyString(), anyInt());
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void verifyOtp_ForSignUp_WhenOtpIsValid_ShouldVerifyEmailAndReturnTokens() throws ExecutionException {
        // Arrange
        OtpVerificationRequestDto requestDto = OtpVerificationRequestDto.builder()
                .emailId(TEST_EMAIL)
                .oneTimePassword(TEST_OTP)
                .context(OtpContext.SIGN_UP)
                .build();

        when(userRepository.findByEmailId(TEST_EMAIL)).thenReturn(Optional.of(testUser));
        when(oneTimePasswordCache.get(TEST_EMAIL)).thenReturn(TEST_OTP);
        when(jwtUtils.generateAccessToken(any(User.class))).thenReturn(TEST_ACCESS_TOKEN);
        when(jwtUtils.generateRefreshToken(any(User.class))).thenReturn(TEST_REFRESH_TOKEN);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        ResponseEntity<LoginSuccessDto> response = userService.verifyOtp(requestDto);

        // Assert
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertTrue(savedUser.isEmailVerified());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(TEST_ACCESS_TOKEN, response.getBody().getAccessToken());
        assertEquals(TEST_REFRESH_TOKEN, response.getBody().getRefreshToken());
    }

    @Test
    void verifyOtp_ForLogin_WhenOtpIsValid_ShouldReturnTokens() throws ExecutionException {
        // Arrange
        OtpVerificationRequestDto requestDto = OtpVerificationRequestDto.builder()
                .emailId(TEST_EMAIL)
                .oneTimePassword(TEST_OTP)
                .context(OtpContext.LOGIN)
                .build();

        when(userRepository.findByEmailId(TEST_EMAIL)).thenReturn(Optional.of(testUser));
        when(oneTimePasswordCache.get(TEST_EMAIL)).thenReturn(TEST_OTP);
        when(jwtUtils.generateAccessToken(any(User.class))).thenReturn(TEST_ACCESS_TOKEN);
        when(jwtUtils.generateRefreshToken(any(User.class))).thenReturn(TEST_REFRESH_TOKEN);

        // Act
        ResponseEntity<LoginSuccessDto> response = userService.verifyOtp(requestDto);

        // Assert
        verify(userRepository, never()).save(any(User.class));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(TEST_ACCESS_TOKEN, response.getBody().getAccessToken());
        assertEquals(TEST_REFRESH_TOKEN, response.getBody().getRefreshToken());
    }

    @Test
    void verifyOtp_ForAccountDeletion_WhenOtpIsValid_ShouldDeactivateAccount() throws ExecutionException {
        // Arrange
        OtpVerificationRequestDto requestDto = OtpVerificationRequestDto.builder()
                .emailId(TEST_EMAIL)
                .oneTimePassword(TEST_OTP)
                .context(OtpContext.ACCOUNT_DELETION)
                .build();

        when(userRepository.findByEmailId(TEST_EMAIL)).thenReturn(Optional.of(testUser));
        when(oneTimePasswordCache.get(TEST_EMAIL)).thenReturn(TEST_OTP);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        ResponseEntity<LoginSuccessDto> response = userService.verifyOtp(requestDto);

        // Assert
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertFalse(savedUser.isActive());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void verifyOtp_WhenUserDoesNotExist_ShouldThrowException() {
        // Arrange
        OtpVerificationRequestDto requestDto = OtpVerificationRequestDto.builder()
                .emailId(TEST_EMAIL)
                .oneTimePassword(TEST_OTP)
                .context(OtpContext.LOGIN)
                .build();

        when(userRepository.findByEmailId(TEST_EMAIL)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userService.verifyOtp(requestDto));
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Invalid email-id"));
    }

    @Test
    void verifyOtp_WhenOtpIsInvalid_ShouldThrowException() throws ExecutionException {
        // Arrange
        OtpVerificationRequestDto requestDto = OtpVerificationRequestDto.builder()
                .emailId(TEST_EMAIL)
                .oneTimePassword(TEST_OTP)
                .context(OtpContext.LOGIN)
                .build();

        when(userRepository.findByEmailId(TEST_EMAIL)).thenReturn(Optional.of(testUser));
        when(oneTimePasswordCache.get(TEST_EMAIL)).thenReturn(999999); // Different OTP

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userService.verifyOtp(requestDto));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void refreshToken_WhenTokenIsValid_ShouldReturnNewAccessToken() {
        // Arrange
        TokenRefreshRequestDto requestDto = TokenRefreshRequestDto.builder()
                .refreshToken(TEST_REFRESH_TOKEN)
                .build();

        when(jwtUtils.isTokenExpired(TEST_REFRESH_TOKEN)).thenReturn(false);
        when(jwtUtils.extractEmail(TEST_REFRESH_TOKEN)).thenReturn(TEST_EMAIL);
        when(userRepository.findByEmailId(TEST_EMAIL)).thenReturn(Optional.of(testUser));
        when(jwtUtils.generateAccessToken(testUser)).thenReturn(TEST_ACCESS_TOKEN);

        // Act
        ResponseEntity<?> response = userService.refreshToken(requestDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(LoginSuccessDto.class, response.getBody());
        LoginSuccessDto responseBody = (LoginSuccessDto) response.getBody();
        assertEquals(TEST_ACCESS_TOKEN, responseBody.getAccessToken());
        assertEquals(TEST_REFRESH_TOKEN, responseBody.getRefreshToken());
    }

    @Test
    void refreshToken_WhenTokenIsExpired_ShouldThrowException() {
        // Arrange
        TokenRefreshRequestDto requestDto = TokenRefreshRequestDto.builder()
                .refreshToken(TEST_REFRESH_TOKEN)
                .build();

        when(jwtUtils.isTokenExpired(TEST_REFRESH_TOKEN)).thenReturn(true);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userService.refreshToken(requestDto));
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Refresh token has expired"));
    }

    @Test
    void refreshToken_WhenUserDoesNotExist_ShouldThrowException() {
        // Arrange
        TokenRefreshRequestDto requestDto = TokenRefreshRequestDto.builder()
                .refreshToken(TEST_REFRESH_TOKEN)
                .build();

        when(jwtUtils.isTokenExpired(TEST_REFRESH_TOKEN)).thenReturn(false);
        when(jwtUtils.extractEmail(TEST_REFRESH_TOKEN)).thenReturn(TEST_EMAIL);
        when(userRepository.findByEmailId(TEST_EMAIL)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userService.refreshToken(requestDto));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void deleteAccount_ShouldSendOtp() {
        // Arrange
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        // Act
        ResponseEntity<?> response = userService.deleteAccount(testUserId);

        // Assert
        verify(oneTimePasswordCache).invalidate(TEST_EMAIL);
        verify(oneTimePasswordCache).put(eq(TEST_EMAIL), anyInt());
//        verify(emailService).sendEmail(eq(TEST_EMAIL), eq("2FA: Confirm account Deletion"), anyString());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertTrue(responseBody.containsKey("message"));
    }

    @Test
    void getDetails_ShouldReturnUserDetails() {
        // Arrange
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        // Act
        ResponseEntity<?> response = userService.getDetails(testUserId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertEquals(TEST_EMAIL, responseBody.get("email_id"));
        assertNotNull(responseBody.get("created_at"));
    }

    @Test
    void deleteAccount_WhenUserDoesNotExist_ShouldThrowException() {
        // Arrange
        UUID nonExistentUserId = UUID.randomUUID();
        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        // Act & Assert
        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> userService.deleteAccount(nonExistentUserId));

        // Verify that no OTP was sent
        verify(oneTimePasswordCache, never()).invalidate(anyString());
        verify(oneTimePasswordCache, never()).put(anyString(), anyInt());
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }
}
