package com.app.dto;

import com.app.pojos.Product;
import com.app.pojos.Unit;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductDTO {
//p.id, p.productName, p.stocks, p.unit, p.price, p.minStock, p.maxStock, p.category.name
	private int id;
	private String productName;
	private int stocks;
	private Unit unit;
	private double price;
	private int minStock;
	private int maxStock;
	private String categoryName;
	
	public ProductDTO(Product product){
		this.id = product.getId();
		this.productName = product.getProductName();
		this.stocks = product.getStocks();
		this.unit = product.getUnit();
		this.price = product.getPrice();
		this.minStock = product.getMinStock();
		this.maxStock = product.getMaxStock();
		this.categoryName = product.getCategory().getName();
	}

}