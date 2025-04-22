package id.my.hendisantika.twofaemailjwt.utility;

import id.my.hendisantika.twofaemailjwt.entity.User;
import lombok.experimental.UtilityClass;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-2fa-email-jwt
 * User: hendisantika
 * Link: s.id/hendisantika
 * Email: hendisantika@yahoo.co.id
 * Telegram : @hendisantika34
 * Date: 22/04/25
 * Time: 07.05
 * To change this template use File | Settings | File Templates.
 */
@UtilityClass
public class SecurityUtils {
    public org.springframework.security.core.userdetails.User convert(User user) {
        return new org.springframework.security.core.userdetails.User(user.getEmailId(), user.getPassword(), List.of());
    }
}
