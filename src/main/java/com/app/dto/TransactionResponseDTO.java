package com.app.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionResponseDTO {
	List<TransactionDTO> transactions;
	int pageNo;
	int numberOfElements;
	boolean nextExists;
}
