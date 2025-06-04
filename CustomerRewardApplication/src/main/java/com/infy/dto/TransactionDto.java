package com.infy.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotNull;


public class TransactionDto {
	
	@Positive(message="Transaction amount must be positive")
	private double amount;
	
	@NotNull(message="Transaction date cannot be null")
	private LocalDate date;

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}
	
	

}
