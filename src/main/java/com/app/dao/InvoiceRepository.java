package com.app.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.pojos.Invoice;
import com.app.pojos.Transaction;

public interface InvoiceRepository extends JpaRepository<Invoice, Integer>  {
	
	Optional<Invoice> findByTransaction(Transaction transaction);
	

	
}
