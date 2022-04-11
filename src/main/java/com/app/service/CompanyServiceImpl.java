package com.app.service;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.custom_exceptions.ResourceNotFoundException;
import com.app.dao.CompanyRepository;
import com.app.dao.UserRepository;
import com.app.pojos.Company;
import com.app.pojos.User;

@Service
@Transactional
public class CompanyServiceImpl implements ICompanyService {

	@Autowired
	CompanyRepository companyRepo;

	@Autowired
	private UserRepository userRepo;

	@Override
	public void registerNewCompany(Company company) {

		companyRepo.save(company);

	}

	@Override
	public Company findCompanyByCompanyName(String name) {

		return companyRepo.findByCompanyName(name).orElseThrow(() -> new ResourceNotFoundException("Company not found"));
	}

	@Override
	public List<Company> getByContactOf(int pNo, int contactOf) {
		final int PAGE_SIZE = 10;
		return companyRepo.findByContactOf(contactOf, PageRequest.of(pNo, PAGE_SIZE));
	}

	@Override
	public long countByContactOf(int companyOf) {
		Company company = new Company();
		company.setContactOf(companyOf);
		ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths("id");
		return companyRepo.count(Example.of(company, matcher));
	}

	@Override
	public void addContactInfo(Company company, Principal principal) {

		User user = userRepo.findByUserName(principal.getName())
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
		int contactOf = user.getCompany().getId();
		company.setContactOf(contactOf);
		companyRepo.save(company);
	}

	@Override
	public void editDetails(int id, Company company, Principal principal) {
		Company databaseComp = companyRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Company not found"));
		company.setContactOf(databaseComp.getContactOf());
		companyRepo.save(company);
	}

	@Override
	public void deleteCompany(int id) {
		companyRepo.deleteById(id);

	}

	@Override
	public void editUserCompanyDetails(Company company, Principal principal) {
		User user = userRepo.findByUserName(principal.getName())
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
		Company userComp = companyRepo.getById(user.getCompany().getId());
		userComp.setCompanyName(company.getCompanyName());
		userComp.setAddress(company.getAddress());
		userComp.setGstin(company.getGstin());
		companyRepo.save(userComp);
	}
}
