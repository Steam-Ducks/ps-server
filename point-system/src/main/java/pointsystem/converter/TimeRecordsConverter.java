package pointsystem.converter;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pointsystem.dto.timeRecords.TimeRecordsDto;
import pointsystem.entity.TimeRecords;

import java.util.List;

@Component
public class TimeRecordsConverter implements Converter<TimeRecords, TimeRecordsDto> {

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public TimeRecordsDto toDto(TimeRecords entity) {
        if (entity == null) {
            return null;
        }
        return modelMapper.map(entity, TimeRecordsDto.class);
    }

    @Override
    public TimeRecords toEntity(TimeRecordsDto dto) {
        if (dto == null) {
            return null;
        }
        return modelMapper.map(dto, TimeRecords.class);
    }

    @Override
    public List<TimeRecordsDto> toDto(List<TimeRecords> entities) {
        return entities.stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<TimeRecords> toEntity(List<TimeRecordsDto> dtos) {
        return dtos.stream()
                .map(this::toEntity)
                .toList();
    }
}