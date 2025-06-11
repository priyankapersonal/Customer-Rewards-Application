package com.infy.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a transaction made by a customer.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long transactionId;

	private double amount;

	private LocalDate date;

	@ManyToOne
	@JoinColumn(name = "customerId")
	@JsonBackReference
	private Customer customer;

	@Override
	public String toString() {
		return "Transaction{id=" + transactionId + ", amount=" + amount + ", date=" + date + "}";
	}
}