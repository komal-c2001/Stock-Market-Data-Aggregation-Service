package com.project.Stock.ingestion;

import com.project.Stock.Model.StockCandle;
import com.project.Stock.Repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class CsvImportRunner implements CommandLineRunner {

    private final StockRepository repository;

    @Override
    public void run(String... args) throws Exception {

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new ClassPathResource("stock_data.csv")
                                .getInputStream()));

        String line;

        // Skip header
        reader.readLine();

        while ((line = reader.readLine()) != null) {

            String[] row = line.split(",");

            LocalDateTime dateTime =
                    LocalDateTime.parse(
                            row[1],
                            DateTimeFormatter.ofPattern(
                                    "yyyy-MM-dd HH:mm:ss"));

            StockCandle candle = new StockCandle();

            candle.setSymbol(row[0]);
            candle.setDatetime(
                    dateTime.toInstant(ZoneOffset.UTC));

            candle.setOpen(new BigDecimal(row[2]));
            candle.setHigh(new BigDecimal(row[3]));
            candle.setLow(new BigDecimal(row[4]));
            candle.setClose(new BigDecimal(row[5]));
            candle.setVolume(Long.parseLong(row[6]));

            //System.out.println("Saving: " + row[0] + " " + row[1]);

            repository.save(candle);
        }

        System.out.println("CSV Imported Successfully");
    }
}