package com.app.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductResponseDTO {
	List<ProductDTO> products;
	int pageNo;
	int numberOfElements;
	boolean nextExists;
}
