package id.my.hendisantika.twofaemailjwt.mail;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-2fa-email-jwt
 * User: hendisantika
 * Link: s.id/hendisantika
 * Email: hendisantika@yahoo.co.id
 * Telegram : @hendisantika34
 * Date: 22/04/25
 * Time: 07.16
 * To change this template use File | Settings | File Templates.
 */
@Configuration
@AllArgsConstructor
@EnableConfigurationProperties(OneTimePasswordConfigurationProperties.class)
public class OtpCacheBean {

    private final OneTimePasswordConfigurationProperties oneTimePasswordConfigurationProperties;

    @Bean
    public LoadingCache<String, Integer> oneTimePasswordCache() {
        return CacheBuilder.newBuilder()
                .expireAfterWrite(oneTimePasswordConfigurationProperties.getOtp().getExpirationMinutes(), TimeUnit.MINUTES)
                .build(new CacheLoader<>() {
                    @Override
                    public Integer load(String key) {
                        return 0; // Default value, not used in practice as we explicitly put values
                    }
                });
    }


}
