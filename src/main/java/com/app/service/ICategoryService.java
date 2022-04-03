package com.app.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.app.dto.CategoryDTO;
import com.app.pojos.Category;
import com.app.pojos.User;

@Service
@Transactional
public interface ICategoryService {
	Category addCategoryByName(String categoryName, User user);;
	List<CategoryDTO> getAllCategories(User user);
	void addAllCategories(List<String> categoryNames, User user);
}
