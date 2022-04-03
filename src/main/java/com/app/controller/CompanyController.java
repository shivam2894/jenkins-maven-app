package com.app.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
