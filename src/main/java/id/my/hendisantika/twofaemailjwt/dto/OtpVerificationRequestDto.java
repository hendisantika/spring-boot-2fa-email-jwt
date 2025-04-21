package id.my.hendisantika.twofaemailjwt.dto;

import id.my.hendisantika.twofaemailjwt.constant.OtpContext;
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
 * Time: 06.47
 * To change this template use File | Settings | File Templates.
 */
@Getter
@Builder
@Jacksonized
public class OtpVerificationRequestDto {
    private final String emailId;
    private final Integer oneTimePassword;
    private final OtpContext context;
}
