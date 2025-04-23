package id.my.hendisantika.twofaemailjwt.mail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OtpCacheBeanTest {

    private static final int TEST_EXPIRATION_MINUTES = 10;

    @Mock
    private OneTimePasswordConfigurationProperties oneTimePasswordConfigurationProperties;

    @Mock
    private OneTimePasswordConfigurationProperties.OTP otp;

    @InjectMocks
    private OtpCacheBean otpCacheBean;

    @BeforeEach
    void setUp() {
        when(oneTimePasswordConfigurationProperties.getOtp()).thenReturn(otp);
        when(otp.getExpirationMinutes()).thenReturn(TEST_EXPIRATION_MINUTES);
    }
}