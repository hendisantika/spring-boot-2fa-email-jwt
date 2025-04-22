package id.my.hendisantika.twofaemailjwt.mail;

import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-2fa-email-jwt
 * User: hendisantika
 * Link: s.id/hendisantika
 * Email: hendisantika@yahoo.co.id
 * Telegram : @hendisantika34
 * Date: 22/04/25
 * Time: 07.12
 * To change this template use File | Settings | File Templates.
 */
@Service
@AllArgsConstructor
@EnableConfigurationProperties(EmailConfigurationProperties.class)
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final EmailConfigurationProperties emailConfigurationProperties;

}
