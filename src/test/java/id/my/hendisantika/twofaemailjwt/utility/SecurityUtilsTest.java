package id.my.hendisantika.twofaemailjwt.utility;

import id.my.hendisantika.twofaemailjwt.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SecurityUtilsTest {

    @Test
    void convert_ShouldConvertUserToUserDetails() {
        // Arrange
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmailId("test@example.com");
        user.setPassword("encoded_password");
        user.setEmailVerified(true);
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now(ZoneId.of("+07:00")));

        // Act
        UserDetails userDetails = SecurityUtils.convert(user);

        // Assert
        assertEquals(user.getEmailId(), userDetails.getUsername());
        assertEquals(user.getPassword(), userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().isEmpty());
    }
}