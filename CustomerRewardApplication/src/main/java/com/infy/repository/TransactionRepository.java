package com.infy.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.infy.model.Transaction;


@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>{
	
	List<Transaction> findByCustomerCustomerIdAndDateBetween(Long customerId, LocalDate startDate, LocalDate endDate);
	
	List<Transaction> findByCustomerCustomerId(Long customerId);

}
