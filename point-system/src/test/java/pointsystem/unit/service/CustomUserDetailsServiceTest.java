package pointsystem.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pointsystem.entity.UserEntity;
import pointsystem.repository.UserRepository;
import pointsystem.service.CustomUserDetailsService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_ReturnsUserDetails_WhenUserExists() {
        // Arrange
        UserEntity user = new UserEntity();
        user.setEmail("user@altave.com");
        user.setPassword("securePassword123");
        user.setIsAdmin(true);

        when(userRepository.findByEmail("user@altave.com"))
                .thenReturn(Optional.of(user));


        UserDetails userDetails = customUserDetailsService.loadUserByUsername("user@altave.com");


        assertNotNull(userDetails);
        assertEquals("user@altave.com", userDetails.getUsername());
        assertEquals("securePassword123", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void loadUserByUsername_ReturnsUserRole_WhenUserIsNotAdmin() {

        UserEntity user = new UserEntity();
        user.setEmail("user2@altave.com");
        user.setPassword("123456");
        user.setIsAdmin(false);

        when(userRepository.findByEmail("user2@altave.com"))
                .thenReturn(Optional.of(user));


        UserDetails userDetails = customUserDetailsService.loadUserByUsername("user2@altave.com");


        assertNotNull(userDetails);
        assertEquals("user2@altave.com", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void loadUserByUsername_ThrowsUsernameNotFoundException_WhenUserNotFound() {
        when(userRepository.findByEmail("notfound@altave.com"))
                .thenReturn(Optional.empty());


        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("notfound@altave.com")
        );


        assertTrue(exception.getMessage().contains("User not found with email: notfound@altave.com"));
    }
}