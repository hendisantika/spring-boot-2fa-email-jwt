package id.my.hendisantika.twofaemailjwt.config.swagger;

import id.my.hendisantika.twofaemailjwt.config.OpenApiConfigurationProperties;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-2fa-email-jwt
 * User: hendisantika
 * Link: s.id/hendisantika
 * Email: hendisantika@yahoo.co.id
 * Telegram : @hendisantika34
 * Date: 22/04/25
 * Time: 06.53
 * To change this template use File | Settings | File Templates.
 */
@Configuration
@EnableConfigurationProperties(OpenApiConfigurationProperties.class)
@AllArgsConstructor
public class OpenApiConfiguration {

    private final OpenApiConfigurationProperties openApiConfigurationProperties;

    @Bean
    public OpenAPI customOpenAPI() {
        final var properties = openApiConfigurationProperties.getSwagger();
        final var security = properties.getSecurity();
        final var contact = properties.getContact();
        final var info = new Info().title(properties.getTitle())
                .version(properties.getApiVersion())
                .description(properties.getDescription())
                .contact(new Contact()
                        .email(contact.getEmail())
                        .name(contact.getName())
                        .url(contact.getUrl()));

        return new OpenAPI().info(info)
                .addSecurityItem(new SecurityRequirement().addList(security.getName()))
                .components(new Components().addSecuritySchemes(security.getName(),
                        new SecurityScheme().name(security.getName())
                                .type(SecurityScheme.Type.HTTP)
                                .scheme(security.getScheme())
                                .bearerFormat(security.getBearerFormat())));
    }
}
