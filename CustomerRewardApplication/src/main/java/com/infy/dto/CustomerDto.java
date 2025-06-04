package com.infy.dto;

import java.util.List;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CustomerDto {
	
		
	@NotNull(message="Customer name cannot be null")
	@NotBlank(message = "Customer name cannot be blank")
	@Size(max=100, message="Customer name cannot exceed 100 characters")
	private String customerName;
	
	@NotNull(message="Transaction list cannot be null")
	@Size(min=1, message="At least one transaction is required")
	private List<TransactionDto> transaction;

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public List<TransactionDto> getTransaction() {
		return transaction;
	}

	public void setTransaction(List<TransactionDto> transaction) {
		this.transaction = transaction;
	}
	
	

}
