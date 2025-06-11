package com.infy;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infy.dto.CustomerDto;
import com.infy.dto.TransactionDto;
import com.infy.model.Customer;

/**
 * Integration tests for the RewardsController.
 * <p>
 * Verifies REST API behavior using a full Spring context with actual
 * request/response flow, including validation and error handling.
 * </p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:application.properties")
public class RewardsControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private CustomerDto customerDto;

	/**
	 * Sets up a sample customer DTO before each test.
	 */
	@BeforeEach
	void setup() {
		customerDto = new CustomerDto();
		customerDto.setCustomerName("Sam");

		TransactionDto transaction = new TransactionDto();
		transaction.setAmount(120.0);
		transaction.setDate(LocalDate.of(2024, 4, 15));
		customerDto.setTransaction(List.of(transaction));
	}

	/**
	 * Tests successful creation of a customer.
	 */
	@Test
	void testCreateCustomerSuccess() throws Exception {
		mockMvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(customerDto))).andExpect(status().isCreated())
				.andExpect(jsonPath("$.customerName").value("Sam"));
	}

	/**
	 * Tests creation of a customer with a blank name.
	 */
	@Test
	void testCreateCustomerBlankName() throws Exception {
		customerDto.setCustomerName("   ");

		mockMvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(customerDto))).andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("Customer name cannot be null or blank.")));
	}

	/**
	 * Tests creation of a customer with no transactions.
	 */
	@Test
	void testCreateCustomerNoTransaction() throws Exception {
		customerDto.setTransaction(List.of());

		mockMvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(customerDto))).andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("Transaction list cannot be empty")));
	}

	/**
	 * Tests creation of a customer with a null request body.
	 */
	@Test
	void testCreateCustomerNullBody() throws Exception {
		mockMvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("Customer data is missing")));
	}

	/**
	 * Tests reward calculation for a valid customer and transaction.
	 */
	@Test
	void testCalculateValidRewards() throws Exception {
		MvcResult result = mockMvc
				.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(customerDto)))
				.andExpect(status().isCreated()).andReturn();

		Customer savedCustomer = objectMapper.readValue(result.getResponse().getContentAsString(), Customer.class);

		mockMvc.perform(get("/api/customers/" + savedCustomer.getCustomerId() + "/rewards")
				.param("startDate", "2024-01-01").param("endDate", "2024-12-31")).andExpect(status().isOk())
				.andExpect(jsonPath("$['Total Rewards']").value(90));
	}

	/**
	 * Tests reward calculation with start date after end date.
	 */
	@Test
	void testCalculateRewardsInvalidDateOrder() throws Exception {
		mockMvc.perform(get("/api/customers/1/rewards").param("startDate", "2024-12-31").param("endDate", "2024-01-01"))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("Start date cannot be after end date.")));
	}

	/**
	 * Tests reward calculation with an invalid customer ID.
	 */
	@Test
	void testCalculateRewardsMissingCustomerId() throws Exception {
		mockMvc.perform(get("/api/customers/0/rewards").param("startDate", "2024-01-01").param("endDate", "2024-12-31"))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("Customer ID must be a positive number")));
	}
}