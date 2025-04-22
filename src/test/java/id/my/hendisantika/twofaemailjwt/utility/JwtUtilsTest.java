package id.my.hendisantika.twofaemailjwt.utility;

import id.my.hendisantika.twofaemailjwt.config.jwt.JwtConfigurationProperties;
import id.my.hendisantika.twofaemailjwt.entity.User;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class JwtUtilsTest {

    private static final String SECRET_KEY = "D!gTpB9z@3K#x6&JhQm^A4z$Y8Lu)S1*V5cW+N0e$2b7^M!r";
    private static final String TEST_EMAIL = "test@example.com";

    @Mock
    private JwtConfigurationProperties jwtConfigurationProperties;

    @Mock
    private JwtConfigurationProperties.JWT jwt;

    @InjectMocks
    private JwtUtils jwtUtils;

    private User testUser;


}