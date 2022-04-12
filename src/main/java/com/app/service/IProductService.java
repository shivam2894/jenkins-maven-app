package com.app.service;

import java.security.Principal;
import java.util.List;

import com.app.dto.ChartDataDTO;
import com.app.dto.ProductDTO;
import com.app.dto.ProductResponseDTO;
import com.app.dto.StockSummaryDTO;
import com.app.pojos.Category;
import com.app.pojos.Product;
import com.app.pojos.User;

public interface IProductService {

	List<Product> getAllProducts();

	ProductDTO getProductById(int pId, Principal principal);

	ProductResponseDTO getProductByName(String name, User user, int pageNo);

	ProductResponseDTO getAllProductByCategory(int pNo, String category, User user);

	ProductResponseDTO getPage(int pNo, String filter, String user);

	void addProduct(ProductDTO productDTO, User user, Category category);
	
	void addProducts(List<ProductDTO> products,User user);

	void editProduct(ProductDTO productDTO, int productId, Category category);

	void deleteProductById(int productId);

	StockSummaryDTO getStockSummary(User user);

	long countByUser(User user);
	
	ChartDataDTO getStockValuationByCategory(User user);
	
	ChartDataDTO getProductCountPerCategory(User user);
	
	ProductDTO getSingleProductByName(String name, User user);
}
