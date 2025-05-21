package pointsystem.integration.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import pointsystem.entity.UserEntity;
import pointsystem.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void saveAndSearchByEmail() {
        UserEntity user = new UserEntity();
        user.setEmail("carlos@example.com");
        user.setIsActive(true);

        userRepository.save(user);

        Optional<UserEntity> found = userRepository.findByEmail("carlos@example.com");
        assertTrue(found.isPresent());
        assertEquals("carlos@example.com", found.get().getEmail());
    }

    @Test
    public void searchActiveUsers() {
        UserEntity user1 = new UserEntity();
        user1.setEmail("ativo1@example.com");
        user1.setIsActive(true);

        UserEntity user2 = new UserEntity();
        user2.setEmail("inativo@example.com");
        user2.setIsActive(false);

        UserEntity user3 = new UserEntity();
        user3.setEmail("ativo2@example.com");
        user3.setIsActive(true);

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        List<UserEntity> ativos = userRepository.findByIsActiveTrue();
        assertEquals(2, ativos.size());
        assertTrue(ativos.stream().allMatch(UserEntity::getIsActive));
    }
}

