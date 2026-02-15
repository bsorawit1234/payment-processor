package org.paymentprocessor.services.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.paymentprocessor.services.PaymentProcessor;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentProcessorSummaryTest {

    @Test
    void shouldCountTotalValidAndInvalidRecords() throws Exception {
        JsonNode report = buildReport();
        assertEquals(5, report.get("totalRecords").asInt());
        assertEquals(4, report.get("validRecords").asInt());
        assertEquals(1, report.get("invalidRecords").asInt());
    }

    @Test
    void shouldCountInvalidReasons() throws Exception {
        JsonNode report = buildReport();
        assertEquals(1, report.get("invalidReasons").get("Invalid amount: must be > 0").asInt());
    }

    @Test
    void shouldCountStatusesWithIdempotentSuccess() throws Exception {
        JsonNode report = buildReport();
        assertEquals(1, report.get("statusCounts").get("SUCCESS").asInt());
        assertEquals(1, report.get("statusCounts").get("FAILED").asInt());
        assertEquals(1, report.get("statusCounts").get("PENDING").asInt());
    }

    @Test
    void shouldComputeSuccessAmountStatsFromIdempotentSuccessRecords() throws Exception {
        JsonNode report = buildReport();
        assertEquals(0, report.get("successAmountStats").get("min").decimalValue().compareTo(new BigDecimal("100.00")));
        assertEquals(0, report.get("successAmountStats").get("max").decimalValue().compareTo(new BigDecimal("100.00")));
        assertEquals(0, report.get("successAmountStats").get("avg").decimalValue().compareTo(new BigDecimal("100.00")));
    }

    @Test
    void shouldCountDuplicateGroups() throws Exception {
        JsonNode report = buildReport();
        assertEquals(1, report.get("duplicateCount").asInt());
    }

    private JsonNode buildReport() throws Exception {
        String inputJson = """
                [
                  {
                    "transactionId": "tx_1",
                    "merchantRef": "m_1",
                    "amount": 100.00,
                    "currency": "USD",
                    "status": "SUCCESS",
                    "createdAtUtc": "2026-02-10T08:00:00Z"
                  },
                  {
                    "transactionId": "tx_1",
                    "merchantRef": "m_2",
                    "amount": 150.00,
                    "currency": "USD",
                    "status": "SUCCESS",
                    "createdAtUtc": "2026-02-10T09:00:00Z"
                  },
                  {
                    "transactionId": "tx_2",
                    "merchantRef": "m_3",
                    "amount": 55.00,
                    "currency": "USD",
                    "status": "FAILED",
                    "createdAtUtc": "2026-02-10T10:00:00Z"
                  },
                  {
                    "transactionId": "tx_3",
                    "merchantRef": "m_4",
                    "amount": 70.00,
                    "currency": "USD",
                    "status": "PENDING",
                    "createdAtUtc": "2026-02-10T11:00:00Z"
                  },
                  {
                    "transactionId": "tx_bad",
                    "merchantRef": "m_5",
                    "amount": 0,
                    "currency": "USD",
                    "status": "SUCCESS",
                    "createdAtUtc": "2026-02-10T12:00:00Z"
                  }
                ]
                """;

        Path tempDir = Files.createTempDirectory("payment-processor-test");
        Path inputPath = tempDir.resolve("transactions.json");
        Path outputPath = tempDir.resolve("report.json");
        Files.writeString(inputPath, inputJson);

        PaymentProcessor processor = new PaymentProcessor();
        processor.process(inputPath.toFile(), outputPath.toFile());

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(new File(outputPath.toString()));
    }
}
