package org.paymentprocessor.utils;

import org.paymentprocessor.model.PaymentRequest;
import org.paymentprocessor.model.report.AmountStats;
import org.paymentprocessor.model.types.TransactionStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PaymentSummaryUtils {

    public Map<String, Integer> buildStatusCounts(List<PaymentRequest> validRequests) {
        Map<String, Integer> statusCounts = new LinkedHashMap<>();
        statusCounts.put(TransactionStatus.SUCCESS.name(), 0);
        statusCounts.put(TransactionStatus.FAILED.name(), 0);
        statusCounts.put(TransactionStatus.PENDING.name(), 0);

        for (PaymentRequest request : validRequests) {
            String statusName = request.status.name();
            statusCounts.put(statusName, statusCounts.get(statusName) + 1);
        }
        return statusCounts;
    }

    public AmountStats computeSuccessAmountStats(List<PaymentRequest> validRequests) {
        BigDecimal min = null;
        BigDecimal max = null;
        BigDecimal sum = BigDecimal.ZERO;
        int count = 0;

        for (PaymentRequest request : validRequests) {
            if (request.status != TransactionStatus.SUCCESS) {
                continue;
            }

            BigDecimal amount = request.amount;
            if (min == null || amount.compareTo(min) < 0) {
                min = amount;
            }
            if (max == null || amount.compareTo(max) > 0) {
                max = amount;
            }

            sum = sum.add(amount);
            count = count + 1;
        }

        if (count == 0) {
            return new AmountStats(null, null, null);
        }

        BigDecimal avg = sum.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
        return new AmountStats(min, max, avg);
    }
}
