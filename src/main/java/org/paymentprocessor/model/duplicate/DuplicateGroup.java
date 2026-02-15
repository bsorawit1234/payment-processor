package org.paymentprocessor.model.duplicate;

import org.paymentprocessor.model.PaymentRequest;
import org.paymentprocessor.model.types.DuplicateRule;

import java.util.List;

public class DuplicateGroup {
    public DuplicateRule rule;
    public String groupKey;
    public List<PaymentRequest> records;

    public DuplicateGroup(DuplicateRule rule, String groupKey, List<PaymentRequest> records) {
        this.rule = rule;
        this.groupKey = groupKey;
        this.records = records;
    }
}
