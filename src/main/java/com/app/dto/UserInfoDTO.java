package com.app.dto;

import com.app.pojos.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDTO {

	String name;
	String email;
	String dob;
	
	String companyName;
	String gstin;
	String address;
	
	public UserInfoDTO(User user)
	{
		this.name=user.getName();
		this.email=user.getEmail();
		this.dob=user.getDob().toString();
		this.companyName=user.getCompany().getCompanyName();
		this.gstin=user.getCompany().getGstin();
		this.address=user.getCompany().getAddress();
	}
}
