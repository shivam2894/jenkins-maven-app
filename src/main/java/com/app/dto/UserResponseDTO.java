package com.app.dto;

import java.util.HashSet;
import java.util.Set;

import com.app.pojos.Company;
import com.app.pojos.Role;

import lombok.Data;

@Data
public class UserResponseDTO {

	private Integer id;
	
	private String userName;
	
	private String name;
	
	private String email;
		
	private boolean active;
	
	private Company company;
	
	private String resetPasswordToken;
		
	private Set<Role> roles = new HashSet<>();

	
}
