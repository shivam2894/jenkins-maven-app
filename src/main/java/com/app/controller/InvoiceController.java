package com.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.service.IInvoiceService;

@RestController
@RequestMapping("/api/invoice")
@CrossOrigin(origins = "http://localhost:3000")
public class InvoiceController {

	@Autowired
	private IInvoiceService invoiceService;

	@GetMapping("/details/{transactionId}")
	public ResponseEntity<?> getWholeInvoice(@PathVariable int transactionId) {

		return new ResponseEntity<>(invoiceService.getInvoiceDetails(transactionId), HttpStatus.OK);
	}

}
