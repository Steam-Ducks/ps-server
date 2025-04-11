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

    @Autowired
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public int createUser(UserDto userDto) {
        User user = UserConverter.toEntity(userDto);
        User userSaved = userRepository.save(user);
        return userSaved.getUserId();
    }

    public Optional<UserDto> getUserById(int userId) {
        return userRepository.findById(userId)
                .map(UserConverter::toDto);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserConverter::toDto)
                .collect(Collectors.toList());
    }

    public void updateUserById(int userId, UserDto userDto) {
        Optional<User> userEntity = userRepository.findById(userId);

        if (userEntity.isPresent()) {
            User user = userEntity.get();
            User updatedUser = UserConverter.updateEntity(user, userDto);
            userRepository.save(updatedUser);
        }
    }

    public void deleteUserById(int userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
        }
    }
}