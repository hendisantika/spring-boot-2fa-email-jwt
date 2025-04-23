package id.my.hendisantika.twofaemailjwt.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class JokeControllerTest {

    @InjectMocks
    private JokeController jokeController;


    @Test
    void jokeRetrievalHandler_ShouldReturnJoke() {
        // Act
        ResponseEntity<?> response = jokeController.jokeRetrievalHandler();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        String responseBody = (String) response.getBody();
        assertTrue(responseBody.contains("joke"));
        assertTrue(responseBody.contains("cereal killer"));
    }

}