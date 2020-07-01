package com.example.gecko.parser;

import com.example.gecko.exception.GeckoServiceException;
import com.example.gecko.model.Record;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Component
public class MultipartFileParser {

    private static final String HEADER = "PRIMARY_KEY,NAME,DESCRIPTION,UPDATED_TIMESTAMP";
    private static final String SEPARATOR = ",";

    public List<Record> parse(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return Collections.emptyList();
        }
        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new GeckoServiceException("Failed to parse file");
        }
        List<String> lines = Arrays.stream(new String(bytes).split("\\r?\\n"))
                                   .filter(Strings::isNotBlank)
                                   .collect(toList());
        if (lines.size() < 2) {
            throw new GeckoServiceException("Unexpected number of lines");
        }
        if (!isValidHeader(lines.get(0))) {
            throw new GeckoServiceException("Unexpected header received");
        }
        int expectedNumberOfColumns = HEADER.split(SEPARATOR).length;
        return lines.subList(1, lines.size()).stream()
                    .map(line -> toRecord(line, expectedNumberOfColumns))
                    .collect(Collectors.toList());
    }

    private Record toRecord(String line, int expectedNumberOfColumns) {
        List<String> columns = Arrays.stream(line.split(SEPARATOR))
                                     .map(String::trim)
                                     .filter(Strings::isNotBlank)
                                     .collect(toList());
        if (columns.size() != expectedNumberOfColumns) {
            throw new GeckoServiceException("Unexpected number of columns in line " + line);
        }

        Long updatedTimestamp;
        try {
            updatedTimestamp = Long.parseLong(columns.get(3));
        } catch (NumberFormatException e) {
            throw new GeckoServiceException("Failed to parse UPDATED_TIMESTAMP");
        }

        return new Record()
                .setPrimaryKey(columns.get(0))
                .setName(columns.get(1))
                .setDescription(columns.get(2))
                .setUpdatedTimestamp(updatedTimestamp);
    }

    private boolean isValidHeader(String header) {
        return HEADER.equalsIgnoreCase(header.replaceAll("\\s+", ""));
    }
}
