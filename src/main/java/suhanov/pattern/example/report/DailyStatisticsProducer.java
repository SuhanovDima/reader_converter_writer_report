package suhanov.pattern.example.report;

import java.time.LocalDate;
import java.util.function.Supplier;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import suhanov.pattern.example.dto.Statistics;
import suhanov.pattern.example.repository.StatisticsRepository;

@AllArgsConstructor
@Slf4j
public class DailyStatisticsProducer implements Supplier<Statistics> {

    private final StatisticsRepository repository;

    @Override
    public Statistics get() {
        return repository.getStatisticsByDate(LocalDate.now());
    }
}
