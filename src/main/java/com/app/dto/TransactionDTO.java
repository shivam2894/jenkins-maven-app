package com.app.dto;

import java.time.LocalDate;

import com.app.pojos.Transaction;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionDTO {
	private int id;
	private String transactionName;
	private String transactionType;
	private String transactionStatus;
	private LocalDate lastModifiedDate;
	private String companyName;

	public TransactionDTO(Transaction transaction) {
		this.id = transaction.getId();
		this.transactionName = transaction.getTransactionName();
		this.transactionStatus = transaction.getTransactionStatus().name();
		this.transactionType = transaction.getTransactionType().name();
		this.lastModifiedDate = transaction.getLastModifiedDate();
		this.companyName = transaction.getCompanyName();
	}

}
