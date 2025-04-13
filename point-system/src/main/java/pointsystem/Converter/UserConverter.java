package pointsystem.converter;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pointsystem.dto.user.UserDto;
import pointsystem.entity.User;

@Component
public class UserConverter {

    @Autowired
    private ModelMapper modelMapper;

    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }
        return modelMapper.map(user, UserDto.class);
    }

    public User toEntity(UserDto userDto) {
        if (userDto == null) {
            return null;
        }
        return modelMapper.map(userDto, User.class);
    }

    public User updateEntity(User user, UserDto userDto) {
        if (userDto == null) {
            return user;
        }
        modelMapper.map(userDto, user);
        return user;
    }
}