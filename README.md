# Stock Market Data Aggregation Service

## Overview

This project provides a stock market candle aggregation service that:

* Stores 1-minute stock candle data in Cassandra.
* Aggregates candle data into higher timeframes (5m, 15m, 30m, 1h, etc.).
* Exposes REST APIs to retrieve aggregated candles.
* Provides a Vue.js frontend for querying and visualizing candle data.

---

## Technology Stack

### Backend

* Java 17
* Spring Boot
* Spring Data Cassandra
* Maven

### Database

* Apache Cassandra

### Frontend

* Vue.js
* Axios

---

# Project Structure

```
project/

├── Stock-Market-Data-Aggregation-Service
│   ├── src
│   ├── pom.xml
│   └── sample_data.csv
│
└── Stock-Market-Data-Aggregation-Service-UI
    ├── src
    ├── public
    └── package.json
```

---

# Prerequisites

Install the following:

* Java 17+
* Maven 3.8+
* Apache Cassandra 4.x
* Node.js 18+
* npm

---

# Cassandra Setup

Start Cassandra:

```bash
cassandra
```

Open CQL shell:

```bash
cqlsh
```

Create keyspace:

```sql
CREATE KEYSPACE stock_market
WITH replication = {
'class':'SimpleStrategy',
'replication_factor':1
};
```

Use keyspace:

```sql
USE stock_market;
```

Create table:

```sql
CREATE TABLE stock_candles (
    symbol text,
    datetime timestamp,
    open decimal,
    high decimal,
    low decimal,
    close decimal,
    volume bigint,
    PRIMARY KEY(symbol, datetime)
) WITH CLUSTERING ORDER BY (datetime ASC);
```

---

# Configure Backend

Update:

```properties
src/main/resources/application.properties
```

```properties
spring.cassandra.contact-points=localhost
spring.cassandra.port=9042
spring.cassandra.keyspace-name=stock_market
spring.cassandra.local-datacenter=datacenter1
```

---

# Data Ingestion

The project includes a CSV ingestion utility that loads 1-minute candle data into Cassandra.

### Input CSV Format

```csv
symbol,datetime,open,high,low,close,volume
RELIANCE,2026-01-01 09:15:00,1572.5,1577.8,1572.0,1577.5,10000
```

### Run Data Ingestion

```bash
mvn spring-boot:run
```

or

```bash
java -jar target/stock-service.jar
```

The ingestion component reads the CSV and inserts records into Cassandra.

Verify:

```sql
SELECT * FROM stock_candles
WHERE symbol='RELIANCE'
LIMIT 5;
```

---

# Running the Backend Service

Navigate to backend project:

```bash
cd Stock-Market-Data-Aggregation-Service
```

Build:

```bash
mvn clean install
```

Run:

```bash
mvn spring-boot:run
```

Backend starts on:

```text
http://localhost:8080
```

---

# REST API

## Get Aggregated Candles

### Request

```http
GET /api/candles
```

### Example

```http
GET http://localhost:8080/api/candles?symbol=RELIANCE&timeframe=5m&start=2026-01-01T09:15:00&end=2026-01-01T10:00:00
```

### cURL

```bash
curl --location \
"http://localhost:8080/api/candles?symbol=RELIANCE&timeframe=5m&start=2026-01-01T09:15:00&end=2026-01-01T10:00:00"
```

### Sample Response

```json
[
  {
    "timestamp": "2026-01-01T09:15:00Z",
    "open": 1572.5,
    "high": 1580.2,
    "low": 1571.8,
    "close": 1578.4,
    "volume": 55000
  },
  {
    "timestamp": "2026-01-01T09:20:00Z",
    "open": 1578.4,
    "high": 1582.0,
    "low": 1577.0,
    "close": 1581.1,
    "volume": 61000
  }
]
```

---

# Aggregation Logic

For every aggregation window:

### Open

First open value in the window.

### High

Maximum high value in the window.

### Low

Minimum low value in the window.

### Close

Last close value in the window.

### Volume

Sum of all volume values in the window.

Example:

```
5 x 1-minute candles
       ↓
1 aggregated 5-minute candle
```

---

# Running the Vue.js Client

Navigate to frontend project:

```bash
cd Stock-Market-Data-Aggregation-Service-UI
```

Install dependencies:

```bash
npm install
```

Run:

```bash
npm run serve
```

Frontend starts on:

```text
http://localhost:8081
```

---

# Frontend Features

* Select stock symbol
* Select timeframe
* Choose start date
* Choose end date
* Validate date range
* Fetch aggregated candles
* Display candle data in table format
* Error handling for invalid inputs

---

# Assumptions

1. Input candle data is already available in CSV format.
2. All timestamps are stored in UTC.
3. Cassandra contains only valid 1-minute candles.
4. Aggregation is performed in-memory after fetching raw candles.
5. Timeframes supported:

   * 1m
   * 5m
   * 15m
   * 30m
   * 60m

---

# Design Decisions

### Cassandra

Chosen because:

* High write throughput
* Time-series data support
* Horizontal scalability
* Efficient partitioning by stock symbol

### Partition Key

```sql
PRIMARY KEY(symbol, datetime)
```

Benefits:

* Fast retrieval by symbol
* Ordered candles by timestamp

### Service Layer Aggregation

Aggregation is performed in the service layer because:

* Easier to support multiple custom timeframes
* Keeps database schema simple
* Allows reusable aggregation logic

### REST API

A simple GET endpoint was used because candle retrieval is a read-only operation and naturally maps to query parameters.

---

# Future Enhancements

* Candlestick chart visualization
* Pagination
* Redis caching
* Kafka-based real-time ingestion
* Docker deployment
* Authentication and authorization

---

# Author

Komal Ramesh Chougule
B.Sc Computer Science
MIT ACSC Alandi
