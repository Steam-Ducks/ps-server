package pointsystem.service;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pointsystem.config.JwtUtil;
import pointsystem.converter_temp.UserConverter;
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
        return new AuthenticationResponseDto(token, userEntity.getIsAdmin());
    }

    public AuthenticationResponseDto authenticate(AuthenticationRequestDto request) throws BadRequestException {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (Exception e) {
            boolean userExists = userRepository.findByEmail(request.getEmail()).isPresent();
            if (userExists) {
                throw new BadRequestException("Senha incorreta");
            } else {
                throw new BadRequestException("Usuário não encontrado");
            }
        }

        UserEntity userEntity = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Usuário não encontrado"));

        String token = jwtUtil.generateToken(userEntity.getEmail(), userEntity.getIsAdmin());
        return new AuthenticationResponseDto(token, userEntity.getIsAdmin());
    }
}