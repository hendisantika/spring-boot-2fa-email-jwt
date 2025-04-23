package id.my.hendisantika.twofaemailjwt.controller;

import id.my.hendisantika.twofaemailjwt.constant.OtpContext;
import id.my.hendisantika.twofaemailjwt.dto.LoginRequestDto;
import id.my.hendisantika.twofaemailjwt.dto.LoginSuccessDto;
import id.my.hendisantika.twofaemailjwt.dto.OtpVerificationRequestDto;
import id.my.hendisantika.twofaemailjwt.dto.SignupRequestDto;
import id.my.hendisantika.twofaemailjwt.dto.TokenRefreshRequestDto;
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

    @Test
    void userLoginHandler_ShouldDelegateToUserService() {
        // Arrange
        LoginRequestDto requestDto = LoginRequestDto.builder()
                .emailId("test@example.com")
                .password("password")
                .build();

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "OTP sent successfully");
        ResponseEntity<Map<String, String>> expectedResponse = ResponseEntity.ok(responseBody);

        // Use unchecked cast to avoid type inference issues
        when(userService.login(any(LoginRequestDto.class))).thenReturn((ResponseEntity) expectedResponse);

        // Act
        ResponseEntity<?> response = authenticationController.userLoginHandler(requestDto);

        // Assert
        verify(userService).login(requestDto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(expectedResponse.getBody(), response.getBody());
    }

    @Test
    void otpVerificationHandler_ShouldDelegateToUserService() {
        // Arrange
        OtpVerificationRequestDto requestDto = OtpVerificationRequestDto.builder()
                .emailId("test@example.com")
                .oneTimePassword(123456)
                .context(OtpContext.LOGIN)
                .build();

        LoginSuccessDto successDto = LoginSuccessDto.builder()
                .accessToken("access_token")
                .refreshToken("refresh_token")
                .build();
        ResponseEntity<LoginSuccessDto> expectedResponse = ResponseEntity.ok(successDto);

        // Use unchecked cast to avoid type inference issues
        when(userService.verifyOtp(any(OtpVerificationRequestDto.class))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<LoginSuccessDto> response = authenticationController.otpVerificationHandler(requestDto);

        // Assert
        verify(userService).verifyOtp(requestDto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(expectedResponse.getBody(), response.getBody());
    }

    @Test
    void tokenRefresherHandler_ShouldDelegateToUserService() {
        // Arrange
        TokenRefreshRequestDto requestDto = TokenRefreshRequestDto.builder()
                .refreshToken("refresh_token")
                .build();

        LoginSuccessDto successDto = LoginSuccessDto.builder()
                .accessToken("new_access_token")
                .refreshToken("refresh_token")
                .build();
        ResponseEntity<LoginSuccessDto> expectedResponse = ResponseEntity.ok(successDto);

        // Use unchecked cast to avoid type inference issues
        when(userService.refreshToken(any(TokenRefreshRequestDto.class))).thenReturn((ResponseEntity) expectedResponse);

        // Act
        ResponseEntity<?> response = authenticationController.tokenRefresherHandler(requestDto);

        // Assert
        verify(userService).refreshToken(requestDto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(expectedResponse.getBody(), response.getBody());
    }

}