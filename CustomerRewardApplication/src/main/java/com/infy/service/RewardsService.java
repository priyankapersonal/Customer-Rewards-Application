package com.infy.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infy.controller.RewardsController;
import com.infy.exception.CustomerNotFoundException;
import com.infy.model.Customer;
import com.infy.model.Transaction;
import com.infy.repository.CustomerRepository;
import com.infy.repository.TransactionRepository;

import jakarta.validation.Valid;

@Service
public class RewardsService {
	
	private static final Logger logger =LogManager.getLogger(RewardsController.class);

	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private TransactionRepository transactionRepository;
	

	public Customer saveCustomer(@Valid Customer customer) {
		logger.info("Saving Customer : {}",customer.getCustomerName());
		Customer saveCustomer=customerRepository.save(customer);
		if(customer.getTransaction()!=null) {
			for(Transaction transaction:customer.getTransaction()) {
				transaction.setCustomer(saveCustomer);
				transactionRepository.save(transaction);
			}
		}
		return saveCustomer;
	}
	
	
	public Map<String, Object> calculateRewards(Long customerId, LocalDate startDate, LocalDate endDate) {
		logger.info("calculating rewards for customer ID: {}", customerId);
		List<Transaction> transactions=transactionRepository.findByCustomerCustomerIdAndDateBetween(customerId, startDate, endDate);
		if(transactions.isEmpty()) {
			logger.warn("No transactions found for customer ID: {}", customerId);
			throw new CustomerNotFoundException("No transactions found for customer ID: " +customerId);
		}
		
		Map<String,Integer> rewards=new HashMap<>();
		int totalPoints=0;
		
		for(Transaction transaction:transactions) {
			int points=calculatePoints(transaction.getAmount());
			totalPoints += points;
			String month = transaction.getDate().getMonth().toString();
			rewards.put(month, rewards.getOrDefault(month, 0)+points);
		}
		rewards.put("Total Rewards", totalPoints);
		
		Customer customer=customerRepository.findById(customerId)
				.orElseThrow(()->new CustomerNotFoundException("Customer not found for ID:" + customerId));
		
		Map<String,Object> response=new HashMap<>();
		response.put("Customer Deatils", customer);
		response.put("Rewards Breakdown", rewards);
		response.put("Transactions", transactions);
		
		logger.info("Rewards calculation completed for customer ID: {}", customerId);
		return response;
	}
	
	private int calculatePoints(double amount) {
		int points=0;
		if(amount > 100) {
			points+= (amount-100) * 2;
			amount=100;
		}
		if(amount> 50) {
			points += (amount - 50);
		}
		return points;
	}

}
