package id.my.hendisantika.twofaemailjwt.repository;

import id.my.hendisantika.twofaemailjwt.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void existsByEmailId_WhenUserExists_ShouldReturnTrue() {
        // Arrange
        User user = createTestUser("test@example.com");
        entityManager.persist(user);
        entityManager.flush();

        // Act
        boolean exists = userRepository.existsByEmailId("test@example.com");

        // Assert
        assertTrue(exists);
    }

    @Test
    void existsByEmailId_WhenUserDoesNotExist_ShouldReturnFalse() {
        // Act
        boolean exists = userRepository.existsByEmailId("nonexistent@example.com");

        // Assert
        assertFalse(exists);
    }

    @Test
    void findByEmailId_WhenUserExists_ShouldReturnUser() {
        // Arrange
        User user = createTestUser("test@example.com");
        entityManager.persist(user);
        entityManager.flush();

        // Act
        Optional<User> foundUser = userRepository.findByEmailId("test@example.com");

        // Assert
        assertTrue(foundUser.isPresent());
        assertEquals("test@example.com", foundUser.get().getEmailId());
    }
}