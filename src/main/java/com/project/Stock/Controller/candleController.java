package com.project.Stock.Controller;

import com.project.Stock.Model.candleResponse;
import com.project.Stock.Service.candleService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:8081")
@RequiredArgsConstructor
public class candleController {

    private final candleService candleService;

    @GetMapping("/v1/candles")
    public ResponseEntity<candleResponse> getCandles(

            @RequestParam String symbol,
            @RequestParam String timeframe,

            @RequestParam
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime start_date,

            @RequestParam
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime end_date) {

            candleResponse response =
                candleService.getCandles(
                        symbol,
                        timeframe,
                        start_date,
                        end_date);

        return ResponseEntity.ok(response);
    }
}
