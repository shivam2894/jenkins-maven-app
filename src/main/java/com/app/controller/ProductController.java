package com.app.controller;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.custom_exceptions.ResourceNotFoundException;
import com.app.dao.UserRepository;
import com.app.dto.ProductDTO;
import com.app.pojos.Category;
import com.app.pojos.Role;
import com.app.pojos.User;
import com.app.pojos.UserRoles;
import com.app.service.ICategoryService;
import com.app.service.IProductService;
import com.app.service.IUserService;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://3.87.75.52:3000")
public class ProductController {
	
	@Autowired
	private IProductService productService;
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private ICategoryService categoryService;
	
	@Autowired
	private UserRepository userRepo;
	
	@GetMapping("/{pId}")
	public ResponseEntity<?> fetchProductById(@PathVariable int pId,Principal principal){
		System.out.println("in fetch all product by id");
		return new ResponseEntity<>(productService.getProductById(pId,principal),HttpStatus.OK);
	}
	
	@GetMapping("/page/{pNo}/filter/{filter}")
	public ResponseEntity<?> fetchProductPages(@PathVariable int pNo,@PathVariable String filter,Principal principal){
		System.out.println("in fetch all product by id");
		System.out.println(principal);
		return new ResponseEntity<>(productService.getPage(pNo,filter,principal.getName()),HttpStatus.OK);
	}
	
	@PostMapping("/add")
	public ResponseEntity<?> addProduct(@RequestBody ProductDTO productDTO, Principal principal){
		User user = userService.getUserByUsername(principal.getName());
		if(!user.getRoles().contains(new Role(UserRoles.ROLE_COMPANYOWNER))) {
			user = userRepo.findOwner(user.getCompany().getId()).orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
		}
		Category category = categoryService.addCategoryByName(productDTO.getCategoryName(), user);
		productService.addProduct(productDTO, user, category);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
	
	@PatchMapping("/edit/{pId}")
	public ResponseEntity<?> editProduct(@PathVariable int pId, @RequestBody ProductDTO productDTO, Principal principal){
		User user = userService.getUserByUsername(principal.getName());
		if(!user.getRoles().contains(new Role(UserRoles.ROLE_COMPANYOWNER))) {
			user = userRepo.findOwner(user.getCompany().getId()).orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
		}
		Category category = categoryService.addCategoryByName(productDTO.getCategoryName(), user);
		productService.editProduct(productDTO, pId, category);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PostMapping("/upload")
	public ResponseEntity<?> uploadProducts(@RequestBody List<ProductDTO> products, Principal principal){
		User user = userService.getUserByUsername(principal.getName());
		if(!user.getRoles().contains(new Role(UserRoles.ROLE_COMPANYOWNER))) {
			user = userRepo.findOwner(user.getCompany().getId()).orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
		}
		categoryService.addAllCategories(products.stream().map(p->p.getCategoryName()).collect(Collectors.toList()), user);
		productService.addProducts(products, user);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
	
	@GetMapping("/categories")
	public ResponseEntity<?> getAllCategories(Principal principal){
		User user = userService.getUserByUsername(principal.getName());
		if(!user.getRoles().contains(new Role(UserRoles.ROLE_COMPANYOWNER))) {
			user = userRepo.findOwner(user.getCompany().getId()).orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
		}
		return new ResponseEntity<>(categoryService.getAllCategories(user),HttpStatus.OK);
	}
	
	@DeleteMapping("/delete/{pId}")
	public ResponseEntity<?> deleteProductById(@PathVariable int pId){
		productService.deleteProductById(pId);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@GetMapping("/name/{pName}/{pNo}")
	public ResponseEntity<?> fetchProductByName(@PathVariable String pName, @PathVariable int pNo, Principal principal){
		User user = userService.getUserByUsername(principal.getName());
		return new ResponseEntity<>(productService.getProductByName(pName, user, pNo),HttpStatus.OK);
	}
	
	@GetMapping("/singleproduct/{pName}")
	public ResponseEntity<?> fetchSingleProductByName(@PathVariable String pName, Principal principal){
		User user = userService.getUserByUsername(principal.getName());
		return new ResponseEntity<>(productService.getSingleProductByName(pName, user),HttpStatus.OK);
	}
	
	@GetMapping("/stocksummary")
	public ResponseEntity<?> fetchStockSummary(Principal principal){
		User user = userService.getUserByUsername(principal.getName());
		return new ResponseEntity<>(productService.getStockSummary(user), HttpStatus.OK);
	}
	
	@GetMapping("/valuation_by_category")
	public ResponseEntity<?> fetchStockValuationByCategory(Principal principal){
		User user = userService.getUserByUsername(principal.getName());
		return new ResponseEntity<>(productService.getStockValuationByCategory(user), HttpStatus.OK);
	}
	
	@GetMapping("/countPerCat")
	public ResponseEntity<?> fetchCountPerCategory(Principal principal){
		User user = userService.getUserByUsername(principal.getName());
		return new ResponseEntity<>(productService.getProductCountPerCategory(user), HttpStatus.OK);
	}
	
}
