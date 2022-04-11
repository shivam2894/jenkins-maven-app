package com.app.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class InvoiceDTO {
	
	private String ownerCompanyName;
	private String ownerCompanyAddress;
	private String ownerEmail;
	
	private LocalDate orderDate;
	private int invoiceId;
	private String companyAddress;
	private double total;
	private List<InvoiceProductListDTO> productList;
	
}
