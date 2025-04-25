package pointsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pointsystem.converter.TimeRecordsConverter;
import pointsystem.dto.timeRecords.TimeRecordsDto;
import pointsystem.entity.TimeRecords;
import pointsystem.repository.TimeRecordsRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TimeRecordsService {

    private final TimeRecordsRepository timeRecordsRepository;
    private final TimeRecordsConverter timeRecordsConverter;

    @Autowired
    public TimeRecordsService(TimeRecordsRepository timeRecordsRepository, TimeRecordsConverter timeRecordsConverter) {
        this.timeRecordsRepository = timeRecordsRepository;
        this.timeRecordsConverter = timeRecordsConverter;
    }

    public Optional<TimeRecordsDto> getTimeRecordsById(Integer timeRecordsId) {
        return timeRecordsRepository.findById(Long.valueOf(timeRecordsId))
                .map(timeRecordsConverter::toDto);
    }

    public List<TimeRecordsDto> getAllTimeRecords() {
        return timeRecordsConverter.toDto(timeRecordsRepository.findAll());
    }

    public List<TimeRecordsDto> getTimeRecordsByEmployeeId(
            Long employeeId,
            LocalDateTime startDate,
            LocalDateTime endDate) {

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("A data inicial deve ser anterior à data final.");
        }

        return timeRecordsConverter.toDto(
                timeRecordsRepository.findByEmployeeId(employeeId, startDate, endDate)
        );
    }

    public TimeRecordsDto createTimeRecords(TimeRecordsDto timeRecordsDto) {
        TimeRecords entity = timeRecordsConverter.toEntity(timeRecordsDto);
        entity.setIsEdit(false);
        TimeRecords savedEntity = timeRecordsRepository.save(entity);
        return timeRecordsConverter.toDto(savedEntity);
    }

    public void updateTimeRecordsById(Integer timeRecordsId, TimeRecordsDto timeRecordsDto) {
        Optional<TimeRecords> timeRecordsEntity = timeRecordsRepository.findById(Long.valueOf(timeRecordsId));

        if (timeRecordsEntity.isPresent()) {
            TimeRecords timeRecords = timeRecordsEntity.get();

            if (timeRecordsDto.getDateTime() != null) {
                timeRecords.setDateTime(timeRecordsDto.getDateTime());
            }

            timeRecords.setIsEdit(true);

            timeRecordsRepository.save(timeRecords);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Time Records not found");
        }
    }
}