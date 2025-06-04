package com.infy.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.infy.dto.CustomerDto;
import com.infy.dto.TransactionDto;
import com.infy.model.Customer;
import com.infy.model.Transaction;
import com.infy.service.RewardsService;

import jakarta.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rewards")
public class RewardsController {

	private static final Logger logger = LogManager.getLogger(RewardsController.class);

	@Autowired
	private RewardsService rewardsService;

    @PostMapping("/addCustomer")
    public ResponseEntity<?> addCustomer(@Valid @RequestBody(required = false) CustomerDto customerDto) {
        if (customerDto == null) {
            logger.warn("Received null customer DTO");
            return ResponseEntity.badRequest().body("Customer data is missing");
        }

        // Convert DTO to Entity
        Customer customer = new Customer();
        customer.setCustomerName(customerDto.getCustomerName());

        List<Transaction> transactions = new ArrayList<>();
        if (customerDto.getTransaction() != null) {
            for (TransactionDto transactionDto : customerDto.getTransaction()) {
                Transaction transaction = new Transaction();
                transaction.setAmount(transactionDto.getAmount());
                transaction.setDate(transactionDto.getDate());
                transaction.setCustomer(customer);
                transactions.add(transaction);
            }
        }

        customer.setTransaction(transactions);

        logger.debug("Adding Customer : {}", customer);
        Customer savedCustomer = rewardsService.saveCustomer(customer);
        logger.info("Customer added successfully : {}", savedCustomer);
        return new ResponseEntity<>(savedCustomer, HttpStatus.CREATED);
    }
    
    
	@GetMapping("/calculateRewards/{customerId}")
	public ResponseEntity<Map<String, Object>> getRewards(
			@PathVariable Long customerId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

		logger.debug("Calculating rewards for customer : {}", customerId);
		Map<String, Object> rewards = rewardsService.calculateRewards(customerId, startDate, endDate);
		logger.info("Rewards calculated successfully for customer: {}", customerId);
		return new ResponseEntity<>(rewards, HttpStatus.OK);
	}
}
