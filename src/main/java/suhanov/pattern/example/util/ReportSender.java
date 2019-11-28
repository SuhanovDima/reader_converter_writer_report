package suhanov.pattern.example.util;

import java.text.MessageFormat;
import java.util.function.Consumer;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import suhanov.pattern.example.dto.FileData;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.apache.commons.lang3.StringUtils;

@AllArgsConstructor
@Slf4j
public class ReportSender implements Consumer<FileData> {

    private final JavaMailSender mailSender;

    private final String mailFrom;
    private final String mailTo;
    private final String mailSubject;
    private final String mailText;

    @Override
    public void accept(FileData data) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(mailFrom);
            helper.setTo(StringUtils.split(mailTo, ','));
            helper.setSubject(mailSubject);
            helper.setText(mailText);
            helper.addAttachment(data.getFileName(), new ByteArrayResource(data.getContent()));

            mailSender.send(message);

            log.info("{} was sent to {}", data.getFileName(), mailTo);
        } catch (MessagingException | MailException e) {
            throw new IllegalStateException(MessageFormat.format("Could not send {0} to {1}", data.getFileName(), mailTo), e);
        }
    }
}
