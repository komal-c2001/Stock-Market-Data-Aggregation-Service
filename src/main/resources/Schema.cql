1.CREATE KEYSPACE IF NOT EXISTS stock_market
WITH replication = {
  'class': 'SimpleStrategy',
  'replication_factor': 1
};
2.DESCRIBE KEYSPACES

3.USE stock_market;

3.CREATE TABLE stock_candles (
      symbol text,
      datetime timestamp,
      open double,
      high double,
      low double,
      close double,
      volume bigint,
      PRIMARY KEY ((symbol), datetime)
  );

5.DESCRIBE TABLES;

6.SELECT *
  FROM stock_candles limit 5;

7.SELECT *
  FROM stock_candles
  WHERE symbol = 'RELIANCE';