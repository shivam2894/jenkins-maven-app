package com.app.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.pojos.Category;
import com.app.pojos.User;

public interface CategoryRepository extends JpaRepository<Category, Integer>{
	Optional<Category> findByName(String categoryName);
	
	@Query("select c from Category c where :user MEMBER OF c.users")
	List<Category> findAllByUser(@Param("user") User user);
}
