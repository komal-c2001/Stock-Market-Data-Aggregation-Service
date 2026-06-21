package com.project.Stock.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class candleResponse {

    private String symbol;
    private String timeframe;
    private List<AggregatedCandle> candles;
    private Integer count;
}