package com.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InvoiceProductListDTO {
	
	private int productId;
	private String productName;
	private double rate;
	private int quantity;	
}
