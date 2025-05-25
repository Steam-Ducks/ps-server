package pointsystem.integration.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pointsystem.dto.timeRecords.TimeRecordsDto;
import pointsystem.entity.Employee;
import pointsystem.entity.TimeRecords;
import pointsystem.repository.EmployeeRepository;
import pointsystem.repository.TimeRecordsRepository;
import pointsystem.service.TimeRecordsService;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class TimeRecordsServiceTest {

    @Autowired
    private TimeRecordsService timeRecordsService;

    @Autowired
    private TimeRecordsRepository timeRecordsRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    private TimeRecords savedRecord;
    private Employee savedEmployee;

    @BeforeEach
    public void setUp() {
        // Cria e salva um funcionário real no banco
        Employee emp = new Employee();
        emp.setName("Carlos Daniel");
        emp.setCpf("12375678900");
        emp.setStatus(true);
        savedEmployee = employeeRepository.save(emp);

        // Cria e salva um registro de ponto associado ao funcionário
        TimeRecords record = new TimeRecords();
        record.setEmployee(savedEmployee);
        record.setDateTime(Timestamp.from(OffsetDateTime.now().minusHours(2).toInstant()));
        record.setIsEdit(false);
        record.setUpdatedAt(OffsetDateTime.now());

        savedRecord = timeRecordsRepository.save(record);
    }

    @Test
    public void testGetTimeRecordsById() {
        Optional<TimeRecordsDto> result = timeRecordsService.getTimeRecordsById(savedRecord.getId().intValue());

        assertTrue(result.isPresent());
        assertEquals(savedEmployee.getId(), result.get().getEmployeeId());
    }

    @Test
    public void testGetAllTimeRecords() {
        List<TimeRecordsDto> all = timeRecordsService.getAllTimeRecords();
        assertFalse(all.isEmpty());
    }

    @Test
    public void testCreateTimeRecords() {
        // Cria outro funcionário para o teste de criação
        Employee employee = new Employee();
        employee.setName("Carlos Daniel");
        employee.setCpf("12345678900");
        employee.setStatus(true);
        employee = employeeRepository.save(employee);

        TimeRecordsDto dto = new TimeRecordsDto();
        dto.setEmployeeId((long) employee.getId());
        dto.setDateTime(Timestamp.from(OffsetDateTime.now().minusHours(1).toInstant()));

        TimeRecordsDto saved = timeRecordsService.createTimeRecords(dto);

        assertNotNull(saved.getId());
        assertEquals(dto.getEmployeeId(), saved.getEmployeeId());
        assertFalse(saved.getIsEdit());
    }

    @Test
    public void testUpdateTimeRecordsById() {
        OffsetDateTime newTime = OffsetDateTime.now().minusHours(5);
        TimeRecordsDto update = new TimeRecordsDto();
        update.setDateTime(Timestamp.from(newTime.toInstant()));

        timeRecordsService.updateTimeRecordsById(savedRecord.getId().intValue(), update);

        TimeRecords updated = timeRecordsRepository.findById(savedRecord.getId()).orElseThrow();
        assertEquals(newTime.toLocalDateTime().withNano(0), updated.getDateTime().toLocalDateTime().withNano(0));
        assertTrue(updated.getIsEdit());
    }

    @Test
    public void testDeleteTimeRecordsById() {
        timeRecordsService.deleteTimeRecordsById(savedRecord.getId().intValue());
        assertFalse(timeRecordsRepository.existsById(savedRecord.getId()));
    }
}
