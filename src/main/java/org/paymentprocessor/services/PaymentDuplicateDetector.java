package org.paymentprocessor.services;

import org.paymentprocessor.model.DuplicateGroup;
import org.paymentprocessor.model.DuplicateReport;
import org.paymentprocessor.model.PaymentRequest;
import org.paymentprocessor.model.types.DuplicateRule;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PaymentDuplicateDetector {

    public DuplicateReport detect(List<PaymentRequest> requests) {
        Map<String, List<PaymentRequest>> byTxId = new LinkedHashMap<>();
        for (PaymentRequest request : requests) {
            if (!byTxId.containsKey(request.transactionId)) {
                byTxId.put(request.transactionId, new ArrayList<>());
            }
            byTxId.get(request.transactionId).add(request);
        }

        Map<String, List<PaymentRequest>> byMerchantAmountDay = new LinkedHashMap<>();
        for (PaymentRequest request : requests) {
            String key = buildMerchantAmountDayKey(request);
            if (!byMerchantAmountDay.containsKey(key)) {
                byMerchantAmountDay.put(key, new ArrayList<>());
            }
            byMerchantAmountDay.get(key).add(request);
        }

        List<DuplicateGroup> groups = new ArrayList<>();
        int txidGroupCount = 0;
        int merchantAmountDayGroupCount = 0;

        for (String txId : byTxId.keySet()) {
            List<PaymentRequest> records = byTxId.get(txId);
            if (records.size() <= 1) {
                continue;
            }

            txidGroupCount = txidGroupCount + 1;
            DuplicateGroup group = new DuplicateGroup(
                    DuplicateRule.TXID,
                    txId,
                    records
            );
            groups.add(group);
        }

        for (String key : byMerchantAmountDay.keySet()) {
            List<PaymentRequest> records = byMerchantAmountDay.get(key);
            if (records.size() <= 1) {
                continue;
            }

            merchantAmountDayGroupCount = merchantAmountDayGroupCount + 1;
            DuplicateGroup group = new DuplicateGroup(
                    DuplicateRule.MERCHANT_AMOUNT_DAY,
                    key,
                    records
            );
            groups.add(group);
        }

        return new DuplicateReport(groups, txidGroupCount, merchantAmountDayGroupCount);
    }

    private String buildMerchantAmountDayKey(PaymentRequest request) {
        String merchantRef = request.merchantRef == null ? "" : request.merchantRef.trim();
        BigDecimal amount = request.amount == null ? BigDecimal.ZERO : request.amount;
        String normalizedAmount = amount.stripTrailingZeros().toPlainString();
        String currency = request.currency == null ? "" : request.currency.trim().toUpperCase();
        LocalDate utcDay = request.createdAtUtc.withOffsetSameInstant(ZoneOffset.UTC).toLocalDate();

        return merchantRef + "_" + normalizedAmount + "_" + currency + "_" + utcDay;
    }
}
