package com.app.dto;

import java.time.LocalDate;

import com.app.pojos.User;

import lombok.Data;

@Data
public class EmployeeDTO {
	
	private String userName;
	private String name;
	private String email;
	private LocalDate dob;
	
	public EmployeeDTO(User user) {
		
		this.userName=user.getUserName();
		this.name=user.getName();
		this.email=user.getEmail();
		this.dob=user.getDob();		
	}

}
