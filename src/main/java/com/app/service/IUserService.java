package com.app.service;

import java.security.Principal;
import java.util.List;

import com.app.dto.ChangePasswordDTO;
import com.app.dto.EmployeeDTO;
import com.app.dto.SignUpRequest;
import com.app.dto.UserInfoDTO;
import com.app.dto.UserResponseDTO;
import com.app.pojos.User;

//Nothing to do with spring security : it's job currently is user registration
public interface IUserService {
	UserResponseDTO registerUser(SignUpRequest request);

	User inviteUser(SignUpRequest request, Principal principal);

	User getUserByUsername(String username);

	List<EmployeeDTO> getAllEmployees(int pNo, Principal principal);

	void deleteEmployeeByName(String userName);

	User changePassword(ChangePasswordDTO obj, Principal principal);

	void changeInfo(UserInfoDTO newInfo, Principal principal);

	User updateResetPasswordToken(String token, String email);

	User getByResetPasswordToken(String token);

	void updatePassword(User user, String newPassword);

	boolean checkUsernameExists(String username);

	boolean checkEmailExists(String email);
}
