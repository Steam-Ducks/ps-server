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
import pointsystem.repository.CompanyPositionEmployeeRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TimeRecordsService {

    private final TimeRecordsRepository timeRecordsRepository;
    private final TimeRecordsConverter timeRecordsConverter;
    private final EmployeeService employeeService;
    private final CompanyPositionEmployeeRepository companyPositionEmployeeRepository;

    private final TimeRecordsHistoryRepository timeRecordsHistoryRepository;
    private final UserRepository userRepository;

    @Autowired

    public TimeRecordsService(TimeRecordsRepository timeRecordsRepository, TimeRecordsHistoryRepository timeRecordsHistoryRepository, TimeRecordsConverter timeRecordsConverter, UserRepository userRepository) {

        this.timeRecordsRepository = timeRecordsRepository;
        this.timeRecordsHistoryRepository = timeRecordsHistoryRepository;
        this.timeRecordsConverter = timeRecordsConverter;
        this.userRepository = userRepository;
        this.employeeService = employeeService;
        this.companyPositionEmployeeRepository = companyPositionEmployeeRepository;

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


    // operações para exibir dados no dashboard

    public Map<String, Object> calculateCompanyMetrics(
            int companyId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            boolean returnAll) {
        double totalSalary = 0;
        double totalHours = 0;
        Map<String, Integer> editedRecords = new HashMap<>();

        List<Integer> employeeIds = employeeService.getAllEmployeeIdsFromCompany(companyId);
        if (employeeIds.isEmpty()) {
            return Map.of("totalSalary", 0.0, "totalWorkedHours", 0.0, "manualChangesByDate", editedRecords);
        }

        for (Integer employeeId : employeeIds) {
            List<TimeRecordsDto> employeeTimeRecords = getTimeRecordsByEmployeeId(Long.valueOf(employeeId), startDate, endDate);
            totalHours += calculateTotalHours(employeeTimeRecords);
            totalSalary += calculateSalaryByEmployeeId(totalHours, employeeId);

            if (returnAll) {
                employeeTimeRecords.stream()
                        .filter(TimeRecordsDto::getIsEdit)
                        .forEach(record -> {
                            String date = record.getDateTime().toLocalDateTime().toLocalDate().toString();
                            editedRecords.put(date, editedRecords.getOrDefault(date, 0) + 1);
                        });
            }
        }

        return Map.of("totalSalary", totalSalary, "totalWorkedHours", totalHours, "manualChangesByDate", editedRecords);
    }

    public double calculateTotalHours(List<TimeRecordsDto> timeRecords) {
        double totalHours = 0;

        for (int i = 0; i < timeRecords.size() - 1; i += 2) {
            TimeRecordsDto clockIn = timeRecords.get(i);
            TimeRecordsDto clockOut = timeRecords.get(i + 1);

            if (clockIn == null || clockOut == null ||
                    clockIn.getDateTime() == null || clockOut.getDateTime() == null) {
                continue;
            }
            Duration duration = Duration.between(clockIn.getDateTime().toLocalDateTime(), clockOut.getDateTime().toLocalDateTime());

            if (duration.toHours() > 12) {
                i--;
                continue;
            }
            totalHours += duration.toHours() + (duration.toMinutesPart() / 60.0);
        }

        BigDecimal rounded = new BigDecimal(totalHours).setScale(2, RoundingMode.HALF_UP);
        return rounded.doubleValue();
    }

    public double calculateSalaryByEmployeeId(double totalHours, Integer employeeId) {
        if (totalHours <= 0) {
            return 0.0;
        }
        double salary = companyPositionEmployeeRepository.findByEmployeeId(employeeId).get().getSalary();
        return new BigDecimal(totalHours * salary).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public void deleteTimeRecordsById(Integer timeRecordsId) {
        if (timeRecordsRepository.existsById(Long.valueOf(timeRecordsId))) {
            timeRecordsRepository.deleteById(Long.valueOf(timeRecordsId));
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Time Records not found");
        }
    }
}