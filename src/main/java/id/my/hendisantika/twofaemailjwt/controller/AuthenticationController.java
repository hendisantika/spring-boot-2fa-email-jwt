package id.my.hendisantika.twofaemailjwt.controller;

import id.my.hendisantika.twofaemailjwt.dto.LoginRequestDto;
import id.my.hendisantika.twofaemailjwt.dto.LoginSuccessDto;
import id.my.hendisantika.twofaemailjwt.dto.OtpVerificationRequestDto;
import id.my.hendisantika.twofaemailjwt.dto.SignupRequestDto;
import id.my.hendisantika.twofaemailjwt.dto.TokenRefreshRequestDto;
import id.my.hendisantika.twofaemailjwt.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-2fa-email-jwt
 * User: hendisantika
 * Link: s.id/hendisantika
 * Email: hendisantika@yahoo.co.id
 * Telegram : @hendisantika34
 * Date: 22/04/25
 * Time: 07.16
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
@RestController
@AllArgsConstructor
public class AuthenticationController {

    private final UserService userService;

    @PostMapping(value = "/sign-up", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(summary = "Creates a user account in the system")
    public ResponseEntity<?> userAccountCreationHandler(
            @RequestBody(required = true) final SignupRequestDto userAccountCreationRequestDto) {
        return userService.createAccount(userAccountCreationRequestDto);
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(summary = "Endpoint to authenticate users credentials")
    public ResponseEntity<?> userLoginHandler(
            @RequestBody(required = true) final LoginRequestDto userLoginRequestDto) {
        return userService.login(userLoginRequestDto);
    }

    @PostMapping(value = "/verify-otp", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(summary = "verifies OTP and returns JWT corresponding to the user")
    public ResponseEntity<LoginSuccessDto> otpVerificationHandler(
            @RequestBody(required = true) final OtpVerificationRequestDto otpVerificationRequestDto) {
        return userService.verifyOtp(otpVerificationRequestDto);
    }

    @PutMapping(value = "/refresh-token", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(summary = "Returns new access_token")
    public ResponseEntity<?> tokenRefresherHandler(
            @RequestBody(required = true) final TokenRefreshRequestDto tokenRefreshRequestDto) {
        return userService.refreshToken(tokenRefreshRequestDto);
    }
}
