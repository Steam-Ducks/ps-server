package pointsystem.converter;

import pointsystem.dto.user.UserDto;
import pointsystem.entity.User;

public class UserConverter {

    public static UserDto toDto(User user) {
        if (user == null) {
            return null;
        }
        return UserDto.builder()
                .id(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(null) // Avoid exposing the password
                .isAdmin(user.getIsAdmin())
                .isInactive(user.getIsInactive())
                .build();
    }

    public static User toEntity(UserDto userDto) {
        if (userDto == null) {
            return null;
        }
        return User.builder()
                .userId(userDto.getId() != null ? userDto.getId() : 0)
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .password(userDto.getPassword())
                .isAdmin(userDto.getIsAdmin())
                .isInactive(userDto.getIsInactive())
                .build();
    }

    public static User updateEntity(User user, UserDto userDto) {
        if (userDto.getUsername() != null) {
            user.setUsername(userDto.getUsername());
        }
        if (userDto.getPassword() != null) {
            user.setPassword(userDto.getPassword());
        }
        user.setIsAdmin(userDto.getIsAdmin());
        user.setIsInactive(userDto.getIsInactive());
        return user;
    }
}