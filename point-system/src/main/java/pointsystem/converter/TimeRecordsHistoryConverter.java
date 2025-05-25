package pointsystem.converter;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pointsystem.dto.timeRecordsHistory.TimeRecordsHistoryDto;
import pointsystem.entity.TimeRecordsHistory;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TimeRecordsHistoryConverter implements Converter<TimeRecordsHistory, TimeRecordsHistoryDto> {

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public TimeRecordsHistoryDto toDto(TimeRecordsHistory entity) {
        if (entity == null) {
            return null;
        }

        TimeRecordsHistoryDto dto = modelMapper.map(entity, TimeRecordsHistoryDto.class);
        dto.setTimeRecordsId(entity.getTimeRecords().getId());
        return dto;
    }

    @Override
    public TimeRecordsHistory toEntity(TimeRecordsHistoryDto dto) {
        if (dto == null) {
            return null;
        }

        TimeRecordsHistory entity = modelMapper.map(dto, TimeRecordsHistory.class);
        return entity;
    }

    @Override
    public List<TimeRecordsHistoryDto> toDto(List<TimeRecordsHistory> entities) {
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TimeRecordsHistory> toEntity(List<TimeRecordsHistoryDto> dtos) {
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}

