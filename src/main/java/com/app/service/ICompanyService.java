package com.app.service;

import java.security.Principal;
import java.util.List;

import com.app.pojos.Company;

public interface ICompanyService {
	
	//insert company details
	void registerNewCompany(Company company);
	long countByContactOf(int companyOf);
	List<Company> getByContactOf(int pNo,int contactOf);
	Company findCompanyByCompanyName(String name);
	
	void addContactInfo(Company company,Principal principal);
	void editDetails(int id,Company company,Principal principal);
	void deleteCompany(int id);
	void editUserCompanyDetails(Company company, Principal principal);
}
