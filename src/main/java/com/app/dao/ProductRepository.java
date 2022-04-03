package com.app.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.pojos.Category;
import com.app.pojos.Product;
import com.app.pojos.User;

public interface ProductRepository extends JpaRepository<Product, Integer> {
	Optional<Product> findByProductNameAndUser(String name, User user);

	List<Product> findByUser(User user, Pageable pageable);

	List<Product> findByCategoryAndUser(Category category, User user, Pageable pageable);
	
	long countByUser(User user);

	@Query("SELECT p FROM Product p WHERE p.stocks < p.minStock AND p.user = :user")
	List<Product> findByUserLowStock(User user, Pageable pageable);

	@Query("SELECT p FROM Product p WHERE p.stocks > p.maxStock AND p.user = :user")
	List<Product> findByUserExcessStock(User user, Pageable pageable);

	@Query("SELECT COUNT(p) FROM Product p WHERE p.stocks < p.minStock AND p.user = :user")
	long countLowStock(@Param("user") User user);

	@Query("SELECT COUNT(p) FROM Product p WHERE p.stocks > p.maxStock AND p.user = :user")
	long countExcessStock(@Param("user") User user);

	@Query("SELECT SUM(p.stocks * p.price) FROM Product p WHERE p.user = :user")
	long stockValue(@Param("user") User user);

}
