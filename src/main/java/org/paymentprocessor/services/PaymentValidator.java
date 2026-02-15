package org.paymentprocessor.services;

import com.fasterxml.jackson.databind.JsonNode;
import org.paymentprocessor.model.PaymentRequest;
import org.paymentprocessor.model.types.TransactionStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class PaymentValidator {

    private static final Pattern THREE_LETTER_CURRENCY = Pattern.compile("^[A-Za-z]{3}$");

    public ValidationReport getValidatedRequests(List<JsonNode> requests) {
        List<PaymentRequest> validatedPaymentRequests = new ArrayList<>();
        Map<String, Integer> invalidReasonCounts = new LinkedHashMap<>();
        int invalidRecordCount = 0;

        for (JsonNode request : requests) {
            ValidationError validationError = validateRecord(request);
            if (validationError != null) {
                invalidRecordCount++;
                invalidReasonCounts.merge(validationError.reason(), 1, Integer::sum);
                continue;
            }

            validatedPaymentRequests.add(toPaymentRequest(request));
        }

        return new ValidationReport(validatedPaymentRequests, invalidRecordCount, invalidReasonCounts);
    }

    private ValidationError validateRecord(JsonNode request) {
        if (request == null || request.isNull() || !request.isObject()) {
            return new ValidationError("Record is not a valid JSON object");
        }

        if (isBlankField(request, "transactionId")) {
            return new ValidationError("Missing required field: transactionId");
        }
        if (isBlankField(request, "merchantRef")) {
            return new ValidationError("Missing required field: merchantRef");
        }
        if (isMissingOrNull(request, "amount")) {
            return new ValidationError("Missing required field: amount");
        }
        if (isBlankField(request, "currency")) {
            return new ValidationError("Missing required field: currency");
        }
        if (isBlankField(request, "status")) {
            return new ValidationError("Missing required field: status");
        }
        if (isBlankField(request, "createdAtUtc")) {
            return new ValidationError("Missing required field: createdAtUtc");
        }

        BigDecimal amount = parseAmount(request.get("amount"));
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return new ValidationError("Invalid amount: must be > 0");
        }

        String currency = request.get("currency").asText().trim();
        if (!THREE_LETTER_CURRENCY.matcher(currency).matches()) {
            return new ValidationError("Invalid currency: must be exactly 3 letters");
        }

        String statusRaw = request.get("status").asText().trim();
        try {
            TransactionStatus.valueOf(statusRaw);
        } catch (IllegalArgumentException ex) {
            return new ValidationError("Invalid status: must be SUCCESS, FAILED, or PENDING");
        }

        String createdAtUtcRaw = request.get("createdAtUtc").asText().trim();
        try {
            OffsetDateTime parsed = OffsetDateTime.parse(createdAtUtcRaw);
            if (!parsed.getOffset().equals(ZoneOffset.UTC)) {
                return new ValidationError("Invalid createdAtUtc: must use UTC offset (+00:00 or Z)");
            }
        } catch (Exception ex) {
            return new ValidationError("Invalid createdAtUtc: must be ISO-8601 UTC timestamp");
        }

        return null;
    }

    private PaymentRequest toPaymentRequest(JsonNode request) {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.transactionId = request.get("transactionId").asText().trim();
        paymentRequest.merchantRef = request.get("merchantRef").asText().trim();
        paymentRequest.amount = parseAmount(request.get("amount"));
        paymentRequest.currency = request.get("currency").asText().trim().toUpperCase();
        paymentRequest.status = TransactionStatus.valueOf(request.get("status").asText().trim());
        paymentRequest.createdAtUtc = OffsetDateTime.parse(request.get("createdAtUtc").asText().trim());

        return paymentRequest;
    }

    private boolean isMissingOrNull(JsonNode request, String fieldName) {
        JsonNode node = request.get(fieldName);
        return node == null || node.isNull();
    }

    private boolean isBlankField(JsonNode request, String fieldName) {
        if (isMissingOrNull(request, fieldName)) {
            return true;
        }
        JsonNode node = request.get(fieldName);
        return node.isTextual() && node.asText().trim().isEmpty();
    }

    private BigDecimal parseAmount(JsonNode amountNode) {
        if (amountNode == null || amountNode.isNull()) {
            return null;
        }
        if (amountNode.isNumber()) {
            return amountNode.decimalValue();
        }
        if (amountNode.isTextual()) {
            try {
                return new BigDecimal(amountNode.asText().trim());
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        return null;
    }

    public record ValidationReport(
            List<PaymentRequest> validRequests,
            int invalidRecordCount,
            Map<String, Integer> invalidReasonCounts
    ) {
        public ValidationReport {
            validRequests = List.copyOf(validRequests);
            invalidReasonCounts = Collections.unmodifiableMap(new LinkedHashMap<>(invalidReasonCounts));
        }
    }

    private record ValidationError(String reason) {
    }
}
