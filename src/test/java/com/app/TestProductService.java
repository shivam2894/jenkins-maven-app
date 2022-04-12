package com.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

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


	@Test
	void testGetAllProductByCategory() {

		User user = new User();
		Set<Role> sr = new HashSet<Role>();
		sr.add(new Role(UserRoles.ROLE_COMPANYOWNER));
		user.setRoles(sr);
		Category category = new Category("dummy", user);
		List<Product> list = new ArrayList<Product>();
		list.add(new Product(1, "gloves", 10, Unit.NUMBER, 100.0, 5, 20, category, user));
		Slice<Product> s = new SliceImpl<>(list, PageRequest.of(0, 10), false);
		when(catRepo.findByName(category.getName())).thenReturn(Optional.of(category));
		when(productRepo.findByCategoryAndUser(category, user, PageRequest.of(0, 10))).thenReturn(s);		
		assertEquals("gloves", productService.getAllProductByCategory(0, category.getName(), user).getProducts()
				.get(0).getProductName());
	}
}
