package com.app.service;

import java.security.Principal;

import com.app.dto.SignUpRequest;
import com.app.dto.UserResponseDTO;
import com.app.pojos.User;

//Nothing to do with spring security : it's job currently is user registration
public interface IUserService {
	UserResponseDTO registerUser(SignUpRequest request);
	
	UserResponseDTO inviteUser(SignUpRequest request,Principal principal);
	
	User getUserByUsername(String username);
}
