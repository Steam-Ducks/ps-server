package pointsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pointsystem.converter.UserConverter;
import pointsystem.dto.user.UserDto;
import pointsystem.entity.UserEntity;
import pointsystem.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, UserConverter userConverter, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userConverter = userConverter;
        this.passwordEncoder = passwordEncoder;
    }

    public int createUser(UserDto userDto) {
        UserEntity user = userConverter.toEntity(userDto);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (!user.isEmailvalidador()) {
            throw new IllegalArgumentException("O e-mail deve ser do domínio '@altave'");
        }

        UserEntity userSaved = userRepository.save(user);
        return userSaved.getUserId();
    }


    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userConverter::toDto)
                .collect(Collectors.toList());
    }

    public void updateUserById(int userId, UserDto userDto) {
        Optional<UserEntity> userEntity = userRepository.findById(userId);

        if (userEntity.isPresent()) {
            UserEntity user = userEntity.get();
            UserEntity updatedUserEntity = userConverter.updateEntity(user, userDto);
            updatedUserEntity.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(updatedUserEntity);
        }
    }
}