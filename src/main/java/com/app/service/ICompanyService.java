package com.app.service;

import java.util.List;

import com.app.pojos.Company;

public interface ICompanyService {
	
	//insert company details
	public void registerNewCompany(Company company);
	public long countByContactOf(int companyOf);
	public List<Company> getByContactOf(int pNo,int contactOf);
	public Company findCompanyByCompanyName(String name);

}
