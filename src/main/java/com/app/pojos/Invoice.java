package com.app.pojos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

@Table(name="invoice")
public class Invoice extends BaseEntity {
	
	@Column(name="total_amt",nullable=false)
	private double totalAmount;
	
	@Column(name="shipping_addr",length=500,nullable=false)
	private String shippingAddress;
	
	@Column(name="date")
	@DateTimeFormat(pattern="yyyy/MM/dd")
	private LocalDate dateTime;	

	@OneToOne
	@JoinColumn(name = "transaction_id",nullable=false)
	@MapsId
	private Transaction transaction;
	
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name="product_list" , joinColumns = @JoinColumn(name="transaction_id"))	
	private List<ProductList> productList = new ArrayList<>();

	@Override
	public String toString() {
		return "Invoice [totalAmount=" + totalAmount + ", shippingAddress=" + shippingAddress + ", dateTime=" + dateTime
				+  "]";
	}


}
