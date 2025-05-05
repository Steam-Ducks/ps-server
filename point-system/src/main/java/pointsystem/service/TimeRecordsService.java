package pointsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pointsystem.converter.TimeRecordsConverter;
import pointsystem.dto.timeRecords.TimeRecordsDto;
import pointsystem.entity.TimeRecords;
import pointsystem.repository.CompanyPositionEmployeeRepository;
import pointsystem.repository.TimeRecordsRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TimeRecordsService {

    private final TimeRecordsRepository timeRecordsRepository;
    private final TimeRecordsConverter timeRecordsConverter;
    private final EmployeeService employeeService;
    private final CompanyPositionEmployeeRepository companyPositionEmployeeRepository;

    @Autowired
    public TimeRecordsService(TimeRecordsRepository timeRecordsRepository, TimeRecordsConverter timeRecordsConverter, EmployeeService employeeService, CompanyPositionEmployeeRepository companyPositionEmployeeRepository) {
        this.timeRecordsRepository = timeRecordsRepository;
        this.timeRecordsConverter = timeRecordsConverter;
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


    // operações para exibir dados no dashboard

    public double calculateTotalSalaryByCompanyId(
            int companyId,
            LocalDateTime startDate,
            LocalDateTime endDate) {
        double totalSalary = 0;
        List<Integer> employeeIds = employeeService.getAllEmployeesFromCompany(companyId);
        if (employeeIds.isEmpty()) {
            return 0.0;
        }
        for (Integer employeeId : employeeIds) {
            List<TimeRecordsDto> employeeTimeRecords = getTimeRecordsByEmployeeId(Long.valueOf(employeeId), startDate, endDate);
            totalSalary += calculateSalaryByEmployeeId(employeeTimeRecords, employeeId);
        }

        return totalSalary;
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

    public double calculateSalaryByEmployeeId(List<TimeRecordsDto> timeRecords, Integer employeeId) {
        if (timeRecords.isEmpty()) {
            return 0.0;
        }
        double totalHours = calculateTotalHours(timeRecords);
        double salary = companyPositionEmployeeRepository.findByEmployeeId(employeeId).get().getSalary();
        return new BigDecimal(totalHours * salary).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}