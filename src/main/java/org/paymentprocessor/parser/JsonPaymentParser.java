package org.paymentprocessor.parser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JsonPaymentParser {
    private final ObjectMapper objectMapper;

    public JsonPaymentParser() {
        this.objectMapper = JsonMapper.builder().findAndAddModules().build();
    }

    public List<JsonNode> readInputFile(String filePath) {
        try {
            File inputFile = new File(filePath);
            return objectMapper.readValue(inputFile, new TypeReference<List<JsonNode>>() {});
        } catch (IOException e) {
            System.err.println(e);
            throw new RuntimeException("Failed to parse the input JSON file at: " + filePath, e);
        }
    }
}
