package id.my.hendisantika.twofaemailjwt.config.jwt;

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
 * Time: 06.55
 * To change this template use File | Settings | File Templates.
 */
@Data
@ConfigurationProperties(prefix = "id.my.hendisantika.twofaemailjwt.config")
public class JwtConfigurationProperties {

    private JWT jwt = new JWT();

    @Data
    public class JWT {

        private String secretKey;
    }
}
