package com.infy;

import com.infy.controller.RewardsController;
import com.infy.dto.CustomerDto;
import com.infy.dto.TransactionDto;
import com.infy.model.Customer;
import com.infy.model.Transaction;
import com.infy.service.RewardsService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CustomerRewardApplicationTests {

    @Mock
    private RewardsService rewardsService;

    @InjectMocks
    private RewardsController rewardsController;

    private CustomerDto customerDto;
    private Customer customerEntity;

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        // Valid DTO
        customerDto = new CustomerDto();
        customerDto.setCustomerName("Merry");

        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setAmount(120.0);
        transactionDto.setDate(LocalDate.now());

        List<TransactionDto> transactionDtos = new ArrayList<>();
        transactionDtos.add(transactionDto);
        customerDto.setTransaction(transactionDtos);

        // Entity returned by service
        customerEntity = new Customer();
        customerEntity.setCustomerId(1L);
        customerEntity.setCustomerName("Merry");

        Transaction transaction = new Transaction();
        transaction.setTransactionId(1L);
        transaction.setAmount(120.0);
        transaction.setDate(LocalDate.now());
        transaction.setCustomer(customerEntity);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);
        customerEntity.setTransaction(transactions);
    }

    @Test
    void testAddCustomer() {
        when(rewardsService.saveCustomer(any(Customer.class))).thenReturn(customerEntity);

        ResponseEntity<?> response = rewardsController.addCustomer(customerDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(customerEntity, response.getBody());
        verify(rewardsService, times(1)).saveCustomer(any(Customer.class));
    }

    @Test
    void testAddCustomerWithNull() {
        ResponseEntity<?> response = rewardsController.addCustomer(null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Customer data is missing", response.getBody());
    }

    @Test
    void testAddCustomerWithInvalidDto() {
        CustomerDto invalidDto = new CustomerDto(); // missing name and transactions

        Set<ConstraintViolation<CustomerDto>> violations = validator.validate(invalidDto);

        assertFalse(violations.isEmpty());
        List<String> messages = new ArrayList<>();
        for (ConstraintViolation<CustomerDto> violation : violations) {
            messages.add(violation.getMessage());
        }

        assertTrue(messages.contains("Customer name cannot be null") ||
                   messages.contains("Customer name cannot be blank"));
        assertTrue(messages.contains("Transaction list cannot be null") ||
                   messages.contains("At least one transaction is required"));
    }

    @Test
    void testCalculateRewards() {
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 3, 31);

        Map<String, Object> rewardsMap = new HashMap<>();
        rewardsMap.put("Total Rewards", 120);

        when(rewardsService.calculateRewards(1L, startDate, endDate)).thenReturn(rewardsMap);

        ResponseEntity<Map<String, Object>> response = rewardsController.getRewards(1L, startDate, endDate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(rewardsMap, response.getBody());
        verify(rewardsService, times(1)).calculateRewards(1L, startDate, endDate);
    }
}
