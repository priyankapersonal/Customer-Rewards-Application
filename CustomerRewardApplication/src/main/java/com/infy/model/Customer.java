package com.infy.model;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a customer in the system.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long customerId;

	private String customerName;

	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
	private List<Transaction> transaction;

	@Override
	public String toString() {
		return "Customer{id=" + customerId + ", name=" + customerName + "}";
	}
}