package pointsystem.integration.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import pointsystem.converter.UserConverter;
import pointsystem.dto.user.UserDto;
import pointsystem.entity.UserEntity;
import pointsystem.repository.UserRepository;
import pointsystem.service.UserService;
import pointsystem.Config;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@ContextConfiguration(classes = {Config.class})
@Import(UserServiceTest.Config.class)
public class UserServiceTest {

    @TestConfiguration
    static class Config {
        @Bean
        public UserConverter userConverter() {
            return new UserConverter();
        }

        @Bean
        public BCryptPasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        public UserService userService(UserRepository userRepository, UserConverter userConverter, BCryptPasswordEncoder encoder) {
            return new UserService(userRepository, userConverter, encoder);
        }
    }

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Deve criar um novo usuário com e-mail válido")
    void testCreateUser() {
        UserDto dto = new UserDto();
        dto.setUsername("Carlos Daniel");
        dto.setEmail("carlos@altave.com.br");
        dto.setPassword("senha123");
        dto.setIsActive(true);

        int userId = userService.createUser(dto);

        UserEntity saved = userRepository.findById(userId).orElse(null);
        assertNotNull(saved);
        assertEquals("carlos@altave.com.br", saved.getEmail());
        assertTrue(saved.getPassword().startsWith("$2")); // Verifica se foi criptografado com BCrypt
    }

    @Test
    @DisplayName("Deve retornar todos os usuários ativos")
    void testGetAllUsers() {
        UserEntity user = new UserEntity();
        user.setUsername("Usuário Ativo");
        user.setEmail("ativo@altave.com.br");
        user.setPassword("senha");
        user.setIsActive(true);
        userRepository.save(user);

        List<UserDto> users = userService.getAllUsers();
        assertEquals(1, users.size());
        assertEquals("ativo@altave.com.br", users.get(0).getEmail());
    }

    @Test
    @DisplayName("Deve atualizar usuário existente")
    void testUpdateUser() {
        UserEntity user = new UserEntity();
        user.setUsername("Antigo");
        user.setEmail("antigo@altave.com.br");
        user.setPassword("old");
        user.setIsActive(true);
        user = userRepository.save(user);

        UserDto update = new UserDto();
        update.setUsername("Novo Nome");
        update.setEmail("novo@altave.com.br");
        update.setPassword("novaSenha");

        userService.updateUserById(user.getUserId(), update);

        UserEntity updated = userRepository.findById(user.getUserId()).orElse(null);
        assertNotNull(updated);
        assertEquals("novo@altave.com.br", updated.getEmail());
        assertTrue(updated.getPassword().startsWith("$2"));
    }
}
