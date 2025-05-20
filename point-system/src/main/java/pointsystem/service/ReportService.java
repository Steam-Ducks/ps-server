package pointsystem.service;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import pointsystem.dto.employee.EmployeeDto;
import pointsystem.entity.Company;
import pointsystem.repository.CompanyRepository;
import pointsystem.repository.EmployeeRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportService {
    private final CompanyRepository companyRepository;
    private final EmployeeService employeeService;

    public ReportService(CompanyRepository companyRepository, EmployeeService employeeService) {
        this.companyRepository = companyRepository;
        this.employeeService = employeeService;
    }


    public byte[] generateAllCompaniesReport() throws IOException {
        try (Workbook workbook = new XSSFWorkbook())
        {
            Sheet sheet = workbook.createSheet("Empresas");
            List<Company> companies = companyRepository.findAll();

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Nome");
            header.createCell(1).setCellValue("CNPJ");
            header.createCell(2).setCellValue("Contato");

            int rowIndex = 1;
            for (Company company : companies) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(company.getName());
                row.createCell(1).setCellValue(company.getCnpj());
                row.createCell(2).setCellValue(company.getContact());
            }

            for (int i = 0; i < 3; i++) {
                sheet.autoSizeColumn(i);
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new IOException("Erro ao gerar o relatório de empresas", e);
        }
    }


    public byte[] generateEmployeeListReport(Integer companyId) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Funcionários");

            List<EmployeeDto> employees = employeeService.getAllEmployeesFromCompany(companyId);

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Nome");
            header.createCell(1).setCellValue("CPF");
            header.createCell(2).setCellValue("Data de Inicio");
            header.createCell(3).setCellValue("Cargo");
            header.createCell(4).setCellValue("Salário");

            int rowIndex = 1;
            for (EmployeeDto employee : employees) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(employee.getName());
                row.createCell(1).setCellValue(employee.getCpf());
                row.createCell(2).setCellValue(employee.getStartDate() != null
                        ? employee.getStartDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        : "");
                row.createCell(3).setCellValue(employee.getPosition() != null ? employee.getPosition().getName() : "");
                row.createCell(4).setCellValue(String.format("%.2f", employee.getSalary()));
            }

            for (int i = 0; i < 5; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new IOException("Erro ao gerar o relatório de funcionários", e);
        }
    }
    /*
    public byte[] generateTimeRecordReport(Integer employeeId, LocalDate startDate, LocalDate endDate) {
    }

    public byte[] generateCompanyHoursReport(Integer companyId) {
    }

     */
}
