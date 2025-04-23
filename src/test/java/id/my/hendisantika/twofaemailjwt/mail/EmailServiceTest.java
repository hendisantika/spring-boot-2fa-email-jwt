package id.my.hendisantika.twofaemailjwt.mail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

    @Test
    void sendEmail_ShouldSendEmailWithCorrectParameters() {
        // Act
        emailService.sendEmail(TEST_EMAIL, TEST_SUBJECT, TEST_MESSAGE);

        // Assert
        verify(javaMailSender, times(1)).send(mailMessageCaptor.capture());

        SimpleMailMessage capturedMessage = mailMessageCaptor.getValue();
        assertEquals(SENDER_EMAIL, capturedMessage.getFrom());
        assertEquals(TEST_EMAIL, capturedMessage.getTo()[0]);
        assertEquals(TEST_SUBJECT, capturedMessage.getSubject());
        assertEquals(TEST_MESSAGE, capturedMessage.getText());
    }

    @Test
    void sendEmail_WithNullValues_ShouldStillAttemptToSendEmail() {
        // Act
        emailService.sendEmail(null, null, null);

        // Assert
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

}