package id.my.hendisantika.twofaemailjwt.service;

import com.google.common.cache.LoadingCache;
import id.my.hendisantika.twofaemailjwt.constant.OtpContext;
import id.my.hendisantika.twofaemailjwt.dto.LoginRequestDto;
import id.my.hendisantika.twofaemailjwt.dto.LoginSuccessDto;
import id.my.hendisantika.twofaemailjwt.dto.OtpVerificationRequestDto;
import id.my.hendisantika.twofaemailjwt.dto.SignupRequestDto;
import id.my.hendisantika.twofaemailjwt.entity.User;
import id.my.hendisantika.twofaemailjwt.repository.UserRepository;
import id.my.hendisantika.twofaemailjwt.utility.JwtUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.ExecutionException;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-2fa-email-jwt
 * User: hendisantika
 * Link: s.id/hendisantika
 * Email: hendisantika@yahoo.co.id
 * Telegram : @hendisantika34
 * Date: 22/04/25
 * Time: 07.08
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoadingCache<String, Integer> oneTimePasswordCache;
    private final EmailService emailService;
    private final JwtUtils jwtUtils;

    public ResponseEntity<?> createAccount(
            final SignupRequestDto userAccountCreationRequestDto) {
        if (userRepository.existsByEmailId(userAccountCreationRequestDto.getEmailId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "User account already exists for provided email-id");
        }

        final var user = new User();
        user.setEmailId(userAccountCreationRequestDto.getEmailId());
        user.setPassword(passwordEncoder.encode(userAccountCreationRequestDto.getPassword()));
        user.setEmailVerified(false);
        user.setActive(true);
        final var savedUser = userRepository.save(user);

        sendOtp(savedUser, "Verify your account");
        return ResponseEntity.ok(getOtpSendMessage());
    }

    public ResponseEntity<?> login(final LoginRequestDto userLoginRequestDto) {
        final User user = userRepository.findByEmailId(userLoginRequestDto.getEmailId())
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid login credentials"));

        if (!passwordEncoder.matches(userLoginRequestDto.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid login credentials");
        }

        if (!user.isActive()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account not active");
        }

        sendOtp(user, "2FA: Request to log in to your account");
        return ResponseEntity.ok(getOtpSendMessage());
    }

    public ResponseEntity<LoginSuccessDto> verifyOtp(
            final OtpVerificationRequestDto otpVerificationRequestDto) {
        User user = userRepository.findByEmailId(otpVerificationRequestDto.getEmailId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email-id"));

        Integer storedOneTimePassword = null;
        try {
            storedOneTimePassword = oneTimePasswordCache.get(user.getEmailId());
        } catch (ExecutionException e) {
            log.error("FAILED TO FETCH PAIR FROM OTP CACHE: ", e);
            throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
        }

        if (storedOneTimePassword.equals(otpVerificationRequestDto.getOneTimePassword())) {
            if (otpVerificationRequestDto.getContext().equals(OtpContext.SIGN_UP)) {
                user.setEmailVerified(true);
                user = userRepository.save(user);
                return ResponseEntity
                        .ok(LoginSuccessDto.builder().accessToken(jwtUtils.generateAccessToken(user))
                                .refreshToken(jwtUtils.generateRefreshToken(user)).build());
            } else if (otpVerificationRequestDto.getContext().equals(OtpContext.LOGIN)) {
                return ResponseEntity
                        .ok(LoginSuccessDto.builder().accessToken(jwtUtils.generateAccessToken(user))
                                .refreshToken(jwtUtils.generateRefreshToken(user)).build());
            } else if (otpVerificationRequestDto.getContext().equals(OtpContext.ACCOUNT_DELETION)) {
                user.setActive(false);
                user = userRepository.save(user);
                return ResponseEntity.ok().build();
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
}
