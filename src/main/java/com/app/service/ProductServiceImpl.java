package com.app.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.custom_exceptions.ResourceNotFoundException;
import com.app.dao.CategoryRepository;
import com.app.dao.ProductRepository;
import com.app.dao.UserRepository;
import com.app.dto.ProductDTO;
import com.app.dto.StockSummaryDTO;
import com.app.pojos.Category;
import com.app.pojos.Product;
import com.app.pojos.User;

@Service
@Transactional
public class ProductServiceImpl implements IProductService {

	@Autowired
	private ProductRepository productRepo;
	@Autowired
	private CategoryRepository categoryRepo;
	@Autowired
	private UserRepository userRepo;

	@Override
	public List<Product> getAllProducts() {
		// TODO Auto-generated method stub
		return productRepo.findAll();
	}

	@Override
	public Product getProductById(int pId) {
		// TODO Auto-generated method stub
		return productRepo.findById(pId)
				.orElseThrow(() -> new ResourceNotFoundException("Product with ID " + pId + " not found!!!!!!!!!"));
	}

	@Override
	public List<ProductDTO> getPage(int pNo,String filter ,String userName) {
		final int PAGE_SIZE = 10;
		User user = userRepo.findByUserName(userName).orElseThrow(() -> new RuntimeException("user nahi mila"));
		if(filter.equals("low"))
			return productRepo.findByUserLowStock(user, PageRequest.of(pNo, PAGE_SIZE)).stream().map(e -> new ProductDTO(e))
					.collect(Collectors.toList());
		if(filter.equals("excess"))
			return productRepo.findByUserExcessStock(user, PageRequest.of(pNo, PAGE_SIZE)).stream().map(e -> new ProductDTO(e))
					.collect(Collectors.toList());
		return productRepo.findByUser(user, PageRequest.of(pNo, PAGE_SIZE)).stream().map(e -> new ProductDTO(e))
				.collect(Collectors.toList());
	}

	@Override
	public void addProduct(ProductDTO productDTO, User user, Category category) {
		Product product = new Product(productDTO.getProductName(), productDTO.getStocks(), productDTO.getUnit(),
				productDTO.getPrice(), productDTO.getMinStock(), productDTO.getMaxStock(), category, user);
		productRepo.save(product);
	}
	
	@Override
	public void addProducts(List<ProductDTO> products, User user) {
		List<Product> productList = products.stream().map(p->new Product(p, user, categoryRepo.findByName(p.getCategoryName()).get())).collect(Collectors.toList());
		productRepo.saveAll(productList);
	}

	@Override
	public void editProduct(ProductDTO productDTO, int productId, Category category) {
		Product product = productRepo.getById(productId);
		if (!product.getProductName().equals(productDTO.getProductName())) {
			product.setProductName(productDTO.getProductName());
		}
		if (product.getCategory() != category) {
			product.setCategory(category);
		}
		if (product.getStocks() != productDTO.getStocks()) {
			product.setStocks(productDTO.getStocks());
		}
		if (product.getMinStock() != productDTO.getMinStock()) {
			product.setMinStock(productDTO.getMinStock());
		}
		if (product.getMaxStock() != productDTO.getMaxStock()) {
			product.setMaxStock(productDTO.getMaxStock());
		}
		if (product.getPrice() != productDTO.getPrice()) {
			product.setPrice(productDTO.getPrice());
		}
		if (product.getUnit() != productDTO.getUnit()) {
			product.setUnit(productDTO.getUnit());
		}
	}

	@Override
	public void deleteProductById(int productId) {
		productRepo.deleteById(productId);
	}

	@Override
	public ProductDTO getProductByName(String name, User user) {
		Product product = productRepo.findByProductNameAndUser(name, user)
				.orElseThrow(() -> new RuntimeException("Product Not Found"));
		return new ProductDTO(product);
	}

	@Override
	public StockSummaryDTO getStockSummary(User user) {
		StockSummaryDTO summary = new StockSummaryDTO();
		summary.setLowStock(productRepo.countLowStock(user));
		summary.setExcessStock(productRepo.countExcessStock(user));
		summary.setStockValue(productRepo.stockValue(user));
		summary.setTotalProducts(productRepo.countByUser(user));
		return summary;
	}

	@Override
	public List<ProductDTO> getAllProductByCategory(int pNo,Category category, User user) {
		final int PAGE_SIZE = 10;
		return productRepo.findByCategoryAndUser(category,user, PageRequest.of(pNo, PAGE_SIZE)).stream().map(e -> new ProductDTO(e))
				.collect(Collectors.toList());
	}

	@Override
	public long countByUser(User user) {
		return productRepo.countByUser(user);
	}

}
