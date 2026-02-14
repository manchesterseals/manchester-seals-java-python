# ManchesterSeals CSV Data Reader

A Spring Boot application for reading and processing GPS coordinate data from CSV files, with velocity calculation and ECEF coordinate conversion.

## Project Setup
- Java 11+
- Spring Boot 3.2.0+
- Maven for build and dependency management

## Personal Setup
- Java 17
- Spring Boot 3.2.0
- Maven 3.8.1
- IntelliJ IDEA Ultimate Edition for development and testing

## Project Structure

```
src/main/java/com/manchesterseals/
├── controller/
│   └── ManchesterSealsController.java      # REST API endpoints
├── service/
│   └── ManchesterSealsService.java         # CSV parsing and data processing logic
└── model/
    ├── CsvRecord.java                      # Data model for CSV records
    ├── CountryData.java                    # Country data model
    └── VelocityResponse.java               # Velocity calculation response model
```

## Data Model

Each CSV record contains:
- **unixTimestamp** (Long): Time since Unix epoch in seconds
- **latitude** (Double): WGS84 latitude in degrees
- **longitude** (Double): WGS84 longitude in degrees
- **altitude** (Double): WGS84 altitude in kilometers
- **ecefX, ecefY, ecefZ** (Double): Earth-Centered, Earth-Fixed coordinates
- **ecefVelocityX, ecefVelocityY, ecefVelocityZ** (Double): Velocity components in ECEF frame

## API Endpoints

### 1. Get All CSV Records
- **Endpoint**: `GET /api/csv-data`
- **Description**: Reads all records from the configured CSV file and returns them as JSON with ECEF coordinates
- **Response**: Array of CsvRecord objects

**Example Response:**
```json
[
  {
    "unixTimestamp": 1532332859,
    "latitude": 53.349800000004094613359484,
    "longitude": -6.260299999999997311306288,
    "altitude": 6.096000000000000085265128,
    "ecefX": 3798137.256,
    "ecefY": -411149.531,
    "ecefZ": 5064921.432
  }
]
```

### 2. Get Dataset Time Range
- **Endpoint**: `GET /api/time-range`
- **Description**: Retrieves the minimum and maximum timestamps available in the dataset
- **Response**: Object with minTime and maxTime

### 3. Calculate Velocity
- **Endpoint**: `GET /api/velocity?time1={timestamp1}&time2={timestamp2}`
- **Description**: Calculates velocity between two time points using interpolation
- **Parameters**: Unix timestamps in seconds
- **Response**: Velocity value in m/s with time range info

### 4. Filter and Sort Countries
- **Endpoint**: `POST /api/filter-countries`
- **Description**: Filters out entries with ID -1 and sorts by country name
- **Request Body**: JSON array of country data
- **Response**: Filtered and sorted country data

## Configuration

The CSV file path can be configured via application properties:

```properties
csv.file.path=config/csvFiles/SciTec_code_problem_data.csv
```

Default location: `config/csvFiles/SciTec_code_problem_data.csv`

## CSV File Format

The CSV file should have 4 columns (no header row):
1. Unix timestamp (decimal format)
2. Latitude (decimal degrees)
3. Longitude (decimal degrees)
4. Altitude (kilometers)

**Example:**
```
1532332859.04,53.349800000004094613359484,-6.260299999999997311306288,6.096000000000000085265128
1532332860.15,53.350200000001234567890123,-6.261200000000234567890123,6.097500000000123456789012
```

## Building and Running

### Build
```bash
mvn clean install
or
mvn -N io.takari:maven:wrapper
```

### Run
```bash
mvn spring-boot:run
```

### Run Tests
```bash
mvn test
mvn test -Dtest=ManchesterSealsServiceTest
mvn test -Dtest=ManchesterSealsControllerTest
mvn test -Dtest=InterpolationUtilTest
```

The application will start on `http://localhost:8080`

## API Documentation

Access Swagger UI for interactive API exploration:
`http://localhost:8080/swagger-ui.html`

Access endpoints via Postman or curl:
```bash
curl -X GET "http://localhost:8080/api/csv-data" -H "accept: application/json"
curl -X GET "http://localhost:8080/api/time-range" -H "accept: application/json"
curl -X GET "http://localhost:8080/api/velocity?time1=1532332859&time2=1532332865" -H "accept: application/json"
```

## Technical Details

### LLA to ECEF Conversion
Reference implementation:
`https://gist.github.com/alekfrohlich/58a88b770397ed866981cf538a3cf494`

### Velocity Calculations
Reference documentation:
`https://stackoverflow.com/questions/11677565/converting-velocity-and-position-vectors-to-longitude-and-latitude`

### Original Solution
Note: An original Python solution to this problem exists at:
`https://github.com/usd-1/SciTec-Coding-Challenge/tree/main`

This Java implementation was created to demonstrate Spring Boot capabilities while solving the same problem.

## Logging

The application uses SLF4J with Logback for logging:
- **INFO**: Records successfully read from CSV file and sample records
- **DEBUG**: Detailed parsing information and column indices
- **WARN**: Parsing errors for invalid data values

Configure logging levels in `application.properties`:
```properties
logging.level.com.manchesterseals.service=INFO
logging.level.com.manchesterseals.service.ManchesterSealsService=DEBUG
```

## Error Handling

- Invalid numeric values are logged as warnings and skipped
- Empty lines in the CSV file are ignored
- Missing columns result in null values for that field
- Out-of-range velocity calculations throw IllegalArgumentException with descriptive message

## Project Status

✅ Fully branded as ManchesterSeals
✅ All 51 tests passing
✅ Complete API documentation with Swagger
✅ Production-ready Spring Boot application

