package com.app.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.pojos.Company;
import com.app.pojos.User;
import com.app.service.ICompanyService;
import com.app.service.IUserService;

@RestController
@RequestMapping("/api/companies")
@CrossOrigin(origins = "http://localhost:3000")
public class CompanyController {

	@Autowired
	private ICompanyService companyService;
	@Autowired
	private IUserService userService;

	@GetMapping("/page/{pNo}")
	public ResponseEntity<?> fetchCompaniesPages(@PathVariable int pNo, Principal principal) {
		User user = userService.getUserByUsername(principal.getName());
		return new ResponseEntity<>(companyService.getByContactOf(pNo, user.getCompany().getId()), HttpStatus.OK);
	}

	@GetMapping("/count")
	public ResponseEntity<?> getCompaniesCount(Principal principal) {
		User user = userService.getUserByUsername(principal.getName());
		System.out.println(user.getCompany().getId());
		return new ResponseEntity<>(companyService.countByContactOf(user.getCompany().getId()), HttpStatus.OK);
	}

	@PostMapping("/add")
	public ResponseEntity<?> addCompanyAsContact(@RequestBody Company company, Principal principal) {
		companyService.addContactInfo(company, principal);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@PatchMapping("/edit/{id}")
	public ResponseEntity<?> editCompanyDetails(@PathVariable int id, @RequestBody Company company,
			Principal principal) {
		companyService.editDetails(id, company, principal);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteCompanyDetails(@PathVariable int id) {
		companyService.deleteCompany(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PatchMapping("/editUserCompany")
	public ResponseEntity<?> editUserCompanyDetails(@RequestBody Company company, Principal principal) {
		companyService.editUserCompanyDetails(company, principal);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
