package com.app.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.dao.CategoryRepository;
import com.app.dto.CategoryDTO;
import com.app.pojos.Category;
import com.app.pojos.User;

@Service
@Transactional
public class CategoryServiceImpl implements ICategoryService {

	@Autowired
	private CategoryRepository categoryRepo;

	@Override
	public Category addCategoryByName(String categoryName, User user) {
		Optional<Category> category = categoryRepo.findByName(categoryName.toUpperCase());
		if (category.isPresent()) {
			if (category.get().getUsers().contains(user)) {
				return category.get();
			} else {
				category.get().getUsers().add(user);
				return categoryRepo.save(category.get());
			}
		} else
			return categoryRepo.save(new Category(categoryName.toUpperCase(), user));
	}

	@Override
	public List<CategoryDTO> getAllCategories(User user) {
		return categoryRepo.findAllByUser(user).stream().map(c -> new CategoryDTO(c)).collect(Collectors.toList());
	}

	@Override
	public void addAllCategories(List<String> categoryNames, User user) {
		categoryNames.forEach(categoryName -> addCategoryByName(categoryName, user));
	}

}
