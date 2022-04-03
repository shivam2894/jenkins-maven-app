package com.app.dto;


import java.util.Set;

import com.app.pojos.Company;

import lombok.Data;

@Data
public class SignUpRequest {
	
	private String userName,name,email, password;
	private String dob;
	private Company company;	
	private Set<String> roles;
	private int ownerId;
}
