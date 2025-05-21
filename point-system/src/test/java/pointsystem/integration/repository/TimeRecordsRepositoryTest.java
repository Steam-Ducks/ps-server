package pointsystem.integration.repository;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import pointsystem.entity.Employee;
import pointsystem.entity.TimeRecords;
import pointsystem.repository.EmployeeRepository;
import pointsystem.repository.TimeRecordsRepository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class TimeRecordsRepositoryTest {

    @Autowired
    private TimeRecordsRepository timeRecordsRepository;

    @Autowired
    private EmployeeRepository employeeRepository; // Presume-se que exista

    @Test
    public void searchRegisterByEmployeeAndTime() {
        Employee emp = new Employee();
        emp.setName("Carlos Daniel");
        emp.setCpf("12345678900");
        emp.setStartDate(LocalDate.now());
        emp.setStatus(true);

        employeeRepository.save(emp);


        // Cria e salva registros de ponto
        TimeRecords r1 = new TimeRecords();
        r1.setEmployee(emp);
        r1.setDateTime(Timestamp.valueOf(LocalDateTime.of(2024, 1, 10, 9, 0)));

        TimeRecords r2 = new TimeRecords();
        r2.setEmployee(emp);
        r2.setDateTime(Timestamp.valueOf(LocalDateTime.of(2024, 1, 15, 17, 0)));

        TimeRecords r3 = new TimeRecords();
        r3.setEmployee(emp);
        r3.setDateTime(Timestamp.valueOf(LocalDateTime.of(2024, 2, 1, 8, 30)));

        timeRecordsRepository.save(r1);
        timeRecordsRepository.save(r2);
        timeRecordsRepository.save(r3);

        // Consulta entre 12 e 31 de janeiro
        LocalDateTime inicio = LocalDateTime.of(2024, 1, 12, 0, 0);
        LocalDateTime fim = LocalDateTime.of(2024, 1, 31, 23, 59);

        List<TimeRecords> resultados = timeRecordsRepository.findByEmployeeId(
                (long) emp.getId(), inicio, fim
        );

        assertEquals(1, resultados.size());
        assertEquals(
                LocalDateTime.of(2024, 1, 15, 17, 0),
                resultados.get(0).getDateTime().toLocalDateTime()
        );

    }
}
