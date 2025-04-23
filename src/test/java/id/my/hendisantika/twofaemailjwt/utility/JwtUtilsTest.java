package id.my.hendisantika.twofaemailjwt.utility;

import id.my.hendisantika.twofaemailjwt.config.jwt.JwtConfigurationProperties;
import id.my.hendisantika.twofaemailjwt.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
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

    @Test
    void isTokenExpired_ShouldReturnFalseForValidToken() {
        // Arrange
        String token = jwtUtils.generateAccessToken(testUser);

        // Act
        boolean isExpired = jwtUtils.isTokenExpired(token);

        // Assert
        assertFalse(isExpired);
    }

    @Test
    void isTokenExpired_ShouldReturnTrueForExpiredToken() {
        // Arrange
        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder()
                .setSubject(TEST_EMAIL)
                .setIssuedAt(new Date(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(2)))
                .setExpiration(new Date(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1)))
                .signWith(key)
                .compact();

        // Act
        boolean isExpired = jwtUtils.isTokenExpired(token);

        // Assert
        assertTrue(isExpired);
    }

    @Test
    void validateToken_ShouldReturnTrueForValidToken() {
        // Arrange
        String token = jwtUtils.generateAccessToken(testUser);
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(TEST_EMAIL);

        // Act
        boolean isValid = jwtUtils.validateToken(token, userDetails);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void validateToken_ShouldReturnFalseForInvalidUsername() {
        // Arrange
        String token = jwtUtils.generateAccessToken(testUser);
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("wrong@example.com");

        // Act
        boolean isValid = jwtUtils.validateToken(token, userDetails);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateToken_ShouldReturnFalseForExpiredToken() {
        // Arrange
        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder()
                .setSubject(TEST_EMAIL)
                .setIssuedAt(new Date(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(2)))
                .setExpiration(new Date(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1)))
                .signWith(key)
                .compact();

        UserDetails userDetails = mock(UserDetails.class);
        // Removed unnecessary stubbing

        // Act
        boolean isValid = jwtUtils.validateToken(token, userDetails);

        // Assert
        assertFalse(isValid);
    }
}
