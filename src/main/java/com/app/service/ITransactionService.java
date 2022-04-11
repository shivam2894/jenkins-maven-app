package com.app.service;

import java.security.Principal;
import java.util.List;

import com.app.dto.MonthChartDataDTO;
import com.app.dto.TransactionDTO;
import com.app.dto.TransactionFilterDTO;
import com.app.dto.TransactionInvoiceDTO;
import com.app.dto.TransactionResponseDTO;
import com.app.pojos.Transaction;
import com.app.pojos.TransactionType;

public interface ITransactionService {
	List<Transaction> transactionsByTransactionType(String transactionType);
	List<Transaction> transactionsByTransactionStatus(String transactionStatus);
	String saveTransaction(Transaction transaction);
	TransactionResponseDTO getTransactionsByUser(int pNo,String userName,TransactionFilterDTO filters);
	TransactionDTO getTransactionByName(String name, Principal principal);
	TransactionDTO getTransactionByTransactionId(int id);
	List<TransactionDTO> getByTransactionStatus(String status, Principal principal);
	void createTransactionAndInvoice(TransactionInvoiceDTO tranDTO, Principal principal);
	long countOfTransactions(Principal principal);
	MonthChartDataDTO transactionValueByMonthAndType(TransactionType transactionType,Principal principal);
	void deleteTransactionById(int id);
	void updateTransactionStatus(int id, String status);
}
