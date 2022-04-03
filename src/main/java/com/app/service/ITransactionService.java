package com.app.service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

import com.app.dto.TransactionDTO;
import com.app.dto.TransactionInvoiceDTO;
import com.app.pojos.Invoice;
import com.app.pojos.Transaction;
import com.app.pojos.TransactionStatus;

public interface ITransactionService {
	List<Transaction> transactionsByTransactionType(String transactionType);
	List<Transaction> transactionsByTransactionStatus(String transactionStatus);
	String saveTransaction(Transaction transaction);
	List<TransactionDTO> getTransactionsByUser(int pNo,String userName);
	TransactionDTO getTransactionByName(String name, Principal principal);
	TransactionDTO getTransactionByTransactionId(int id);
	List<TransactionDTO> getTransactionByCompName(String compName,Principal principal);
	List<TransactionDTO> filterTranByDate(LocalDate from_date,LocalDate to_date,int pNo, String userName);
	List<TransactionDTO> getByTransactionStatus(String status, Principal principal);
	Invoice createTransactionAndInvoice(TransactionInvoiceDTO tranDTO, Principal principal);
	public long countOfTransactions(Principal principal);
}
