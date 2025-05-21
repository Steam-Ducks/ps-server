package pointsystem.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import pointsystem.dto.employee.EmployeeDto;
import pointsystem.dto.timeRecords.TimeRecordsDto;
import pointsystem.entity.Company;
import pointsystem.repository.CompanyRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportService {
    private final CompanyRepository companyRepository;
    private final EmployeeService employeeService;
    private final TimeRecordsService timeRecordsService;


    public ReportService(CompanyRepository companyRepository, EmployeeService employeeService, TimeRecordsService timeRecordsService) {
        this.companyRepository = companyRepository;
        this.employeeService = employeeService;
        this.timeRecordsService = timeRecordsService;
    }


    public byte[] generateAllCompaniesReport() throws IOException {
        try (Workbook workbook = new XSSFWorkbook())
        {
            Sheet sheet = workbook.createSheet("Empresas");
            List<Company> companies = companyRepository.findAll();

            Font boldFont = workbook.createFont();
            boldFont.setBold(true);

            CellStyle boldStyle = workbook.createCellStyle();
            boldStyle.setFont(boldFont);


            Row header = sheet.createRow(0);
            String[] headers = {"Nome", "CNPJ", "Contato"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(boldStyle);
            }
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

            Font boldFont = workbook.createFont();
            boldFont.setBold(true);

            CellStyle boldStyle = workbook.createCellStyle();
            boldStyle.setFont(boldFont);

            List<EmployeeDto> employees = employeeService.getAllEmployeesFromCompany(companyId);

            Row header = sheet.createRow(0);
            String[] headers = {"Nome", "CPF", "Data de Início", "Cargo", "Salário (por hora)"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(boldStyle);
            }

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

    public byte[] generateTimeRecordReport(Integer employeeId, LocalDate startDate, LocalDate endDate) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Registros de Tempo");

            List<TimeRecordsDto> timeRecords = timeRecordsService.getTimeRecordsByEmployeeId(
                    Long.valueOf(employeeId),
                    startDate.atStartOfDay(),
                    endDate.atTime(23, 59)
            );

            Font boldFont = workbook.createFont();
            boldFont.setBold(true);

            CellStyle boldStyle = workbook.createCellStyle();
            boldStyle.setFont(boldFont);

            Row header = sheet.createRow(0);
            String[] headers = {"Entrada", "Editado (Entrada)", "Saída", "Editado (Saída)", "Total de Horas"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(boldStyle);
            }

            int rowIndex = 1;
            for (int i = 0; i < timeRecords.size(); i += 2) {
                Row row = sheet.createRow(rowIndex++);
                TimeRecordsDto entry = timeRecords.get(i);
                row.createCell(0).setCellValue(entry.getDateTime().toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                row.createCell(1).setCellValue(entry.getIsEdit() ? "SIM" : "NÃO");

                if (i + 1 < timeRecords.size()) {
                    TimeRecordsDto exit = timeRecords.get(i + 1);
                    row.createCell(2).setCellValue(exit.getDateTime().toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                    row.createCell(3).setCellValue(exit.getIsEdit() ? "SIM" : "NÃO");

                    double totalHours = timeRecordsService.calculateTotalHours(List.of(entry, exit));
                    row.createCell(4).setCellValue(String.format("%.2f", totalHours));
                } else {
                    row.createCell(2).setCellValue("");
                    row.createCell(3).setCellValue("");
                    row.createCell(4).setCellValue("");
                }
            }

            for (int i = 0; i < 5; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new IOException("Erro ao gerar o relatório de registros de tempo", e);
        }
    }


    public byte[] generateCompanyHoursReport(Integer companyId, LocalDate startDate, LocalDate endDate) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Horas por Empresa");

            Company company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada com o ID: " + companyId));
            List<EmployeeDto> employees = employeeService.getAllEmployeesFromCompany(companyId);

            double totalCompanyHours = 0;


            Font boldFont = workbook.createFont();
            boldFont.setBold(true);

            CellStyle boldStyle = workbook.createCellStyle();
            boldStyle.setFont(boldFont);


            Row companyRow = sheet.createRow(0);
            String[] companyHeaders = {"Nome da Empresa:", company.getName(), "Horas Totais:"};
            for (int i = 0; i < companyHeaders.length; i++) {
                Cell cell = companyRow.createCell(i);
                cell.setCellValue(companyHeaders[i]);
                cell.setCellStyle(boldStyle);
            }

            Row header = sheet.createRow(2);
            String[] headers = {"Nome Funcionário", "Cargo", "Salário (por hora)", "Horas Trabalhadas"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(boldStyle);
            }

            int rowIndex = 3;
            for (EmployeeDto employee : employees) {
                List<TimeRecordsDto> timeRecords = timeRecordsService.getTimeRecordsByEmployeeId(
                        Long.valueOf(employee.getId()),
                        startDate.atStartOfDay(),
                        endDate.atTime(23, 59)
                );

                double totalHours = timeRecordsService.calculateTotalHours(timeRecords);
                totalCompanyHours += totalHours;

                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(employee.getName());
                row.createCell(1).setCellValue(employee.getPosition() != null ? employee.getPosition().getName() : "");
                row.createCell(2).setCellValue(Double.parseDouble(String.format("%.2f", employee.getSalary())));
                row.createCell(3).setCellValue(Double.parseDouble(String.format("%.2f", totalHours)));
            }

            companyRow.createCell(3).setCellValue(totalCompanyHours);

            for (int i = 0; i < 4; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new IOException("Erro ao gerar o relatório de horas por empresa", e);
        }
    }

    public byte[] generateAllCompaniesReportPdf() throws DocumentException {
        com.itextpdf.text.Document document = new com.itextpdf.text.Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, outputStream);

        document.open();

        com.itextpdf.text.Font titleFont = com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA_BOLD, 16);
        com.itextpdf.text.Paragraph title = new com.itextpdf.text.Paragraph("Relatório de Empresas", titleFont);
        title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
        document.add(title);

        document.add(com.itextpdf.text.Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{4, 4, 4});

        com.itextpdf.text.Font headerFont = com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA_BOLD, 12);
        table.addCell(new PdfPCell(new Phrase("Nome", headerFont)));
        table.addCell(new PdfPCell(new Phrase("CNPJ", headerFont)));
        table.addCell(new PdfPCell(new Phrase("Contato", headerFont)));

        List<Company> companies = companyRepository.findAll();
        for (Company company : companies) {
            table.addCell(company.getName());
            table.addCell(company.getCnpj());
            table.addCell(company.getContact());
        }

        document.add(table);
        document.close();

        return outputStream.toByteArray();
    }

    public byte[] generateEmployeeListReportPdf(Integer companyId) throws DocumentException {
        com.itextpdf.text.Document document = new com.itextpdf.text.Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, outputStream);

        document.open();

        com.itextpdf.text.Font titleFont = com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA_BOLD, 16);
        com.itextpdf.text.Paragraph title = new com.itextpdf.text.Paragraph("Relatório de Funcionários", titleFont);
        title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
        document.add(title);

        document.add(com.itextpdf.text.Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{4, 4, 4, 4, 4});

        com.itextpdf.text.Font headerFont = com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA_BOLD, 12);
        table.addCell(new PdfPCell(new Phrase("Nome", headerFont)));
        table.addCell(new PdfPCell(new Phrase("CPF", headerFont)));
        table.addCell(new PdfPCell(new Phrase("Data de Início", headerFont)));
        table.addCell(new PdfPCell(new Phrase("Cargo", headerFont)));
        table.addCell(new PdfPCell(new Phrase("Salário (por hora)", headerFont)));

        List<EmployeeDto> employees = employeeService.getAllEmployeesFromCompany(companyId);
        for (EmployeeDto employee : employees) {
            table.addCell(employee.getName());
            table.addCell(employee.getCpf());
            table.addCell(employee.getStartDate() != null
                    ? employee.getStartDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    : "");
            table.addCell(employee.getPosition() != null ? employee.getPosition().getName() : "");
            table.addCell(String.format("%.2f", employee.getSalary()));
        }

        document.add(table);
        document.close();

        return outputStream.toByteArray();
    }

    public byte[] generateTimeRecordReportPdf(Integer employeeId, LocalDate startDate, LocalDate endDate) throws DocumentException {
        com.itextpdf.text.Document document = new com.itextpdf.text.Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, outputStream);

        document.open();

        com.itextpdf.text.Font titleFont = com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA_BOLD, 16);
        com.itextpdf.text.Paragraph title = new com.itextpdf.text.Paragraph("Relatório de Registros de Tempo", titleFont);
        title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
        document.add(title);

        document.add(com.itextpdf.text.Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{4, 4, 4, 4, 4});

        com.itextpdf.text.Font headerFont = com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA_BOLD, 12);
        table.addCell(new PdfPCell(new Phrase("Entrada", headerFont)));
        table.addCell(new PdfPCell(new Phrase("Editado (Entrada)", headerFont)));
        table.addCell(new PdfPCell(new Phrase("Saída", headerFont)));
        table.addCell(new PdfPCell(new Phrase("Editado (Saída)", headerFont)));
        table.addCell(new PdfPCell(new Phrase("Total de Horas", headerFont)));

        List<TimeRecordsDto> timeRecords = timeRecordsService.getTimeRecordsByEmployeeId(
                Long.valueOf(employeeId),
                startDate.atStartOfDay(),
                endDate.atTime(23, 59)
        );

        for (int i = 0; i < timeRecords.size(); i += 2) {
            TimeRecordsDto entry = timeRecords.get(i);
            table.addCell(entry.getDateTime().toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            table.addCell(entry.getIsEdit() ? "SIM" : "NÃO");

            if (i + 1 < timeRecords.size()) {
                TimeRecordsDto exit = timeRecords.get(i + 1);
                table.addCell(exit.getDateTime().toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                table.addCell(exit.getIsEdit() ? "SIM" : "NÃO");

                double totalHours = timeRecordsService.calculateTotalHours(List.of(entry, exit));
                table.addCell(String.format("%.2f", totalHours));
            } else {
                table.addCell("");
                table.addCell("");
                table.addCell("");
            }
        }

        document.add(table);
        document.close();

        return outputStream.toByteArray();
    }

    public byte[] generateCompanyHoursReportPdf(Integer companyId, LocalDate startDate, LocalDate endDate) throws DocumentException {
        com.itextpdf.text.Document document = new com.itextpdf.text.Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, outputStream);

        document.open();

        com.itextpdf.text.Font titleFont = com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA_BOLD, 16);
        com.itextpdf.text.Paragraph title = new com.itextpdf.text.Paragraph("Relatório de Horas por Empresa", titleFont);
        title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
        document.add(title);

        document.add(com.itextpdf.text.Chunk.NEWLINE);

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada com o ID: " + companyId));
        List<EmployeeDto> employees = employeeService.getAllEmployeesFromCompany(companyId);



        com.itextpdf.text.Font infoFont = com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA, 12);
        com.itextpdf.text.Paragraph companyInfo = new com.itextpdf.text.Paragraph(
                String.format("Empresa: %s\nPeríodo: %s a %s",
                        company.getName(),
                        startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                ),
                infoFont
        );
        document.add(companyInfo);

        document.add(com.itextpdf.text.Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{4, 4, 4, 4});


        com.itextpdf.text.Font headerFont = com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA_BOLD, 12);
        table.addCell(new PdfPCell(new Phrase("Nome Funcionário", headerFont)));
        table.addCell(new PdfPCell(new Phrase("Cargo", headerFont)));
        table.addCell(new PdfPCell(new Phrase("Salário (por hora)", headerFont)));
        table.addCell(new PdfPCell(new Phrase("Horas Trabalhadas", headerFont)));

        double totalCompanyHours = 0;
        for (EmployeeDto employee : employees) {
            List<TimeRecordsDto> timeRecords = timeRecordsService.getTimeRecordsByEmployeeId(
                    Long.valueOf(employee.getId()),
                    startDate.atStartOfDay(),
                    endDate.atTime(23, 59)
            );



            double totalHours = timeRecordsService.calculateTotalHours(timeRecords);
            totalCompanyHours += totalHours;


            table.addCell(employee.getName());
            table.addCell(employee.getPosition() != null ? employee.getPosition().getName() : "");
            table.addCell(String.format("%.2f", employee.getSalary()));
            table.addCell(String.format("%.2f", totalHours));
        }

        com.itextpdf.text.Font totalFont = com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA_BOLD, 12);
        PdfPCell totalCell = new PdfPCell(new Phrase("Total de Horas", totalFont));
        totalCell.setColspan(3);
        totalCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);

        table.addCell(totalCell);
        table.addCell(new PdfPCell(new Phrase(String.format("%.2f", totalCompanyHours), totalFont)));

        document.add(table);
        document.close();

        return outputStream.toByteArray();
    }
}
