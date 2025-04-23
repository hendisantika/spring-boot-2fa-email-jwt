package id.my.hendisantika.twofaemailjwt.controller;

import id.my.hendisantika.twofaemailjwt.service.UserService;
import id.my.hendisantika.twofaemailjwt.utility.JwtUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private UserController userController;

    @Test
    void userAccountDeletionHandler_ShouldDelegateToUserService() {
        // Arrange
        String authHeader = "Bearer jwt_token";
        UUID userId = UUID.randomUUID();

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "OTP sent successfully");
        ResponseEntity<Map<String, String>> expectedResponse = ResponseEntity.ok(responseBody);

        when(jwtUtils.extractUserId(authHeader)).thenReturn(userId);
        // Use unchecked cast to avoid type inference issues
        when(userService.deleteAccount(userId)).thenReturn((ResponseEntity) expectedResponse);

        // Act
        ResponseEntity<?> response = userController.userAccountDeletionHandler(authHeader);

        // Assert
        verify(jwtUtils).extractUserId(authHeader);
        verify(userService).deleteAccount(userId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(expectedResponse.getBody(), response.getBody());
    }
}
