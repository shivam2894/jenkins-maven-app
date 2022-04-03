package com.app.pojos;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductList {
	@Column(name="product_count" , nullable=false)
	private int productCount;
	@OneToOne
	@JoinColumn(name = "product_id",nullable=false)
	private Product  product;
	
	
	@Override
	public String toString() {
		return "ProductList [productId=" + product.getId() + ", productCount=" + productCount + "]";
	}
	

	

}
