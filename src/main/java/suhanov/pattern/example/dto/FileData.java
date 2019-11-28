package suhanov.pattern.example.dto;

import lombok.Value;

@Value
public class FileData {

    private String fileName;

    private byte[] content;
}
