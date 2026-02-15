package org.paymentprocessor.model.report;

import java.util.List;
import java.util.Map;

public class PaymentReport {
    public int totalRecords;
    public int validRecords;
    public int invalidRecords;
    public Map<String, Integer> invalidReasons;
    public Map<String, Integer> statusCounts;
    public AmountStats successAmountStats;
    public int duplicateCount;
    public List<DuplicateGroupReportItem> duplicateGroups;

    public PaymentReport(
            int totalRecords,
            int validRecords,
            int invalidRecords,
            Map<String, Integer> invalidReasons,
            Map<String, Integer> statusCounts,
            AmountStats successAmountStats,
            int duplicateCount,
            List<DuplicateGroupReportItem> duplicateGroups
    ) {
        this.totalRecords = totalRecords;
        this.validRecords = validRecords;
        this.invalidRecords = invalidRecords;
        this.invalidReasons = invalidReasons;
        this.statusCounts = statusCounts;
        this.successAmountStats = successAmountStats;
        this.duplicateCount = duplicateCount;
        this.duplicateGroups = duplicateGroups;
    }
}
