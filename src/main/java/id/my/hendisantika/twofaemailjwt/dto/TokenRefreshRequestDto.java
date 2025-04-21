package id.my.hendisantika.twofaemailjwt.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-2fa-email-jwt
 * User: hendisantika
 * Link: s.id/hendisantika
 * Email: hendisantika@yahoo.co.id
 * Telegram : @hendisantika34
 * Date: 22/04/25
 * Time: 06.48
 * To change this template use File | Settings | File Templates.
 */
@Getter
@Builder
@Jacksonized
public class TokenRefreshRequestDto {
    private final String refreshToken;
}
