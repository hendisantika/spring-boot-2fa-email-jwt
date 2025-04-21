package id.my.hendisantika.twofaemailjwt.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class SignupRequestDto {
    @Email
    @NotBlank
    private final String emailId;

    @NotBlank
    private final String password;
}
