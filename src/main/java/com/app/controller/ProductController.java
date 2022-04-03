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

import com.app.dto.ProductDTO;
import com.app.pojos.Category;
import com.app.pojos.User;
import com.app.service.ICategoryService;
import com.app.service.IProductService;
import com.app.service.IUserService;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {
	
	@Autowired
	private IProductService productService;
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private ICategoryService categoryService;
	
	@GetMapping
	public ResponseEntity<?> fetchAllProducts(){
		System.out.println("in fetch all products");
		return new ResponseEntity<>(productService.getAllProducts(),HttpStatus.OK);
	}
	
	@GetMapping("/{pId}")
	public ResponseEntity<?> fetchProductById(@PathVariable int pId){
		System.out.println("in fetch all product by id");
		return new ResponseEntity<>(productService.getProductById(pId),HttpStatus.OK);
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
		Category category = categoryService.addCategoryByName(productDTO.getCategoryName(), user);
		productService.addProduct(productDTO, user, category);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
	
	@PatchMapping("/edit/{pId}")
	public ResponseEntity<?> editProduct(@PathVariable int pId, @RequestBody ProductDTO productDTO, Principal principal){
		User user = userService.getUserByUsername(principal.getName());
		Category category = categoryService.addCategoryByName(productDTO.getCategoryName(), user);
		productService.editProduct(productDTO, pId, category);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PostMapping("/upload")
	public ResponseEntity<?> uploadProducts(@RequestBody List<ProductDTO> products, Principal principal){
		User user = userService.getUserByUsername(principal.getName());
		categoryService.addAllCategories(products.stream().map(p->p.getCategoryName()).collect(Collectors.toList()), user);
		productService.addProducts(products, user);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
	
	@GetMapping("/categories")
	public ResponseEntity<?> getAllCategories(Principal principal){
		User user = userService.getUserByUsername(principal.getName());
		return new ResponseEntity<>(categoryService.getAllCategories(user),HttpStatus.OK);
	}
	
	@DeleteMapping("/delete/{pId}")
	public ResponseEntity<?> deleteProductById(@PathVariable int pId){
		productService.deleteProductById(pId);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@GetMapping("/name/{pName}")
	public ResponseEntity<?> fetchProductByName(@PathVariable String pName, Principal principal){
		User user = userService.getUserByUsername(principal.getName());
		return new ResponseEntity<>(productService.getProductByName(pName, user),HttpStatus.OK);
	}
	
	@GetMapping("/stocksummary")
	public ResponseEntity<?> fetchStockSummary(Principal principal){
		User user = userService.getUserByUsername(principal.getName());
		return new ResponseEntity<>(productService.getStockSummary(user), HttpStatus.OK);
	}
}
