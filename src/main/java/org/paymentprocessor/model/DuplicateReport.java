package org.paymentprocessor.model;

import java.util.List;

public class DuplicateReport {
    public List<DuplicateGroup> groups;
    public int txidGroupCount;
    public int merchantAmountDayGroupCount;

    public DuplicateReport(List<DuplicateGroup> groups, int txidGroupCount, int merchantAmountDayGroupCount) {
        this.groups = groups;
        this.txidGroupCount = txidGroupCount;
        this.merchantAmountDayGroupCount = merchantAmountDayGroupCount;
    }
}
