package pointsystem.service;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pointsystem.converter.TimeRecordsConverter;
import pointsystem.dto.timeRecords.TimeRecordsDto;
import pointsystem.entity.TimeRecords;
import pointsystem.entity.TimeRecordsHistory;
import pointsystem.entity.UserEntity;
import pointsystem.repository.TimeRecordsHistoryRepository;
import pointsystem.repository.TimeRecordsRepository;
import pointsystem.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TimeRecordsService {

    private final TimeRecordsRepository timeRecordsRepository;
    private final TimeRecordsConverter timeRecordsConverter;

    private final TimeRecordsHistoryRepository timeRecordsHistoryRepository;
    private final UserRepository userRepository;

    @Autowired
    public TimeRecordsService(TimeRecordsRepository timeRecordsRepository, TimeRecordsHistoryRepository timeRecordsHistoryRepository, TimeRecordsConverter timeRecordsConverter, UserRepository userRepository) {
        this.timeRecordsRepository = timeRecordsRepository;
        this.timeRecordsHistoryRepository = timeRecordsHistoryRepository;
        this.timeRecordsConverter = timeRecordsConverter;
        this.userRepository = userRepository;
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

    @Transactional
    public void updateTimeRecordsById(Integer timeRecordsId, TimeRecordsDto timeRecordsDto, String email) throws BadRequestException {
        Optional<TimeRecords> timeRecordsEntity = timeRecordsRepository.findById(Long.valueOf(timeRecordsId));
        if (timeRecordsEntity.isPresent()) {
            TimeRecords timeRecords = timeRecordsEntity.get();

            TimeRecordsHistory history = new TimeRecordsHistory();
            history.setTimeRecords(timeRecords);
            history.setDateTimeBefore(timeRecords.getDateTime());
            history.setDateTimeAfter(timeRecordsDto.getDateTime());

            UserEntity userEntity = userRepository.findByEmail(email)
                    .orElseThrow(() -> new BadRequestException("Usuário não encontrado. Tente novamente."));
            String username = userEntity.getUsername();

            history.setUsername(username);

            timeRecordsHistoryRepository.save(history);

            if (timeRecordsDto.getDateTime() != null) {
                timeRecords.setDateTime(timeRecordsDto.getDateTime());
            }
            timeRecords.setIsEdit(true);
            timeRecords.setUpdatedAt(OffsetDateTime.now());
            timeRecordsRepository.save(timeRecords);

        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Time Records not found");
        }
    }

    public void deleteTimeRecordsById(Integer timeRecordsId) {
        if (timeRecordsRepository.existsById(Long.valueOf(timeRecordsId))) {
            timeRecordsRepository.deleteById(Long.valueOf(timeRecordsId));
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Time Records not found");
        }
    }
}