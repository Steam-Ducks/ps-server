package pointsystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pointsystem.config.JwtUtil;
import pointsystem.converter.UserConverter;
import pointsystem.dto.authentication.*;
import pointsystem.entity.UserEntity;
import pointsystem.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserConverter userConverter;

    public AuthenticationResponseDto register(RegisterRequestDto request) {
        UserEntity userEntity = userConverter.toEntity(request);

        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        if (!userEntity.isEmailvalidador()) {
            throw new IllegalArgumentException("O e-mail deve ser do domínio '@altave'");
        }

        UserEntity userEntitySaved = userRepository.save(userEntity);

        String token = jwtUtil.generateToken(userEntitySaved.getEmail(), userEntitySaved.getIsAdmin());
        return new AuthenticationResponseDto(token);
    }

    public AuthenticationResponseDto authenticate(AuthenticationRequestDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserEntity userEntity = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(userEntity.getEmail(), userEntity.getIsAdmin());
        return new AuthenticationResponseDto(token);
    }
}