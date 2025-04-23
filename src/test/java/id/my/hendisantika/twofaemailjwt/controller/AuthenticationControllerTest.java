package id.my.hendisantika.twofaemailjwt.controller;

import id.my.hendisantika.twofaemailjwt.dto.SignupRequestDto;
import id.my.hendisantika.twofaemailjwt.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticationControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @Test
    void userAccountCreationHandler_ShouldDelegateToUserService() {
        // Arrange
        SignupRequestDto requestDto = SignupRequestDto.builder()
                .emailId("test@example.com")
                .password("password")
                .build();

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "OTP sent successfully");
        ResponseEntity<Map<String, String>> expectedResponse = ResponseEntity.ok(responseBody);

        // Use unchecked cast to avoid type inference issues
        when(userService.createAccount(any(SignupRequestDto.class))).thenReturn((ResponseEntity) expectedResponse);

        // Act
        ResponseEntity<?> response = authenticationController.userAccountCreationHandler(requestDto);

        // Assert
        verify(userService).createAccount(requestDto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(expectedResponse.getBody(), response.getBody());
    }
}