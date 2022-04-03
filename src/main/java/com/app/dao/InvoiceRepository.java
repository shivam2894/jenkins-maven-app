package com.app.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.pojos.Invoice;

public interface InvoiceRepository extends JpaRepository<Invoice, Integer>  {
	

}
