package org.paymentprocessor.model;

import org.paymentprocessor.model.types.TransactionStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class PaymentRequest {
    public String transactionId;
    public String merchantRef;
    public BigDecimal amount;
    public String currency;
    public TransactionStatus status;
    public OffsetDateTime createdAtUtc;

    @Override
    public String toString() {
        return "PaymentRequest{" +
                "transactionId='" + transactionId + '\'' +
                ", merchantRef='" + merchantRef + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", status=" + status +
                ", createdAtUtc=" + createdAtUtc +
                '}';
    }
}