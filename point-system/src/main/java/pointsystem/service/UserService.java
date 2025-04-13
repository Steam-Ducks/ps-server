package pointsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pointsystem.converter.UserConverter;
import pointsystem.dto.user.UserDto;
import pointsystem.entity.User;
import pointsystem.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserConverter userConverter;

    @Autowired
    public UserService(UserRepository userRepository, UserConverter userConverter) {
        this.userRepository = userRepository;
        this.userConverter = userConverter;
    }

    public int createUser(UserDto userDto) {
        User user = userConverter.toEntity(userDto);

        if (!user.isEmailvalidador()) {
            throw new IllegalArgumentException("O e-mail deve ser do domínio '@altave'");
        }

        User userSaved = userRepository.save(user);
        return userSaved.getUserId();
    }

    public Optional<UserDto> getUserById(int userId) {
        return userRepository.findById(userId)
                .map(userConverter::toDto);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userConverter::toDto)
                .collect(Collectors.toList());
    }

    public void updateUserById(int userId, UserDto userDto) {
        Optional<User> userEntity = userRepository.findById(userId);

        if (userEntity.isPresent()) {
            User user = userEntity.get();
            User updatedUser = userConverter.updateEntity(user, userDto);
            userRepository.save(updatedUser);
        }
    }

    public void deleteUserById(int userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
        }
    }
}