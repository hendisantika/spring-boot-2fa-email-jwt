package id.my.hendisantika.twofaemailjwt.controller;

import id.my.hendisantika.twofaemailjwt.service.UserService;
import id.my.hendisantika.twofaemailjwt.utility.JwtUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-2fa-email-jwt
 * User: hendisantika
 * Link: s.id/hendisantika
 * Email: hendisantika@yahoo.co.id
 * Telegram : @hendisantika34
 * Date: 22/04/25
 * Time: 07.19
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
@RestController
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtils jwtUtils;
}
