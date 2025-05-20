package pointsystem.service;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
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
        userEntity.setEmail(request.getEmail().toLowerCase());
        if (!userEntity.isEmailvalidador()) {
            throw new IllegalArgumentException("O e-mail deve ser do domínio '@altave'");
        }

        UserEntity userEntitySaved = userRepository.save(userEntity);

        String token = jwtUtil.generateToken(userEntitySaved.getEmail(), userEntitySaved.getIsAdmin());
        return new AuthenticationResponseDto(token, userEntity.getIsAdmin(), userEntity.getUsername());
    }

    public AuthenticationResponseDto authenticate(AuthenticationRequestDto request) throws BadRequestException {
        try {
            request.setEmail(request.getEmail().toLowerCase());
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (Exception e) {
            throw new BadRequestException("E-mail e/ou senha incorretos. Tente novamente.");
        }

        UserEntity userEntity = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("E-mail e/ou senha incorretos. Tente novamente."));

        String token = jwtUtil.generateToken(userEntity.getEmail(), userEntity.getIsAdmin());
        return new AuthenticationResponseDto(token, userEntity.getIsAdmin(), userEntity.getUsername());
    }
}