package com.app.dto;

import com.app.pojos.Category;

import lombok.Data;

@Data
public class CategoryDTO {
	
	String categoryName;

	public CategoryDTO(Category category) {
		this.categoryName = category.getName();
	}
}
