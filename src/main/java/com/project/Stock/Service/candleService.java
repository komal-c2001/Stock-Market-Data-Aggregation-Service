package com.project.Stock.Service;

import com.project.Stock.Model.AggregatedCandle;
import com.project.Stock.Model.StockCandle;
import com.project.Stock.Model.candleResponse;
import com.project.Stock.Repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Service
@RequiredArgsConstructor
public class candleService {

    private final StockRepository repository;

    public candleResponse getCandles(
            String symbol,
            String timeframe,
            LocalDateTime startDate,
            LocalDateTime endDate) {

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                    "Start date cannot be greater than end date");
        }

        int intervalMinutes = getTimeframeMinutes(timeframe);

        List<StockCandle> allCandles = new ArrayList<>();

        LocalDate currentDate = startDate.toLocalDate();

        while (!currentDate.isAfter(endDate.toLocalDate())) {

            List dailyData =
                    repository.findBySymbolAndDate(
                            symbol);

            allCandles.addAll(dailyData);

            currentDate = currentDate.plusDays(1);
        }

        allCandles = allCandles.stream()
                .filter(c ->
                        !c.getDatetime().isBefore(
                                startDate.toInstant(
                                        ZoneOffset.UTC))
                                &&
                                !c.getDatetime().isAfter(
                                        endDate.toInstant(
                                                ZoneOffset.UTC)))
                .sorted(Comparator.comparing(
                        StockCandle::getDatetime))
                .toList();

        List<AggregatedCandle> candles =
                aggregateCandles(
                        allCandles,
                        intervalMinutes);

        return new candleResponse(
                symbol,
                timeframe,
                candles,
                candles.size());
    }

    private int getTimeframeMinutes(String timeframe) {

        return switch (timeframe) {

            case "1m" -> 1;
            case "5m" -> 5;
            case "15m" -> 15;
            case "30m" -> 30;
            case "1h" -> 60;
            case "1d" -> 1440;

            default ->
                    throw new IllegalArgumentException(
                            "Unsupported timeframe: "
                                    + timeframe);
        };
    }

    private List<AggregatedCandle> aggregateCandles(
            List<StockCandle> candles,
            int intervalMinutes) {

        Map<Instant, List<StockCandle>> grouped =
                new TreeMap<>();

        for (StockCandle candle : candles) {

            long epochSeconds =
                    candle.getDatetime()
                            .getEpochSecond();

            long bucketStart =
                    (epochSeconds /
                            (intervalMinutes * 60L))
                            * (intervalMinutes * 60L);

            Instant bucket =
                    Instant.ofEpochSecond(bucketStart);

            grouped.computeIfAbsent(
                            bucket,
                            k -> new ArrayList<>())
                    .add(candle);
        }

        List<AggregatedCandle> result =
                new ArrayList<>();

        for (Map.Entry<Instant,
                List<StockCandle>> entry
                : grouped.entrySet()) {

            List<StockCandle> group =
                    entry.getValue();

            group.sort(
                    Comparator.comparing(
                            StockCandle::getDatetime));

            AggregatedCandle aggregated =
                    new AggregatedCandle();

            aggregated.setDatetime(
                    entry.getKey());

            aggregated.setOpen(
                    group.get(0).getOpen());

            aggregated.setClose(
                    group.get(group.size() - 1)
                            .getClose());
            BigDecimal high =
                    group.stream()
                            .map(StockCandle::getHigh)
                            .max(BigDecimal::compareTo)
                            .orElse(BigDecimal.ZERO);

            aggregated.setHigh(high);

            BigDecimal low =
                    group.stream()
                            .map(StockCandle::getLow)
                            .min(BigDecimal::compareTo)
                            .orElse(BigDecimal.ZERO);

            aggregated.setLow(low);
            long volume =
                    group.stream()
                            .mapToLong(
                                    StockCandle::getVolume)
                            .sum();

            aggregated.setVolume(volume);

            result.add(aggregated);
        }

        return result;
    }
}