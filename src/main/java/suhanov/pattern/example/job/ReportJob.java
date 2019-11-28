package suhanov.pattern.example.job;

import java.util.function.Consumer;
import java.util.function.Supplier;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import suhanov.pattern.example.dto.FileData;

import org.springframework.scheduling.quartz.QuartzJobBean;

@Slf4j
public class ReportJob extends QuartzJobBean {

    @Setter
    private Supplier<FileData> producer;
    @Setter
    private Consumer<FileData> consumer;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        consumer.accept(producer.get());
    }
}
