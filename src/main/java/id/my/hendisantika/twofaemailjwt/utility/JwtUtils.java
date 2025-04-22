package id.my.hendisantika.twofaemailjwt.utility;

import id.my.hendisantika.twofaemailjwt.config.jwt.JwtConfigurationProperties;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-2fa-email-jwt
 * User: hendisantika
 * Link: s.id/hendisantika
 * Email: hendisantika@yahoo.co.id
 * Telegram : @hendisantika34
 * Date: 22/04/25
 * Time: 07.02
 * To change this template use File | Settings | File Templates.
 */
@Component
@AllArgsConstructor
@EnableConfigurationProperties(JwtConfigurationProperties.class)
public class JwtUtils {

    private final JwtConfigurationProperties jwtConfigurationProperties;
}
