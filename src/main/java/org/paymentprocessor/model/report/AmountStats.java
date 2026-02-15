package org.paymentprocessor.model.report;

import java.math.BigDecimal;

public class AmountStats {
    public BigDecimal min;
    public BigDecimal max;
    public BigDecimal avg;

    public AmountStats(BigDecimal min, BigDecimal max, BigDecimal avg) {
        this.min = min;
        this.max = max;
        this.avg = avg;
    }
}
