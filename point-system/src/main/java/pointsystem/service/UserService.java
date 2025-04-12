package pointsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pointsystem.dto.user.CreateUserDto;
import pointsystem.dto.user.UpdateUserDto;
import pointsystem.entity.User;
import pointsystem.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public int createUser(CreateUserDto userDto) {
        User user = new User();
        user.setUsername(userDto.username());
        user.setPassword(userDto.password());
        user.setEmail(userDto.email());

        if (!user.isEmailvalidador()) {
            throw new IllegalArgumentException("O e-mail deve conter ou terminar com '@altave'");
        }

        return userRepository.save(user).getUserId();
    }

    public Optional<User> getUserById(int userId) {
        return userRepository.findById(userId);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void updateUserById(int userId, UpdateUserDto updateUserDto) {
        Optional<User> userEntity = userRepository.findById(userId);

        if (userEntity.isPresent()) {
            User user = userEntity.get();
            if (updateUserDto.username() != null) {
                user.setUsername(updateUserDto.username());
            }

            if (updateUserDto.password() != null) {
                user.setPassword(updateUserDto.password());
            }
            userRepository.save(user);
        }
    }

    public void deleteUserById(int userID) {
        boolean exists = userRepository.existsById(userID);
        if (exists) {
            userRepository.deleteById(userID);
        }
    }
}
