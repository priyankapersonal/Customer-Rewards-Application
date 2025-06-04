package com.infy.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Transaction {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long TransactionId;
	
	private double amount;
	
	private LocalDate date;
	
	@ManyToOne
	@JoinColumn(name="customerId")
	@JsonIgnore
	private Customer customer;

	public Long getTransactionId() {
		return TransactionId;
	}

	public void setTransactionId(Long transactionId) {
		TransactionId = transactionId;
	}

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

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Transaction() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Transaction(Long transactionId, double amount, LocalDate date, Customer customer) {
		super();
		TransactionId = transactionId;
		this.amount = amount;
		this.date = date;
		this.customer = customer;
	}

	@Override
	public String toString() {
		return "Transaction [TransactionId=" + TransactionId + ", amount=" + amount + ", date=" + date + ", customer="
				+ customer + "]";
	}
	
	

}
