package pointsystem.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pointsystem.entity.UserEntity;
import pointsystem.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CustomUserDetailsServiceTest {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testLoadUserByUsername_whenUserExists_shouldReturnUserDetails() {
        // Arrange - cria e salva um usuário no banco
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("Test User");
        userEntity.setEmail("test@example.com");
        userEntity.setPassword("encryptedPassword123");
        userEntity.setIsAdmin(true);
        userRepository.save(userEntity);

        // Act - chama o método a testar
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("test@example.com");

        // Assert - valida os resultados
        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        assertEquals("encryptedPassword123", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    public void testLoadUserByUsername_whenUserDoesNotExist_shouldThrowException() {
        // Act & Assert
        UsernameNotFoundException thrown = assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername("nonexistent@example.com");
        });

        assertTrue(thrown.getMessage().contains("User not found with email: nonexistent@example.com"));
    }
}
