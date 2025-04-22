package id.my.hendisantika.twofaemailjwt.mail;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-2fa-email-jwt
 * User: hendisantika
 * Link: s.id/hendisantika
 * Email: hendisantika@yahoo.co.id
 * Telegram : @hendisantika34
 * Date: 22/04/25
 * Time: 07.13
 * To change this template use File | Settings | File Templates.
 */
@Data
@ConfigurationProperties(prefix = "id.my.hendisantika.twofaemailjwt")
public class OneTimePasswordConfigurationProperties {

    private OTP otp = new OTP();

    @Data
    public class OTP {

        private Integer expirationMinutes;
    }
}
