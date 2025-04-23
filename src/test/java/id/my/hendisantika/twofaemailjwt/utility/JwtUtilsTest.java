package id.my.hendisantika.twofaemailjwt.utility;

import id.my.hendisantika.twofaemailjwt.config.jwt.JwtConfigurationProperties;
import id.my.hendisantika.twofaemailjwt.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtUtilsTest {

    private static final String SECRET_KEY = "D!gTpB9z@3K#x6&JhQm^A4z$Y8Lu)S1*V5cW+N0e$2b7^M!r";
    private static final String TEST_EMAIL = "test@example.com";

    @Mock
    private JwtConfigurationProperties jwtConfigurationProperties;

    @Mock
    private JwtConfigurationProperties.JWT jwt;

    @InjectMocks
    private JwtUtils jwtUtils;

    private User testUser;

    @BeforeEach
    void setUp() {
        when(jwtConfigurationProperties.getJwt()).thenReturn(jwt);
        when(jwt.getSecretKey()).thenReturn(SECRET_KEY);

        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setEmailId(TEST_EMAIL);
        testUser.setPassword("password");
        testUser.setEmailVerified(true);
        testUser.setActive(true);
        testUser.setCreatedAt(LocalDateTime.now(ZoneId.of("+07:00")));
    }

    @Test
    void generateAccessToken_ShouldCreateValidToken() {
        // Act
        String token = jwtUtils.generateAccessToken(testUser);

        // Assert
        assertNotNull(token);
        assertEquals(TEST_EMAIL, jwtUtils.extractEmail(token));
        assertEquals(testUser.getId().toString(), jwtUtils.extractClaim(token, claims -> claims.get("user_id").toString()));
        assertFalse(jwtUtils.isTokenExpired(token));
    }

    @Test
    void generateRefreshToken_ShouldCreateValidToken() {
        // Act
        String token = jwtUtils.generateRefreshToken(testUser);

        // Assert
        assertNotNull(token);
        assertEquals(TEST_EMAIL, jwtUtils.extractEmail(token));
        assertFalse(jwtUtils.isTokenExpired(token));
    }

    @Test
    void extractEmail_ShouldReturnCorrectEmail() {
        // Arrange
        String token = jwtUtils.generateAccessToken(testUser);

        // Act
        String email = jwtUtils.extractEmail(token);

        // Assert
        assertEquals(TEST_EMAIL, email);
    }

    @Test
    void extractUserId_ShouldReturnCorrectUserId() {
        // Arrange
        String token = jwtUtils.generateAccessToken(testUser);

        // Act
        UUID userId = jwtUtils.extractUserId(token);

        // Assert
        assertEquals(testUser.getId(), userId);
    }
}