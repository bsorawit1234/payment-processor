package org.paymentprocessor.services.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.paymentprocessor.services.PaymentValidator;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentValidatorTest {

    @Test
    void shouldCountValidAndInvalidRecords() throws Exception {
        PaymentValidator.ValidationReport report = buildReport();
        assertEquals(1, report.validRequests().size());
        assertEquals(2, report.invalidRecordCount());
    }

    @Test
    void shouldCountInvalidAmountReason() throws Exception {
        PaymentValidator.ValidationReport report = buildReport();
        assertEquals(1, report.invalidReasonCounts().get("Invalid amount: must be > 0"));
    }

    @Test
    void shouldCountMissingMerchantRefReason() throws Exception {
        PaymentValidator.ValidationReport report = buildReport();
        assertEquals(1, report.invalidReasonCounts().get("Missing required field: merchantRef"));
    }

    private PaymentValidator.ValidationReport buildReport() throws Exception {
        String json = """
                [
                  {
                    "transactionId": "tx_valid_1",
                    "merchantRef": "m_1",
                    "amount": 99.5,
                    "currency": "USD",
                    "status": "SUCCESS",
                    "createdAtUtc": "2026-02-10T08:15:30Z"
                  },
                  {
                    "transactionId": "tx_bad_amount",
                    "merchantRef": "m_2",
                    "amount": 0,
                    "currency": "USD",
                    "status": "SUCCESS",
                    "createdAtUtc": "2026-02-10T08:16:30Z"
                  },
                  {
                    "transactionId": "tx_missing_ref",
                    "amount": 10,
                    "currency": "USD",
                    "status": "PENDING",
                    "createdAtUtc": "2026-02-10T08:17:30Z"
                  }
                ]
                """;

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);
        List<JsonNode> records = new ArrayList<>();
        for (JsonNode node : root) {
            records.add(node);
        }

        PaymentValidator validator = new PaymentValidator();
        return validator.getValidatedRequests(records);
    }
}
