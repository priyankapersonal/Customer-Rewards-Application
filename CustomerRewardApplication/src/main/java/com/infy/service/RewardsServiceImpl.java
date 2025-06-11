package com.infy.service;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.infy.exception.CustomerNotFoundException;
import com.infy.exception.InvalidDateFormatException;
import com.infy.exception.InvalidRequestException;
import com.infy.model.Customer;
import com.infy.model.Transaction;
import com.infy.repository.CustomerRepository;
import com.infy.repository.TransactionRepository;

/**
 * Service implementation for managing customer rewards.
 */
@Service
public class RewardsServiceImpl implements RewardsService {

    private static final Logger logger = LoggerFactory.getLogger(RewardsServiceImpl.class);

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    /**
     * Saves a customer and their transactions to the database.
     *
     * @param customer the customer entity to save
     * @return the saved customer entity
     * @throws InvalidRequestException if customer or transaction data is invalid
     */
    @Override
    public Customer saveCustomer(Customer customer) {
        logger.info("Saving Customer: {}", customer.getCustomerName());
        validateCustomer(customer);

        Customer savedCustomer = customerRepository.save(customer);

        customer.getTransaction().forEach(transaction -> {
            transaction.setCustomer(savedCustomer);
            transactionRepository.save(transaction);
        });

        logger.info("Customer saved successfully with ID: {}", savedCustomer.getCustomerId());
        return savedCustomer;
    }

    /**
     * Validates the customer object and its transactions.
     *
     * @param customer the customer to validate
     * @throws InvalidRequestException if validation fails
     */
    private void validateCustomer(Customer customer) {
        if (customer == null) {
            throw new InvalidRequestException("Customer cannot be null.");
        }
        if (customer.getCustomerName() == null || customer.getCustomerName().isBlank()) {
            throw new InvalidRequestException("Customer name cannot be null or blank.");
        }
        if (customer.getTransaction() == null || customer.getTransaction().isEmpty()) {
            throw new InvalidRequestException("Transaction list cannot be null or empty.");
        }
        for (Transaction transaction : customer.getTransaction()) {
            validateTransaction(transaction);
        }
    }

    /**
     * Validates a transaction object.
     *
     * @param transaction the transaction to validate
     * @throws InvalidDateFormatException if the transaction date is null
     * @throws InvalidRequestException if the transaction amount is not positive
     */
    private void validateTransaction(Transaction transaction) {
        if (transaction.getDate() == null) {
            logger.warn("Transaction date is null");
            throw new InvalidDateFormatException("Transaction date cannot be null.");
        }
        if (transaction.getAmount() <= 0) {
            logger.warn("Invalid transaction amount: {}", transaction.getAmount());
            throw new InvalidRequestException("Transaction amount must be greater than zero.");
        }
    }

    /**
     * Calculates the rewards for a customer within a specified date range.
     *
     * @param customerId the ID of the customer
     * @param startDate  the start date of the period
     * @param endDate    the end date of the period
     * @return a map containing customer details, rewards breakdown by month, and total rewards
     * @throws InvalidRequestException if input is invalid
     * @throws CustomerNotFoundException if no transactions or the customer is found
     */
    @Override
    public Map<String, Object> calculateRewards(Long customerId, LocalDate startDate, LocalDate endDate) {
        logger.info("Calculating rewards for customer ID: {}", customerId);
        validateRewardRequest(customerId, startDate, endDate);

        List<Transaction> transactions = transactionRepository.findByCustomerCustomerIdAndDateBetween(customerId,
                startDate, endDate);

        if (transactions.isEmpty()) {
            throw new CustomerNotFoundException("No transactions found for customer ID: " + customerId);
        }

        List<Map<String, Object>> rewardByMonth = transactions.stream()
                .collect(Collectors.groupingBy(t -> t.getDate().getMonth().toString(), LinkedHashMap::new,
                        Collectors.summingInt(t -> calculatePoints(t.getAmount()))))
                .entrySet().stream().map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("month", entry.getKey());
                    map.put("points", entry.getValue());
                    return map;
                }).collect(Collectors.toList());

        int totalPoints = rewardByMonth.stream().mapToInt(m -> (int) m.get("points")).sum();

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found for ID: " + customerId));

        Map<String, Object> response = new HashMap<>();
        response.put("Customer Details", customer);
        response.put("Rewards Breakdown", rewardByMonth);
        response.put("Total Rewards", totalPoints);

        logger.info("Reward calculation completed for customer ID: {}", customerId);
        return response;
    }

    /**
     * Validates the input for reward calculation.
     *
     * @param customerId the customer ID
     * @param startDate  the start date
     * @param endDate    the end date
     * @throws InvalidRequestException if any input is invalid
     */
    private void validateRewardRequest(Long customerId, LocalDate startDate, LocalDate endDate) {
        if (customerId == null || customerId <= 0) {
            throw new InvalidRequestException("Customer ID must be a positive number.");
        }
        if (startDate == null || endDate == null) {
            throw new InvalidRequestException("Start date and end date cannot be null.");
        }
        if (startDate.isAfter(endDate)) {
            throw new InvalidRequestException("Start date cannot be after end date.");
        }
    }

	/**
	 * Calculates reward points for a single transaction based on the amount. - 2
	 * points for every dollar spent over $100 - 1 point for every dollar spent over
	 * $50 up to $100 Uses integer arithmetic to avoid the half-point bug.
	 */
	private int calculatePoints(double amount) {
		int points = 0;
		if (amount > 100)
			points += (int) ((amount - 100) * 2);
		if (amount > 50)
			points += (int) Math.min(amount, 100) - 50;
		return points;
	}
}
