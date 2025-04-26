package pointsystem.converter_temp;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pointsystem.dto.position.PositionDto;
import pointsystem.entity.Position;

import java.util.List;

@Component
public class PositionConverter implements Converter<Position, PositionDto> {

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PositionDto toDto(Position entity) {
        if (entity == null) {
            return null;
        }
        return modelMapper.map(entity, PositionDto.class);
    }

    @Override
    public Position toEntity(PositionDto dto) {
        if (dto == null) {
            return null;
        }
        return modelMapper.map(dto, Position.class);
    }

    @Override
    public List<PositionDto> toDto(List<Position> entities) {
        return entities.stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<Position> toEntity(List<PositionDto> dtos) {
        return dtos.stream()
                .map(this::toEntity)
                .toList();
    }
}