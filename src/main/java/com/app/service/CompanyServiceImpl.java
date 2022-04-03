package com.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.dao.CompanyRepository;
import com.app.pojos.Company;

@Service
@Transactional
public class CompanyServiceImpl implements ICompanyService {
	
	@Autowired
	CompanyRepository companyRepo;

	@Override
	public void registerNewCompany(Company company) {
		
		companyRepo.save(company);

	}

	@Override
	public Company findCompanyByCompanyName(String name) {
		
		return companyRepo.findByCompanyName(name).orElseThrow(()->new RuntimeException("Company not found"));
	}
	
	@Override
	public List<Company> getByContactOf(int pNo, int contactOf) {
		final int PAGE_SIZE = 10;
		return companyRepo.findByContactOf(contactOf,PageRequest.of(pNo, PAGE_SIZE));
	}

	@Override
	public long countByContactOf(int companyOf) {
		Company company = new Company();
		company.setContactOf(companyOf);
		ExampleMatcher matcher = ExampleMatcher.matching()     
				  .withIgnorePaths("id");
		return companyRepo.count(Example.of(company,matcher));
	}

}
