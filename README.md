🎁 Customer Rewards Application

    📝 Overview
    A Spring Boot application that calculates customer reward points based on transaction history.



✅ Features

    - Add new customers along with transaction history
    - Calculate reward points based on configurable business logic
    - Validate inputs using `jakarta.validation` annotations
    - Clean architecture with DTO, Entity, and Repository separation
    - Log4j2-based logging for easier debugging
    - Unit tests using JUnit 5 and Mockito
    - RESTful API design with proper HTTP status handling

🏗️ Layers Breakdown

     1. Controller Layer
      - Handles HTTP requests/responses  
      - Accepts validated `CustomerDto` and `TransactionDto` via `@RequestBody @Valid`  
      - Routes requests to `RewardsService`
    
     2. Service Layer
      - Business logic for saving customers and calculating rewards  
      - Maps DTOs to JPA entities  
      - Delegates data access to the repository layer
    
     3. Repository Layer
      - Uses Spring Data JPA to persist entities  
      - Interface-based approach with `JpaRepository`
    
     4. Model/Entity Layer
      - JPA entities: `Customer` and `Transaction`  
      - Mapped to relational database tables using Hibernate  
      - Relationship:  
        `Customer 1 → ∞ Transaction`
    
     5. DTO Layer
      - `CustomerDto` and `TransactionDto` encapsulate input data  
      - Help decouple internal entity structure from API contracts  
      - Validated using `jakarta.validation` annotations
    
     6. Validation Layer
      - Enforces constraints using annotations like:  
        `@NotNull`, `@NotBlank`, `@Positive`, `@Size`  
      - Validation errors are automatically handled via `@Valid`



 🧰 Tech Stack

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


 📂 Project Structure

        Customer-Rewards-Application
        ├── src
        │   └── main
        │       ├── java
        │       │   └── com.infy
        │       │       ├── controller          # REST controllers (RewardsController)
        │       │       ├── dto                 # DTOs for Customer and Transaction
        │       │       ├── model               # JPA entities (Customer, Transaction)
        │       │       ├── repository          # Spring Data JPA repositories
        │       │       ├── service             # Business logic layer
        │       │       └── CustomerRewardsApplication.java  # Main Spring Boot entry point
        │       └── resources
        │           ├── application.properties  # DB config and app settings
        │           └── log4j2.xml              # Log4j2 configuration (if present)
        │
        ├── src
        │   └── test
        │       └── java
        │           └── com.infy
        │               └── CustomerRewardApplicationTests.java  # JUnit + Mockito test cases
        │
        ├── pom.xml                             # Maven build file
        └── README.md                           # Project documentation






✅ Tests include:

    1. Positive & negative scenarios for the controller
    2. Manual validation tests for DTOs
   

🔗 API Endpoints:
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
        GET /api/rewards/calculateRewards/{customerId}?startDate=2024-05-01&endDate=2024-08-31   

   Returns (JSON):

        {
          "Transactions": [
          
            {
              "amount": 120.0,
              "date": "2024-05-01",
              "transactionId": 1
            }            
          ],
          "Customer Details": {
            "customerId": 1,
            "customerName": "Merry",
            "transaction": [
            
              {
                "amount": 120.0,
                "date": "2024-05-01",
                "transactionId": 1
              }
            ]
          },
          "Rewards Breakdown": {
            "JANUARY": 90,
            "Total Rewards": 90
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
     

👩‍💼 Author:
   Priyanka Patil
  (Senior Associate Consultant)

