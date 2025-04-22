package id.my.hendisantika.twofaemailjwt.service;

import com.google.common.cache.LoadingCache;
import id.my.hendisantika.twofaemailjwt.repository.UserRepository;
import id.my.hendisantika.twofaemailjwt.utility.JwtUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

}
