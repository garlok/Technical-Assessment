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
    Fetches the latest best aggregated price for the given symbol. If no symbol is provided, it returns the prices for all available symbols.
	
		
- **Execute Trade**

    ```http
    POST /api/trading/perform/{actionType}
    ```
    Executes a trade based on the latest best aggregated price and the given actionType(BUY/SELL). 
	The request body should include the user name, symbol, buyQuantity, and sellQuantity.

    Example Request Body:

    ```json
    {
    "userName": "Tester Tester",
    "symbol": "ETHUSDT",
    "buyQuantity": 2,
    "sellQuantity": 0
	}
    ```
	
- **Get Wallet Balance**

    ```http
    GET /api/wallets/balance/{userName}
    ```

    Retrieves the wallet balance for the specified userName.

    Example:

    ```http
    GET /api/wallets/balance/Tester tester
	```

### Preloading Data

You can preload data into the H2 database by executing the following SQL statements in the H2 console or adding them to the `data.sql` file in the `src/main/resources` directory: