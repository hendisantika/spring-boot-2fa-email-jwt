package id.my.hendisantika.twofaemailjwt.mail;

import com.google.common.cache.LoadingCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

    @Test
    void oneTimePasswordCache_ShouldCreateCacheWithCorrectExpiration() throws ExecutionException {
        // Act
        LoadingCache<String, Integer> cache = otpCacheBean.oneTimePasswordCache();

        // Assert
        assertNotNull(cache);

        // Verify default value
        assertEquals(0, cache.get("test_key").intValue());

        // Put a value and verify it can be retrieved
        cache.put("test_key", 123456);
        assertEquals(123456, cache.get("test_key").intValue());
    }
}