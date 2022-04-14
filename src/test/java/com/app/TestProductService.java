package com.app;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.management.remote.JMXPrincipal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import com.app.custom_exceptions.ResourceNotFoundException;
import com.app.dao.CategoryRepository;
import com.app.dao.CompanyRepository;
import com.app.dao.ProductRepository;
import com.app.dao.UserRepository;
import com.app.pojos.Category;
import com.app.pojos.Product;
import com.app.pojos.Role;
import com.app.pojos.Unit;
import com.app.pojos.User;
import com.app.pojos.UserRoles;
import com.app.service.IProductService;

@SpringBootTest
class TestProductService {

	@Autowired
	private IProductService productService;

	@MockBean
	private UserRepository userRepo;

	@MockBean
	private ProductRepository productRepo;
	
	@MockBean
	private CompanyRepository companyRepo;

	@MockBean
	private CategoryRepository catRepo;
	
	private User user;
	private Principal principal;

	@BeforeEach
	void before() {
		user = new User();
		Set<Role> sr = new HashSet<Role>();
		sr.add(new Role(UserRoles.ROLE_COMPANYOWNER));
		user.setRoles(sr);
		user.setId(1);
		user.setName("test user");
		principal  = new JMXPrincipal(user.getName());
	}
	
	@Test
	void testGetProductById() {
		int id = 1;
		when(productRepo.findByIdAndUser(id,user)).thenReturn(
				Optional.of(new Product(id, "gloves", 10, Unit.NUMBER, 100.0, 5, 20, new Category(), new User())));
		when(userRepo.findByUserName(user.getName())).thenReturn(Optional.of(user));
		assertEquals("gloves", productService.getProductById(id,principal).getProductName());
	}

	@Test
	void testGetProductById2() {
		int id = 1;
		when(productRepo.findByIdAndUser(id,user)).thenReturn(
				Optional.of(new Product(id, "gloves", 10, Unit.NUMBER, 100.0, 5, 20, new Category(), new User())));
		when(userRepo.findByUserName(user.getName())).thenReturn(Optional.of(user));
		assertNotEquals("sunglass", productService.getProductById(id,principal).getProductName());
	}

	@Test
	void testGetProductById3() {
		int id = 1;
		when(productRepo.findByIdAndUser(id,user)).thenReturn(
				Optional.of(new Product(id, "gloves", 10, Unit.NUMBER, 100.0, 5, 20, new Category(), new User())));
		when(userRepo.findByUserName(user.getName())).thenReturn(Optional.of(user));
		assertThatExceptionOfType(ResourceNotFoundException.class).isThrownBy(() -> productService.getProductById(2,principal))
				.withMessage("Product with ID 2 not found!!!!!!!!!");
	}

	
	@Test
	void testGetAllProductByCategory() {
		Category category = new Category("dummy", user);
		List<Product> list = new ArrayList<Product>();
		list.add(new Product(1, "gloves", 10, Unit.NUMBER, 100.0, 5, 20, category, user));
		Slice<Product> s = new SliceImpl<>(list, PageRequest.of(0, 10), false);
		when(catRepo.findByName(category.getName())).thenReturn(Optional.of(category));
		when(productRepo.findByCategoryAndUser(category, user, PageRequest.of(0, 10))).thenReturn(s);		
		assertEquals("gloves", productService.getAllProductByCategory(0, category.getName(), user).getProducts()
				.get(0).getProductName());
	}
	
	@Test
	void testGetProductByName() {
		Category category = new Category("dummy", user);
		List<Product> list = new ArrayList<Product>();
		list.add(new Product(1, "gloves", 10, Unit.NUMBER, 100.0, 5, 20, category, user));
		Slice<Product> s = new SliceImpl<>(list, PageRequest.of(0, 10), false);
		when(catRepo.findByName(category.getName())).thenReturn(Optional.of(category));
		when(productRepo.findMatchingProducts("gloves", user.getId(), PageRequest.of(0, 10))).thenReturn(s);		
		assertEquals("gloves", productService.getProductByName("gloves", user, 0).getProducts()
				.get(0).getProductName());
	}
	
	@Test
	void testGetStockSummary() {
		when(productRepo.countLowStock(user)).thenReturn(Long.valueOf(2));
		when(productRepo.countExcessStock(user)).thenReturn(Long.valueOf(4));
		when(productRepo.stockValue(user)).thenReturn(Long.valueOf(987654));
		when(productRepo.countByUser(user)).thenReturn(Long.valueOf(100));
		assertEquals(productService.getStockSummary(user).getStockValue(), 987654);
	}
}
