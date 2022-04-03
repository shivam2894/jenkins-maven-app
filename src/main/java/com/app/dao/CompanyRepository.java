package com.app.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.app.pojos.Company;

public interface CompanyRepository extends JpaRepository<Company, Integer> {

	// inherited method save() -> to register a new company

	Optional<Company> findByCompanyName(String companyName);
	List<Company> findByContactOf(int cId,Pageable pageable);

}
