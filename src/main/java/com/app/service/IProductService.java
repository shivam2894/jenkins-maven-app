package com.app.service;

import java.util.List;

import com.app.dto.ProductDTO;
import com.app.dto.StockSummaryDTO;
import com.app.pojos.Category;
import com.app.pojos.Product;
import com.app.pojos.User;

public interface IProductService {

	List<Product> getAllProducts();

	Product getProductById(int pId);

	ProductDTO getProductByName(String name, User user);

	List<ProductDTO> getAllProductByCategory(int pNo, Category category, User user);

	List<ProductDTO> getPage(int pNo, String filter, String user);

	void addProduct(ProductDTO productDTO, User user, Category category);
	
	void addProducts(List<ProductDTO> products,User user);

	void editProduct(ProductDTO productDTO, int productId, Category category);

	void deleteProductById(int productId);

	StockSummaryDTO getStockSummary(User user);

	long countByUser(User user);
}
