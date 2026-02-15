package org.paymentprocessor.services.services;

import org.junit.jupiter.api.Test;
import org.paymentprocessor.model.PaymentRequest;
import org.paymentprocessor.model.duplicate.DuplicateReport;
import org.paymentprocessor.model.types.DuplicateRule;
import org.paymentprocessor.model.types.TransactionStatus;
import org.paymentprocessor.services.PaymentDuplicateDetector;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaymentDuplicateDetectorTest {

    @Test
    void shouldDetectTxIdDuplicate() {
        PaymentRequest txid1 = request("tx_same", "m_one", "10", "USD", "SUCCESS", "2026-02-10T10:00:00Z");
        PaymentRequest txid2 = request("tx_same", "m_two", "20", "USD", "FAILED", "2026-02-11T10:00:00Z");

        PaymentDuplicateDetector detector = new PaymentDuplicateDetector();
        DuplicateReport report = detector.detect(List.of(txid1, txid2));

        assertEquals(1, report.txidGroupCount);
        assertEquals(0, report.merchantAmountDayGroupCount);
        assertEquals(1, report.groups.size());

        assertEquals("tx_same", report.groups.get(0).groupKey);
        assertEquals(DuplicateRule.TXID, report.groups.get(0).rule);
    }

    @Test
    void shouldDetectMerchantAmountDayDuplicate() {
        PaymentRequest mad1 = request("tx_a", "m_shop", "99.99", "THB", "SUCCESS", "2026-02-12T01:00:00Z");
        PaymentRequest mad2 = request("tx_b", "m_shop", "99.99", "THB", "PENDING", "2026-02-12T23:00:00Z");

        PaymentDuplicateDetector detector = new PaymentDuplicateDetector();
        DuplicateReport report = detector.detect(List.of(mad1, mad2));

        assertEquals(0, report.txidGroupCount);
        assertEquals(1, report.merchantAmountDayGroupCount);
        assertEquals(1, report.groups.size());

        assertEquals("m_shop_99.99_THB_2026-02-12", report.groups.get(0).groupKey);
        assertEquals(DuplicateRule.MERCHANT_AMOUNT_DAY, report.groups.get(0).rule);
    }


    private PaymentRequest request(
            String transactionId,
            String merchantRef,
            String amount,
            String currency,
            String status,
            String createdAtUtc
    ) {
        PaymentRequest request = new PaymentRequest();
        request.transactionId = transactionId;
        request.merchantRef = merchantRef;
        request.amount = new BigDecimal(amount);
        request.currency = currency;
        request.status = TransactionStatus.valueOf(status);
        request.createdAtUtc = OffsetDateTime.parse(createdAtUtc);
        return request;
    }
}
