package com.app.dto;

import lombok.Data;

@Data
public class StockSummaryDTO {
	private double stockValue;
	private long lowStock;
	private long excessStock;
	private long totalProducts;
}
