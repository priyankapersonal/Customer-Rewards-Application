package com.infy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.infy.model.Customer;
import com.infy.model.Transaction;
import com.infy.repository.CustomerRepository;
import com.infy.repository.TransactionRepository;
import com.infy.service.RewardsService;

/**
 * Integration tests for {@link RewardsService} using Spring context and actual
 * database.
 * <p>
 * These tests verify the business logic for saving customers and calculating
 * rewards, including validation and error scenarios.
 * </p>
 */
@SpringBootTest
@TestPropertySource("classpath:application.properties")
public class RewardsServiceIntegrationTest {

	@Autowired
	private RewardsService rewardsService;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	private Customer customer;
	private Transaction transaction;

	/**
	 * Sets up test data before each test. Clears the repositories and creates a
	 * sample customer with a transaction.
	 */
	@BeforeEach
	void setup() {
		transactionRepository.deleteAll();
		customerRepository.deleteAll();

		customer = new Customer();
		customer.setCustomerName("John");

		transaction = new Transaction();
		transaction.setAmount(120.0);
		transaction.setDate(LocalDate.of(2024, 1, 15));
		transaction.setCustomer(customer);

		customer.setTransaction(List.of(transaction));
	}

	/**
	 * Tests saving a customer successfully.
	 */
	@Test
	void testSaveCustomerSuccess() {
		Customer saved = rewardsService.saveCustomer(customer);
		assertNotNull(saved);
		assertNotNull(saved.getCustomerId());
		assertEquals("John", saved.getCustomerName());
	}

	/**
	 * Tests reward calculation for a valid customer and transaction.
	 */
	@Test
	void testCalculateRewardsSuccess() {
		Customer saved = rewardsService.saveCustomer(customer);

		Map<String, Object> result = rewardsService.calculateRewards(saved.getCustomerId(), LocalDate.of(2024, 1, 1),
				LocalDate.of(2024, 12, 31));

		assertNotNull(result);
		assertTrue(result.containsKey("Total Rewards"));
		assertEquals(saved.getCustomerName(), ((Customer) result.get("Customer Details")).getCustomerName());
		assertEquals(90, result.get("Total Rewards")); // 120.0 should give 90 points
	}

	/**
	 * Tests that an exception is thrown when start date is after end date.
	 */
	@Test
	void testCalculateRewardsInvalidDateOrder() {
		Customer saved = rewardsService.saveCustomer(customer);

		Exception ex = assertThrows(RuntimeException.class, () -> rewardsService.calculateRewards(saved.getCustomerId(),
				LocalDate.of(2024, 12, 31), LocalDate.of(2024, 1, 1)));
		assertTrue(ex.getMessage().contains("Start date cannot be after end date"));
	}

	/**
	 * Tests that an exception is thrown for an invalid customer ID.
	 */
	@Test
	void testCalculateRewardsWithInvalidCustomerId() {
		Exception ex = assertThrows(RuntimeException.class,
				() -> rewardsService.calculateRewards(0L, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31)));
		assertTrue(ex.getMessage().contains("Customer ID must be a positive number"));
	}

	/**
	 * Tests that an exception is thrown when start or end date is null.
	 */
	@Test
	void testCalculateRewardsWithNullDates() {
		Customer saved = rewardsService.saveCustomer(customer);

		Exception ex = assertThrows(RuntimeException.class,
				() -> rewardsService.calculateRewards(saved.getCustomerId(), null, null));
		assertTrue(ex.getMessage().contains("cannot be null"));
	}
}