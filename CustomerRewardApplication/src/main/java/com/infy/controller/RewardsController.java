package com.infy.controller;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.infy.dto.CustomerDto;
import com.infy.exception.InvalidRequestException;
import com.infy.model.Customer;
import com.infy.model.Transaction;
import com.infy.service.RewardsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * REST controller for managing customers and their rewards.
 * <p>
 * Provides endpoints to create new customers with their transaction history and
 * to retrieve reward points earned by a customer within a specified date range.
 * </p>
 */
@Tag(name = "Customer Rewards API", description = "Operations related to customer rewards and transactions")
@RestController
@RequestMapping("/api/customers")
public class RewardsController {

	private static final Logger logger = LoggerFactory.getLogger(RewardsController.class);

	@Autowired
	private RewardsService rewardsService;

	/**
	 * Creates a new customer along with their transaction history.
	 *
	 * @param customerDto the data transfer object containing customer name and
	 *                    transaction list
	 * @return ResponseEntity containing the saved customer and HTTP status 201
	 *         (Created)
	 * @throws InvalidRequestException if the input customer data is missing or
	 *                                 invalid
	 */
	@Operation(summary = "Add a new customer with transactions", description = "Creates a new customer and saves their transaction history.", responses = {
			@ApiResponse(responseCode = "201", description = "Customer created successfully", content = @Content(schema = @Schema(implementation = Customer.class))),
			@ApiResponse(responseCode = "400", description = "Invalid input", content = @Content) })
	@PostMapping(consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> addCustomer(
			@Valid @RequestBody @Parameter(description = "Customer data with transactions", required = true) CustomerDto customerDto) {
		Customer customer = new Customer();
		customer.setCustomerName(customerDto.getCustomerName());

		List<Transaction> customerTransactions = customerDto.getTransaction().stream().map(dto -> {
			Transaction t = new Transaction();
			t.setAmount(dto.getAmount());
			t.setDate(dto.getDate());
			t.setCustomer(customer);
			return t;
		}).collect(Collectors.toList());

		customer.setTransaction(customerTransactions);

		logger.debug("Adding Customer : {}", customer);
		Customer savedCustomer = rewardsService.saveCustomer(customer);
		logger.info("Customer added successfully : {}", savedCustomer);
		return new ResponseEntity<>(savedCustomer, HttpStatus.CREATED);
	}

	/**
	 * Retrieves the reward points earned by a customer within a specified date
	 * range.
	 *
	 * @param customerId the ID of the customer
	 * @param startDate  the start date of the period (ISO format)
	 * @param endDate    the end date of the period (ISO format)
	 * @return ResponseEntity containing the reward breakdown and customer details
	 */
	@Operation(summary = "Get rewards for a customer", description = "Calculates reward points earned by a customer within a specified date range.", responses = {
			@ApiResponse(responseCode = "200", description = "Rewards calculated successfully", content = @Content(schema = @Schema(implementation = Map.class))),
			@ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
			@ApiResponse(responseCode = "404", description = "Customer not found", content = @Content) })
	@GetMapping(value = "/{customerId}/rewards", produces = "application/json")
	public ResponseEntity<?> getRewards(
			@Parameter(description = "Customer ID", required = true) @PathVariable Long customerId,
			@Parameter(description = "Start date (yyyy-MM-dd)", required = true, example = "2024-01-01") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@Parameter(description = "End date (yyyy-MM-dd)", required = true, example = "2024-03-31") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

		logger.debug("Calculating rewards for customer : {}", customerId);
		Map<String, Object> rewards = rewardsService.calculateRewards(customerId, startDate, endDate);
		logger.info("Rewards calculated successfully for customer: {}", customerId);
		return new ResponseEntity<>(rewards, HttpStatus.OK);
	}
}