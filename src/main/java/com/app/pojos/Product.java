package com.app.pojos;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.app.dto.ProductDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products", uniqueConstraints = @UniqueConstraint(columnNames = { "product_name", "user_id" }))
@AttributeOverride(name = "id", column = @Column(name = "product_id"))
public class Product extends BaseEntity {

	@Column(name = "product_name", nullable = false)
	@NotBlank
	private String productName;

	@Min(value = 0, message = "stock value can't be negative")
	private int stocks;

	@Enumerated(EnumType.STRING)
	@NotNull
	private Unit unit;

	@Min(value = 0, message = "Price value can't be negative")
	@NotNull
	@Column(nullable = false)
	private double price;

	@Column(name = "min_stock")
	@Min(value = 0, message = "Minimum stock value can't be negative")
	private int minStock;

	@Column(name = "max_stock")
	@Min(value = 0, message = "Maximum stock value can't be negative")
	private int maxStock;

	@ManyToOne
	@JoinColumn(name = "category_id", nullable = false)
	private Category category;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	public Product(ProductDTO productDTO, User user, Category category) {
		this.productName = productDTO.getProductName();
		this.stocks = productDTO.getStocks();
		this.unit = productDTO.getUnit();
		this.price = productDTO.getPrice();
		this.minStock = productDTO.getMinStock();
		this.maxStock = productDTO.getMaxStock();
		this.category = category;
		this.user = user;
	}
	
	public Product(int id, String productName, int stocks, Unit unit, double price, int minStock, int maxStock,
            Category category, User user) {
        super();
        this.setId(id);
        this.productName = productName;
        this.stocks = stocks;
        this.unit = unit;
        this.price = price;
        this.minStock = minStock;
        this.maxStock = maxStock;
        this.category = category;
        this.user = user;
    }
	
	@Override
	public String toString() {
		return "Products [id=" + getId() + ", productName=" + productName + ", category=" + category + ", stocks="
				+ stocks + ", unit=" + unit + ", price=" + price + ", minStock=" + minStock + ", maxStock=" + maxStock
				+ "]";
	}

}
