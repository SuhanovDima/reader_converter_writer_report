package suhanov.pattern.example.report;

import java.util.function.Function;
import java.util.function.Supplier;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import suhanov.pattern.example.dto.FileData;
import suhanov.pattern.example.dto.Statistics;

@AllArgsConstructor
@Slf4j
public class ReportProducer implements Supplier<FileData> {

    private final Supplier<Statistics> producer;

    private final Function<Statistics, FileData> converter;

    @Override
    public FileData get() {
        return converter.apply(producer.get());
    }
}
