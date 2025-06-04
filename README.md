# 🎁 Customer Rewards Application

## 📝 Overview
A Spring Boot application that calculates customer reward points based on transaction history.

---

## ✅ Features

  - Add new customers along with transaction history
  - Calculate reward points based on configurable business logic
  - Validate inputs using `jakarta.validation` annotations
  - Clean architecture with DTO, Entity, and Repository separation
  - Log4j2-based logging for easier debugging
  - Unit tests using JUnit 5 and Mockito
  - RESTful API design with proper HTTP status handling

## 🏗️ Layers Breakdown

### 1. Controller Layer
  - Handles HTTP requests/responses  
  - Accepts validated `CustomerDto` and `TransactionDto` via `@RequestBody @Valid`  
  - Routes requests to `RewardsService`

### 2. Service Layer
  - Business logic for saving customers and calculating rewards  
  - Maps DTOs to JPA entities  
  - Delegates data access to the repository layer

### 3. Repository Layer
  - Uses Spring Data JPA to persist entities  
  - Interface-based approach with `JpaRepository`

### 4. Model/Entity Layer
  - JPA entities: `Customer` and `Transaction`  
  - Mapped to relational database tables using Hibernate  
  - Relationship:  
    `Customer 1 → ∞ Transaction`

### 5. DTO Layer
  - `CustomerDto` and `TransactionDto` encapsulate input data  
  - Help decouple internal entity structure from API contracts  
  - Validated using `jakarta.validation` annotations

### 6. Validation Layer
  - Enforces constraints using annotations like:  
    `@NotNull`, `@NotBlank`, `@Positive`, `@Size`  
  - Validation errors are automatically handled via `@Valid`

---

## 🧰 Tech Stack

| Layer        | Technology                  |
|--------------|-----------------------------|
| Language     | Java 17                     |
| Framework    | Spring Boot 3.5.0           |
| Persistence  | Spring Data JPA, Hibernate  |
| Validation   | Jakarta Bean Validation     |
| Database     | MySQL                       |
| Logging      | Log4j2                      |
| Testing      | JUnit 5, Mockito, Spring Test|
| Build Tool   | Maven                       |

---

## 📂 Project Structure

| Component/Folder                  | Description                                                               |
| --------------------------------- | ------------------------------------------------------------------------- |
| `controller/`                     | REST API controllers (e.g., `RewardsController.java`)                     |
| `dto/`                            | Data Transfer Objects like `CustomerDto`, `TransactionDto`                |
| `model/`                          | JPA Entity classes like `Customer.java`, `Transaction.java`               |
| `repository/`                     | Interfaces extending `JpaRepository` for database access                  |
| `service/`                        | Business logic (e.g., `RewardsService`)             |
| `exception/` *(optional)*         | Custom exception handlers (e.g., `GlobalExceptionHandler.java`)           |
| `test/`                           | Unit and integration test classes (`CustomerRewardApplicationTests.java`) |
| `application.properties`          | Application-level configuration (DB, JPA, logging, etc.)                  |
| `log4j2.xml`                      | Logging configuration using Log4j2                                        |
| `pom.xml`                         | Maven build configuration and project dependencies                        |
| `CustomerRewardsApplication.java` | Main Spring Boot application entry point                                  |




✅ Tests include:
1. Positive & negative scenarios for the controller
2. Manual validation tests for DTOs

🔗 API Endpoints
1. ➕ Add Customer
POST /api/rewards/addCustomer
Request Body (JSON):

          {
        "customerName": "Merry",
        "transaction": [
    
        {
          "amount": 120.0,
          "date": "2024-05-01"
        }
          ]
        }

  Validation:
        1. customerName must not be blank or null
        2. The transaction list must have at least one valid transaction

2. 🧮 Calculate Rewards
GET /api/rewards/calculateRewards/{customerId}?startDate=2024-01-01&endDate=2024-03-31
Returns (JSON):

        {
          "Transactions": [
          
            {
              "amount": 120.0,
              "date": "2025-01-30",
              "transactionId": 1
            },
            {
              "amount": 70.0,
              "date": "2025-01-30",
              "transactionId": 2
            }
          ],
          "Customer Details": {
            "customerId": 1,
            "customerName": "Merry",
            "transaction": [
            
              {
                "amount": 120.0,
                "date": "2025-01-30",
                "transactionId": 1
              },
              {
                "amount": 70.0,
                "date": "2025-01-30",
                "transactionId": 2
              }
            ]
          },
          "Rewards Breakdown": {
            "JANUARY": 110,
            "Total Rewards": 110
          }
        }

🎯 Reward Calculation Logic:
     1. $50–$100 → 1 point per dollar over $50      
     2. $100+ → 2 points per dollar over $100 + 1 point per dollar between $50–$100


🚀 How to Run
   1. Clone the Repo
         git clone https://github.com/priyankapersonal/Customer-Rewards-Application.git
   
   2. Configure MySQL in application.properties  
        spring.datasource.url=jdbc:mysql://localhost:3306/rewards_db
        spring.datasource.username=root
        spring.datasource.password=yourpassword
        spring.jpa.hibernate.ddl-auto=update
      
  3. Run the App
        mvn spring-boot: run
     

👩‍💼 Author
Priyanka Patil
(Senior Associate Consultant)

