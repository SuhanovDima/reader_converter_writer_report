package suhanov.pattern.example.config;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.quartz.JobDetail;
import suhanov.pattern.example.dto.FileData;
import suhanov.pattern.example.dto.Statistics;
import suhanov.pattern.example.job.ReportJob;
import suhanov.pattern.example.report.DailyStatisticsProducer;
import suhanov.pattern.example.report.MonthlyStatisticsProducer;
import suhanov.pattern.example.report.ReportProducer;
import suhanov.pattern.example.report.StatisticsToFileDataConverter;
import suhanov.pattern.example.repository.StatisticsRepository;
import suhanov.pattern.example.util.ReportSender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;

@Configuration
public class ReportConfig {

    private static final String TEMPLATE = "classpath:/report-template/report.xlsx";
    private static final String DAILY_REPORT_SUBJECT = "Ежедневный отчет";
    private static final String REPORT_TEXT = "Отчет во вложении";
    private static final String MONTHLY_REPORT_SUBJECT = "Ежемесячный отчет";
    private static final String REPORT_FROM = "\"Instant Payment Server\"<tes-noreply@alfastrah.ru>";
    private static final String REPORT_NAME = "report.xlsx";

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private StatisticsRepository repository;

    @Autowired
    private ResourceLoader resourceLoader;

    @Value("${jobs.dailyReport.cron}")
    private String dailyCron;

    @Value("${jobs.monthlyReport.cron}")
    private String monthlyCron;

    @Value("${report.mail.to}")
    private String reportMailTo;

    @Bean
    public JobDetailFactoryBean dailyReportJob() {
        ReportSender sender = reportSender(DAILY_REPORT_SUBJECT, REPORT_TEXT);
        Supplier<FileData> producer = reportProducer(new DailyStatisticsProducer(repository));
        Consumer<FileData> consumer = sender;

        return reportJobDetail(producer, consumer);
    }

    @Bean
    public JobDetailFactoryBean monthlyReportJob() {
        ReportSender sender = reportSender(MONTHLY_REPORT_SUBJECT, REPORT_TEXT);
        Supplier<FileData> producer = reportProducer(new MonthlyStatisticsProducer(repository));
        Consumer<FileData> consumer = sender;

        return reportJobDetail(producer, consumer);
    }

    @Bean
    public CronTriggerFactoryBean dailyReportTrigger(JobDetail dailyReportJob) {
        CronTriggerFactoryBean factory = new CronTriggerFactoryBean();
        factory.setJobDetail(dailyReportJob);
        factory.setCronExpression(dailyCron);
        return factory;
    }

    @Bean
    public CronTriggerFactoryBean monthlyReportTrigger(JobDetail monthlyReportJob) {
        CronTriggerFactoryBean factory = new CronTriggerFactoryBean();
        factory.setJobDetail(monthlyReportJob);
        factory.setCronExpression(monthlyCron);
        return factory;
    }

    private ReportSender reportSender(String subject, String text) {
        return new ReportSender(mailSender, REPORT_FROM, reportMailTo, subject, text);
    }

    private ReportProducer reportProducer(Supplier<Statistics> statisticsProducer) {
        return new ReportProducer(statisticsProducer, new StatisticsToFileDataConverter(resourceLoader, TEMPLATE, REPORT_NAME));
    }

    private JobDetailFactoryBean reportJobDetail(Supplier<FileData> producer, Consumer<FileData> consumer) {
        Map<String, Object> jobData = new HashMap<>();
        jobData.put("producer", producer);
        jobData.put("consumer", consumer);

        JobDetailFactoryBean factory = new JobDetailFactoryBean();
        factory.setJobClass(ReportJob.class);
        factory.setJobDataAsMap(jobData);
        factory.setDurability(true);
        return factory;
    }
}
