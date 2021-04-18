# Lendico Annuity Planner

This is a challenge implementation for Lendico's annuity planner

## Swagger API

Visit http://localhost:8080/swagger-ui/ for API details
and to try the endpoints out by sample data

## Installation

Use the mvnw included in the root folder or your local mvn tool  [mvn](https://maven.apache.org/) to clean and install the application.

```bash
./mvnw clean install
```

## Running

```bash
./mvnw spring-boot:run
```

## Tests
Tests related to the annuity planner will be automatically run while "mvn clean install" command is in action.
Sample loan queries within 2000-10000 Euros, 3 to 240 months
 ranges and 3% - 40% nominal interest rates