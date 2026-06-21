package com.project.Stock.Model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;

@Data
public class AggregatedCandle {

    private Instant datetime;
    private BigDecimal open;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal close;
    private Long volume;
}