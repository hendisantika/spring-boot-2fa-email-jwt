package id.my.hendisantika.twofaemailjwt.service;

import com.google.common.cache.LoadingCache;
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
}
