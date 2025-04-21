package id.my.hendisantika.twofaemailjwt.config;

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
 * Time: 06.49
 * To change this template use File | Settings | File Templates.
 */
@Data
@ConfigurationProperties(prefix = "id.my.hendisantika.twofaemailjwt.config")
public class OpenApiConfigurationProperties {

    private Swagger swagger = new Swagger();

    @Data
    public static class Swagger {

        private String title;
        private String description;
        private String apiVersion;
        private Contact contact = new Contact();
        private Security security = new Security();

        @Data
        public static class Contact {

            private String email;
            private String name;
            private String url;
        }

        @Data
        public static class Security {

            private String name;
            private String scheme;
            private String bearerFormat;
        }
    }
}
