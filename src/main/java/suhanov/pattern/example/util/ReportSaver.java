package suhanov.pattern.example.util;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.function.Consumer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import suhanov.pattern.example.dto.FileData;

@AllArgsConstructor
@Slf4j
public class ReportSaver implements Consumer<FileData> {

    private final String directory;

    @Override
    public void accept(FileData data) {
        try (OutputStream writer = new BufferedOutputStream(Files.newOutputStream(Paths.get(directory, data.getFileName())))) {
            writer.write(data.getContent());

            log.info("{} was saved at {}", data.getFileName(), directory);
        } catch (IOException e) {
            throw new IllegalStateException(MessageFormat.format("Could not save {0} at {1}", data.getFileName(), directory), e);
        }
    }
}
