package com.project.Stock.Repository;

import com.project.Stock.Model.StockCandle;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository
        extends CassandraRepository<StockCandle, String> {

    @Query("""
       SELECT *
       FROM stock_candles
       WHERE symbol=?0
    """)
    List<StockCandle> findBySymbolAndDate(
            String symbol);
}
