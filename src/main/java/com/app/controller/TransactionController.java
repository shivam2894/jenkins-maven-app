package com.app.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.dto.TransactionDTO;
import com.app.dto.TransactionFilterDTO;
import com.app.dto.TransactionInvoiceDTO;
import com.app.pojos.TransactionType;
import com.app.service.ITransactionService;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "http://localhost:3000")
public class TransactionController {
	@Autowired
	private ITransactionService transactionService;

	@PostMapping("/page/{pNo}")
	public ResponseEntity<?> fetchTransactionPages(@RequestBody TransactionFilterDTO filters, @PathVariable int pNo,
			Principal principal) {
		System.out.println("in fetch all transaction by user");
		System.out.println(principal);
		return new ResponseEntity<>(transactionService.getTransactionsByUser(pNo, principal.getName(), filters),
				HttpStatus.OK);
	}

	@GetMapping("/tranName/{name}")
	public ResponseEntity<?> getTransactionByTransactionName(@PathVariable String name, Principal principal) {
		TransactionDTO tranDTOObj = transactionService.getTransactionByName(name, principal);
		return new ResponseEntity<>(tranDTOObj, HttpStatus.OK);
	}

	@GetMapping("/id/{id}")
	public ResponseEntity<?> getTransactionByTransactionId(@PathVariable int id) {
		TransactionDTO tranDTOObj = transactionService.getTransactionByTransactionId(id);
		return new ResponseEntity<>(tranDTOObj, HttpStatus.OK);
	}

	@GetMapping("/status/{status}")
	public ResponseEntity<?> getTransactionByTransactionStatus(@PathVariable String status, Principal principal) {
		List<TransactionDTO> tranDTOObj = transactionService.getByTransactionStatus(status, principal);
		return new ResponseEntity<>(tranDTOObj, HttpStatus.OK);
	}

	@PostMapping("/createTran")
	public ResponseEntity<?> createTransaction(@RequestBody TransactionInvoiceDTO tranDTO, Principal principal) {
		transactionService.createTransactionAndInvoice(tranDTO, principal);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@GetMapping("/tranCount")
	public ResponseEntity<?> getTransactionCount(Principal principal) {
		return new ResponseEntity<>(transactionService.countOfTransactions(principal), HttpStatus.OK);
	}

	@GetMapping("/type/{type}")
	public ResponseEntity<?> getTransactionCount(@PathVariable String type, Principal principal) {
		return new ResponseEntity<>(transactionService
				.transactionValueByMonthAndType(TransactionType.valueOf(type.toUpperCase()), principal), HttpStatus.OK);
	}

	@DeleteMapping("/delete/{txId}")
	public ResponseEntity<?> deleteTransaction(@PathVariable int txId, Principal principal) {
		transactionService.deleteTransactionById(txId);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PatchMapping("/update/{txId}")
	public ResponseEntity<?> updateTransactionStatus(@PathVariable int txId, @RequestParam String status, Principal principal) {
		System.out.println(txId+" "+status);
		transactionService.updateTransactionStatus(txId, status);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}