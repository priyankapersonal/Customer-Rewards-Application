package com.infy.dto;
import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for individual transaction details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {

	@Positive(message = "Transaction amount must be positive")
	private double amount;

	@NotNull(message = "Transaction date cannot be null")
	private LocalDate date;
}