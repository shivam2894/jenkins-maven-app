package com.app.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.dto.IStockValuationByCategoryDTO;
import com.app.pojos.Category;
import com.app.pojos.Product;
import com.app.pojos.User;

public interface ProductRepository extends JpaRepository<Product, Integer> {
	//Pageable :  Spring Data infrastructure will recognizes this parameter automatically to apply pagination and sorting to database.
	
	Optional<Product> findByProductNameAndUser(String name, User user);
	
	@Query(value = "SELECT * FROM products where user_id = :userId and product_name LIKE %:name% ", nativeQuery = true)
	Slice<Product> findMatchingProducts(String name, int userId, Pageable pageable);

	Slice<Product> findByUser(User user, Pageable pageable);
	
	Optional<Product> findByIdAndUser(int id , User user);

//	List<Product> findByCategoryAndUser(Category category, User user, Pageable pageable);
	Slice<Product> findByCategoryAndUser(Category category, User user, Pageable pageable);
	
	long countByUser(User user);

	@Query("SELECT p FROM Product p WHERE p.stocks < p.minStock AND p.user = :user")
	Slice<Product> findByUserLowStock(User user, Pageable pageable);

	@Query("SELECT p FROM Product p WHERE p.stocks > p.maxStock AND p.user = :user")
	Slice<Product> findByUserExcessStock(User user, Pageable pageable);

	@Query("SELECT COUNT(p) FROM Product p WHERE p.stocks < p.minStock AND p.user = :user")
	long countLowStock(@Param("user") User user);

	@Query("SELECT COUNT(p) FROM Product p WHERE p.stocks > p.maxStock AND p.user = :user")
	long countExcessStock(@Param("user") User user);

	@Query("SELECT SUM(p.stocks * p.price) FROM Product p WHERE p.user = :user")
	long stockValue(@Param("user") User user);
	
	@Query("SELECT p.category.name AS category, SUM(p.stocks * p.price) AS value FROM Product AS p WHERE p.user = :user GROUP BY p.category")
	List<IStockValuationByCategoryDTO> stockValuationByCategory(@Param("user") User user);
	
	@Query("SELECT p.category.name AS category, SUM(p.stocks) AS value FROM Product AS p WHERE p.user = :user GROUP BY p.category")
	List<IStockValuationByCategoryDTO> countPerCategory(@Param("user") User user);

}
