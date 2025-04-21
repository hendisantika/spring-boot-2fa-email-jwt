package id.my.hendisantika.twofaemailjwt.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-2fa-email-jwt
 * User: hendisantika
 * Link: s.id/hendisantika
 * Email: hendisantika@yahoo.co.id
 * Telegram : @hendisantika34
 * Date: 22/04/25
 * Time: 06.58
 * To change this template use File | Settings | File Templates.
 */
@Getter
@AllArgsConstructor
public enum ApiPathExclusion {
    SWAGGER_API_V2_DOCS("/v2/api-docs"),
    SWAGGER_RESOURCE_CONFIGURATION("/swagger-resources/configuration/ui"),
    SWAGGER_RESOURCES("/swagger-resources"),
    SWAGGER_RESOURCES_SECURITY_CONFIGURATION("/swagger-resources/configuration/security"),
    SWAGGER_UI_HTML("swagger-ui.html"),
    WEBJARS("/webjars/**"),
    SWAGGER_UI("/swagger-ui/**"),
    SWAGGER_API_V3_DOCS("/v3/api-docs/**"),
    SWAGGER_CONFIGURATION("/configuration/**"),
    SWAGGER("/swagger*/**"),
    HEALTH_CHECK("/health-check"),
    ACTUATOR("/actuator/**"),
    LOGIN("/login"),
    SIGN_UP("/sign-up"),
    OTP_VERIFICATION("/verify-otp"),
    REFRESH_TOKEN("/refresh-token");

    private final String path;
}
