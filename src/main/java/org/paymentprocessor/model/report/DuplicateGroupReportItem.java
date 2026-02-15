package org.paymentprocessor.model.report;

import org.paymentprocessor.model.PaymentRequest;

import java.util.List;

public class DuplicateGroupReportItem {
    public String rule;
    public String groupKey;
    public List<PaymentRequest> records;

    public DuplicateGroupReportItem(String rule, String groupKey, List<PaymentRequest> records) {
        this.rule = rule;
        this.groupKey = groupKey;
        this.records = records;
    }
}
