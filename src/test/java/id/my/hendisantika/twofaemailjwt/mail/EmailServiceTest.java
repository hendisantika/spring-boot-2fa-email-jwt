package id.my.hendisantika.twofaemailjwt.mail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    private static final String TEST_EMAIL = "recipient@example.com";
    private static final String TEST_SUBJECT = "Test Subject";
    private static final String TEST_MESSAGE = "Test Message Body";
    private static final String SENDER_EMAIL = "sender@example.com";

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private EmailConfigurationProperties emailConfigurationProperties;

    @InjectMocks
    private EmailService emailService;

    @Captor
    private ArgumentCaptor<SimpleMailMessage> mailMessageCaptor;

    @BeforeEach
    void setUp() {
        when(emailConfigurationProperties.getUsername()).thenReturn(SENDER_EMAIL);
    }
}