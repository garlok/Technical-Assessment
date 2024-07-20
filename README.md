# Technical Assessment

This project is a Spring Boot application designed for trading based on the latest best aggregated price. It includes RESTful endpoints to fetch prices and execute trades. The application uses an H2 in-memory database for data storage.

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven

### Running the Application

1. **Clone the repository**

    ```bash
    git clone https://github.com/garlok/Technical-Assessment.git
    cd Technical-Assessment
    ```

2. **Build the project**

    ```bash
    mvn clean install
    ```

3. **Run the application**

    ```bash
    mvn spring-boot:run
    ```

### Testing the API

Once the application is running, you can test the API using Swagger UI.

1. Open your web browser.
2. Go to [http://localhost:8080/swagger-ui/index.html].
3. Use the provided interface to explore and test the available API endpoints.

### Accessing the H2 Database Console

The application uses an H2 in-memory database. You can access the H2 console to view and manipulate data.

1. Open your web browser.
2. Go to [http://localhost:8080/h2-console].
3. Use the following settings to connect:
    - **JDBC URL**: `jdbc:h2:mem:testdb`
    - **Username**: `sa`
    - **Password**: `password`

### API Endpoints

- **Get Latest Best Price**

    ```http
    GET /api/prices/listing?symbol={symbol}
    ```
	```cURL
	curl --location 'http://localhost:8080/api/prices/listing'
	curl --location 'http://localhost:8080/api/prices/listing?cryptoTrading=BTCUSDT'
    ```
    Fetches the latest best aggregated price for the given symbol. If no symbol is provided, it returns the prices for all available symbols.
	
		
- **Execute Trade**

    ```http
    POST /api/trades/execute
    ```

    Executes a trade based on the latest best aggregated price. The request body should include the user name, symbol, amount, and trade type (BUY/SELL).

    Example Request Body:

    ```json
    {
        "userName": "tester",
        "symbol": "ETHBTC",
        "amount": 0.5,
        "tradeType": "BUY"
    }
    ```
	
- **Get Wallet Balance**

    ```http
    GET /api/wallets/balance/{userName}
    ```
    ```cURL
    curl --location 'http://localhost:8080/api/wallets/balance/tester'
    ```

    Retrieves the wallet balance for the specified userName.

    Example:

    ```http
    GET /api/wallets/balance/tester
	```

### Preloading Data

You can preload data into the H2 database by executing the following SQL statements in the H2 console or adding them to the `data.sql` file in the `src/main/resources` directory:

```sql
INSERT INTO users (id, user_name) VALUES ('1d4e2e4b-8c1e-4c9b-b5ad-1a229d66d1d8', 'tester');
INSERT INTO wallet (id, user_id, balance, updated_at) VALUES ('2f4e6b7a-6e2f-4c89-ae2c-3d6f1a26e2d2', '1d4e2e4b-8c1e-4c9b-b5ad-1a229d66d1d8', 1000.00, CURRENT_TIMESTAMP);
```
