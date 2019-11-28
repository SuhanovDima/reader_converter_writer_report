package suhanov.pattern.example.report;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import suhanov.pattern.example.dto.FileData;
import suhanov.pattern.example.dto.Statistics;

import org.springframework.core.io.ResourceLoader;

import static org.apache.poi.ss.usermodel.Row.MissingCellPolicy.CREATE_NULL_AS_BLANK;

@AllArgsConstructor
@Slf4j
public class StatisticsToFileDataConverter implements Function<Statistics, FileData> {

    private final ResourceLoader resourceLoader;

    private final String templateLocation;

    private final String reportName;

    @Override
    public FileData apply(Statistics statistics) {
        try (InputStream stream = resourceLoader.getResource(templateLocation).getInputStream();
             Workbook workbook = new XSSFWorkbook(stream)) {

            workbook.getSheetAt(0).getRow(0).getCell(1, CREATE_NULL_AS_BLANK).setCellValue(statistics.getClickCount());
            workbook.getSheetAt(0).getRow(1).getCell(1, CREATE_NULL_AS_BLANK).setCellValue(statistics.getPaymentTotal().doubleValue());

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            byte[] bytes = out.toByteArray();
            log.info("Statistics was converted to XLSX");
            return new FileData(reportName, bytes);
        } catch (IOException e) {
            throw new IllegalStateException("Could not create XLSX from " + templateLocation, e);
        }
    }
}
