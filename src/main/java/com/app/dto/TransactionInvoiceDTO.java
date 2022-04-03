package com.app.dto;

import java.util.List;

import lombok.Data;

@Data
public class TransactionInvoiceDTO {
	
	private String transactionName;
	private String transactionStatus;
	private String transactionType;
	private String companyName;
	private List<ProductListDTO> productList;
	private String shippingAddress;
}
