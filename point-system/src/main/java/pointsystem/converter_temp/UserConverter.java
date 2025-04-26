package pointsystem.converter_temp;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pointsystem.dto.authentication.RegisterRequestDto;
import pointsystem.dto.user.UserDto;
import pointsystem.entity.UserEntity;

@Component
public class UserConverter {

    @Autowired
    private ModelMapper modelMapper;

    public UserEntity toEntity(UserDto dto) {
        if (dto == null) {
            return null;
        }
        return modelMapper.map(dto, UserEntity.class);
    }

    public UserEntity toEntity(RegisterRequestDto dto) {
        if (dto == null) {
            return null;
        }
        return modelMapper.map(dto, UserEntity.class);
    }

    public UserDto toDto(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        return modelMapper.map(entity, UserDto.class);
    }

    public UserEntity updateEntity(UserEntity entity, UserDto dto) {
        if (entity == null || dto == null) {
            return entity;
        }

        if (dto.getUsername() != null) {
            entity.setUsername(dto.getUsername());
        }
        if (dto.getEmail() != null) {
            entity.setEmail(dto.getEmail());
        }
        if (dto.getPassword() != null) {
            entity.setPassword(dto.getPassword());
        }
        if (dto.getIsAdmin() != null) {
            entity.setIsAdmin(dto.getIsAdmin());
        }
        if (dto.getIsActive() != null) {
            entity.setIsActive(dto.getIsActive());
        }

        return entity;
    }
}