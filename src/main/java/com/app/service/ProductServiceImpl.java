package com.app.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.custom_exceptions.ResourceNotFoundException;
import com.app.dao.CategoryRepository;
import com.app.dao.ProductRepository;
import com.app.dao.UserRepository;
import com.app.dto.ChartDataDTO;
import com.app.dto.ProductDTO;
import com.app.dto.ProductResponseDTO;
import com.app.dto.StockSummaryDTO;
import com.app.pojos.Category;
import com.app.pojos.Product;
import com.app.pojos.Role;
import com.app.pojos.User;
import com.app.pojos.UserRoles;

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
	public ProductDTO getProductById(int pId) {
		// TODO Auto-generated method stub
		Product product = productRepo.findById(pId)
				.orElseThrow(() -> new ResourceNotFoundException("Product with ID " + pId + " not found!!!!!!!!!"));
		return new ProductDTO(product);
	}

	@Override
	public ProductResponseDTO getPage(int pNo, String filter, String userName) {
		final int PAGE_SIZE = 10;
		User user = userRepo.findByUserName(userName)
				.orElseThrow(() -> new ResourceNotFoundException("user not found"));
		if (!user.getRoles().contains(new Role(UserRoles.ROLE_COMPANYOWNER))) {
			user = userRepo.findOwner(user.getCompany().getId())
					.orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
		}
		Slice<Product> slice;
		if (filter.equals("low"))
			slice = productRepo.findByUserLowStock(user, PageRequest.of(pNo, PAGE_SIZE));
		else if (filter.equals("excess"))
			slice = productRepo.findByUserExcessStock(user, PageRequest.of(pNo, PAGE_SIZE));
		else
			slice = productRepo.findByUser(user, PageRequest.of(pNo, PAGE_SIZE));
		List<ProductDTO> prodList = slice.getContent().stream().map(e -> new ProductDTO(e))
				.collect(Collectors.toList());
		int pageNo = slice.getNumber();
		int numberOfElements = slice.getNumberOfElements();
		boolean nextExists = slice.hasNext();

		return new ProductResponseDTO(prodList, pageNo, numberOfElements, nextExists);
	}

	@Override
	public void addProduct(ProductDTO productDTO, User user, Category category) {
		if (!user.getRoles().contains(new Role(UserRoles.ROLE_COMPANYOWNER))) {
			user = userRepo.findOwner(user.getCompany().getId())
					.orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
		}
		Product product = new Product(productDTO.getProductName(), productDTO.getStocks(), productDTO.getUnit(),
				productDTO.getPrice(), productDTO.getMinStock(), productDTO.getMaxStock(), category, user);
		productRepo.save(product);
	}

	@Override
	public void addProducts(List<ProductDTO> products, User user) {
		List<Product> productList = products.stream()
				.map(p -> new Product(p, user, categoryRepo.findByName(p.getCategoryName()).get()))
				.collect(Collectors.toList());
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
	public ProductResponseDTO getProductByName(String name, User user, int pNo) {
		if (!user.getRoles().contains(new Role(UserRoles.ROLE_COMPANYOWNER))) {
			user = userRepo.findOwner(user.getCompany().getId())
					.orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
		}
		Slice<Product> slice = productRepo.findMatchingProducts(name, user.getId(), PageRequest.of(pNo, 10));
		List<ProductDTO> prodList = slice.getContent().stream().map(e -> new ProductDTO(e))
				.collect(Collectors.toList());
		int pageNo = slice.getNumber();
		int numberOfElements = slice.getNumberOfElements();
		boolean nextExists = slice.hasNext();

		return new ProductResponseDTO(prodList, pageNo, numberOfElements, nextExists);
	}

	@Override
	public StockSummaryDTO getStockSummary(User user) {
		if (!user.getRoles().contains(new Role(UserRoles.ROLE_COMPANYOWNER))) {
			user = userRepo.findOwner(user.getCompany().getId())
					.orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
		}
		StockSummaryDTO summary = new StockSummaryDTO();
		summary.setLowStock(productRepo.countLowStock(user));
		summary.setExcessStock(productRepo.countExcessStock(user));
		summary.setStockValue(productRepo.stockValue(user));
		summary.setTotalProducts(productRepo.countByUser(user));
		return summary;
	}

	@Override
	public long countByUser(User user) {
		return productRepo.countByUser(user);
	}

	@Override
	public ChartDataDTO getStockValuationByCategory(User user) {
		if (!user.getRoles().contains(new Role(UserRoles.ROLE_COMPANYOWNER))) {
			user = userRepo.findOwner(user.getCompany().getId())
					.orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
		}
		ChartDataDTO out = new ChartDataDTO();
		productRepo.stockValuationByCategory(user).forEach(o -> {
			out.getData().add(o.getValue());
			out.getLabels().add(o.getCategory());
		});
		return out;
	}

	@Override
	public ChartDataDTO getProductCountPerCategory(User user) {
		if (!user.getRoles().contains(new Role(UserRoles.ROLE_COMPANYOWNER))) {
			user = userRepo.findOwner(user.getCompany().getId())
					.orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
		}
		ChartDataDTO out = new ChartDataDTO();
		productRepo.countPerCategory(user).forEach(o -> {
			out.getData().add(o.getValue());
			out.getLabels().add(o.getCategory());
		});
		return out;
	}

	@Override
	public ProductResponseDTO getAllProductByCategory(int pNo, String category, User user) {
		if (!user.getRoles().contains(new Role(UserRoles.ROLE_COMPANYOWNER))) {
			user = userRepo.findOwner(user.getCompany().getId())
					.orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
		}
		final int PAGE_SIZE = 10;
		Category cat = categoryRepo.findByName(category)
				.orElseThrow(() -> new ResourceNotFoundException("Category Not Found"));
		Slice<Product> slice = productRepo.findByCategoryAndUser(cat, user, PageRequest.of(pNo, PAGE_SIZE));
		List<ProductDTO> productDTO = slice.getContent().stream().map(e -> new ProductDTO(e))
				.collect(Collectors.toList());
		int pageNo = slice.getNumber();
		int numberOfElements = slice.getNumberOfElements();
		boolean nextExists = slice.hasNext();

		return new ProductResponseDTO(productDTO, pageNo, numberOfElements, nextExists);
	}
	
	@Override
	public ProductDTO getSingleProductByName(String name, User user) {
		if (!user.getRoles().contains(new Role(UserRoles.ROLE_COMPANYOWNER))) {
			user = userRepo.findOwner(user.getCompany().getId())
					.orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
		}
		Product product = productRepo.findByProductNameAndUser(name, user)
				.orElseThrow(() -> new ResourceNotFoundException("Product Not Found"));
		return new ProductDTO(product);
	}

}
