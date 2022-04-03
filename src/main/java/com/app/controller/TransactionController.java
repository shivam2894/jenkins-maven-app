package com.app.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.dto.FromToDateDTO;
import com.app.dto.TransactionDTO;
import com.app.dto.TransactionInvoiceDTO;
import com.app.pojos.Invoice;
import com.app.service.ITransactionService;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "http://localhost:3000")
public class TransactionController {
	@Autowired
	private ITransactionService transactionService;

	@GetMapping("/page/{pNo}")
	public ResponseEntity<?> fetchTransactionPages(@PathVariable int pNo, Principal principal) {
		System.out.println("in fetch all transaction by user");
		System.out.println(principal);
		return new ResponseEntity<>(transactionService.getTransactionsByUser(pNo, principal.getName()), HttpStatus.OK);
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

	@GetMapping("/CompanyName/{compName}")
	public ResponseEntity<?> getTransactionByCompanyName(@PathVariable String compName, Principal principal) {
		List<TransactionDTO> tranDTOObj = transactionService.getTransactionByCompName(compName, principal);
		return new ResponseEntity<>(tranDTOObj, HttpStatus.OK);
	}

	@PostMapping("/filterByDate/{pNo}")
	public ResponseEntity<?> filterTransactionsByDate(@RequestBody FromToDateDTO dates, @PathVariable int pNo,
			Principal principal) {

		return new ResponseEntity<>(transactionService.filterTranByDate(LocalDate.parse(dates.getFrom_date()),
				LocalDate.parse(dates.getTo_date()), pNo, principal.getName()), HttpStatus.OK);
	}
	
	@GetMapping("/status/{status}")
	public ResponseEntity<?> getTransactionByTransactionStatus(@PathVariable String status,Principal principal)
	{
		List<TransactionDTO> tranDTOObj = transactionService.getByTransactionStatus(status, principal);		
		return new ResponseEntity<>(tranDTOObj, HttpStatus.OK);
	}
	
	@PostMapping("/createTran")
	public ResponseEntity<?> createTransaction(@RequestBody TransactionInvoiceDTO tranDTO, Principal principal)
	{
		Invoice invoice= transactionService.createTransactionAndInvoice(tranDTO, principal);
		return new ResponseEntity<>(invoice,HttpStatus.CREATED);		
	}
	
	@GetMapping("/tranCount")
	public ResponseEntity<?> getTransactionCount(Principal principal) {
		return new ResponseEntity<>(transactionService.countOfTransactions(principal), HttpStatus.OK);
	}
	
}
