package org.paymentprocessor.services;

import org.paymentprocessor.model.duplicate.DuplicateGroup;
import org.paymentprocessor.model.report.DuplicateGroupReportItem;
import org.paymentprocessor.model.report.PaymentReport;
import org.paymentprocessor.parser.JsonPaymentParser;
import org.paymentprocessor.utils.PaymentSummaryUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PaymentProcessor {

    private final JsonPaymentParser parser;
    private final PaymentValidator validator;
    private final PaymentDuplicateDetector duplicateDetector;
    private final PaymentSummaryUtils paymentSummaryUtils;

    public PaymentProcessor() {
        this.parser = new JsonPaymentParser();
        this.validator = new PaymentValidator();
        this.duplicateDetector = new PaymentDuplicateDetector();
        this.paymentSummaryUtils = new PaymentSummaryUtils();
    }

    public PaymentReport process(File inputFile, File outputFile) {
        var rawRequests = parser.readInputFile(inputFile.getPath());
        var validationReport = validator.getValidatedRequests(rawRequests);
        var duplicateReport = duplicateDetector.detect(validationReport.validRequests());

        int totalRecords = rawRequests.size();
        int validRecords = validationReport.validRequests().size();
        int invalidRecords = validationReport.invalidRecordCount();

        var statusCounts = paymentSummaryUtils.buildStatusCounts(validationReport.validRequests());
        var successAmountStats = paymentSummaryUtils.computeSuccessAmountStats(validationReport.validRequests());
        List<DuplicateGroupReportItem> duplicateGroups = toDuplicateGroupItems(duplicateReport.groups);

        PaymentReport report = new PaymentReport(
                totalRecords,
                validRecords,
                invalidRecords,
                validationReport.invalidReasonCounts(),
                statusCounts,
                successAmountStats,
                duplicateReport.groups.size(),
                duplicateGroups
        );

        parser.writeOutputFile(outputFile.getPath(), report);
        return report;
    }

    private List<DuplicateGroupReportItem> toDuplicateGroupItems(List<DuplicateGroup> groups) {
        List<DuplicateGroupReportItem> items = new ArrayList<>();
        for (DuplicateGroup group : groups) {
            items.add(new DuplicateGroupReportItem(group.rule.name(), group.groupKey, group.records));
        }
        return items;
    }
}
