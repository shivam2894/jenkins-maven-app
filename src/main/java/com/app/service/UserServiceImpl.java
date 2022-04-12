package com.app.service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.custom_exceptions.InvalidTokenException;
import com.app.custom_exceptions.ResourceNotFoundException;
import com.app.dao.CompanyRepository;
import com.app.dao.RoleRepository;
import com.app.dao.UserRepository;
import com.app.dto.ChangePasswordDTO;
import com.app.dto.EmployeeDTO;
import com.app.dto.SignUpRequest;
import com.app.dto.UserInfoDTO;
import com.app.dto.UserResponseDTO;
import com.app.pojos.Company;
import com.app.pojos.Role;
import com.app.pojos.User;
import com.app.pojos.UserRoles;

@Service
@Transactional
public class UserServiceImpl implements IUserService {
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private RoleRepository roleRepo;
	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private CompanyRepository companyRepo;

	@Override
	public UserResponseDTO registerUser(SignUpRequest request) {
		// create User from request payload
//		{
//		    "userName": "NattuKaka",
//		    "name": "Nattu kaka",
//		    "email" : "nattu@gmail.com",
//		    "password": "2233",
//		    "roles": [
//		        "ROLE_COMPANYOWNER"
//		    ],
//		    "dob" : "1992-01-12",
//		    "company": {"companyName":"ManasInc.", "gstin": "29GGGGG1314R9Z6", "address":"kone waali gali"}
//		}

		User user = new User();
		user.setUserName(request.getUserName());
		user.setName(request.getName());
		user.setEmail(request.getEmail());
		user.setPassword(encoder.encode(request.getPassword()));// set encoded pwd

		// new code: added by Shivam
		user.setDob(LocalDate.parse(request.getDob()));

		Company persistentCompany = companyRepo.save(request.getCompany());
		user.setCompany(persistentCompany);
		// code add ended

		Set<Role> roles = request.getRoles().stream()// convert Set<String> : role names ---> Stream<String>
				// mapping roleName --> Role (using RoleRepo)
				.map(roleName -> roleRepo.findByUserRole(UserRoles.valueOf(roleName)).get())
				// collect in a Set<Role>
				.collect(Collectors.toSet());
		user.setRoles(roles);
		user.setActive(true);
		user.setResetPasswordToken(null);
		User persistentUser = userRepo.save(user);// persisted user details in db
		UserResponseDTO dto = new UserResponseDTO();
		BeanUtils.copyProperties(persistentUser, dto);// for sending resp : copied User--->User resp DTO
		System.out.println(dto);
		return dto;
	}

	@Override
	public User inviteUser(SignUpRequest request, Principal principal) {
		// create User from request payload, case: when company already exists
//		{
//		    "userName": "NattuKaka",
//	        "name": "Nattu kaka",
//		    "email" : "gud_night@gmail.com",
//		    "password": "subehLectureHai",
//		    "roles": [
//		        "ROLE_EMPLOYEE"
//		    ],
//		    "dob" : "1996-01-10",
//		}

		User user = new User();
		user.setUserName(request.getUserName());
		user.setName(request.getName());
		user.setEmail(request.getEmail());
		user.setPassword(encoder.encode(request.getPassword()));
		user.setDob(LocalDate.parse(request.getDob()));

		User ownerInfo = userRepo.findByUserName(principal.getName())
				.orElseThrow(() -> new ResourceNotFoundException("Company Owner information not found!!"));

		user.setCompany(ownerInfo.getCompany());

		Set<Role> roles = request.getRoles().stream()// convert Set<String> : role names ---> Stream<String>
				// mapping roleName --> Role (using RoleRepo)
				.map(roleName -> roleRepo.findByUserRole(UserRoles.valueOf(roleName)).get())
				// collect in a Set<Role>
				.collect(Collectors.toSet());
		user.setRoles(roles);
		user.setActive(true);
		user.setResetPasswordToken(null);
		User persistentUser = userRepo.save(user);// persisted user details in db
		
		return persistentUser;

	}

	@Override
	public User getUserByUsername(String username) {
		return userRepo.findByUserName(username).orElseThrow(() -> new ResourceNotFoundException("User not found"));
	}

	@Override
	public List<EmployeeDTO> getAllEmployees(int pNo, Principal principal) {
		User user = userRepo.findByUserName(principal.getName())
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		List<User> listUsers = userRepo.findByCompany(user.getCompany(), PageRequest.of(pNo, 10)); //
		listUsers.stream().forEach(u -> System.out.println(u.getUserName()));
		return listUsers.stream().filter(u -> u.getId() != user.getId()).map(u -> new EmployeeDTO(u))
				.collect(Collectors.toList());

	}

	@Override
	public void deleteEmployeeByName(String userName) {
		userRepo.deleteByUserName(userName);
	}

	@Override
	public User changePassword(ChangePasswordDTO obj, Principal principal) {
		User user = userRepo.findByUserName(principal.getName())
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
		if (!encoder.matches(obj.getOldPassword(), user.getPassword()))
			throw new BadCredentialsException("Old Password did not match.");
		user.setPassword(encoder.encode(obj.getNewPassword()));
		User savedUser = userRepo.save(user);
		return savedUser;
	}

	@Override
	public void changeInfo(UserInfoDTO newInfo, Principal principal) {
		LocalDate dateOfBirth = LocalDate.parse(newInfo.getDob());
		User user = userRepo.findByUserName(principal.getName())
				.orElseThrow(() -> new ResourceNotFoundException("User not found for username : "+ principal.getName()));
		user.setDob(dateOfBirth);
		user.setEmail(newInfo.getEmail());
		user.setName(newInfo.getName());
		userRepo.save(user);
	}
	
	@Override
	public User updateResetPasswordToken(String token, String email) {
		User user = userRepo.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found for email : " + email));

		user.setResetPasswordToken(token);
		return userRepo.save(user);
	}
	
	@Override
    public User getByResetPasswordToken(String token) {
        return userRepo.findByResetPasswordToken(token).orElseThrow(() -> new InvalidTokenException("Could not reset Password !! Please generate the link again"));
    }
	
	@Override
	public void updatePassword(User user, String newPassword) {
		user.setPassword(encoder.encode(newPassword));
		user.setResetPasswordToken(null);
		userRepo.save(user);
	}
	@Override
	public boolean checkUsernameExists(String username) {

		return !userRepo.findByUserName(username).isEmpty();

	}

	@Override
	public boolean checkEmailExists(String email) {

		return !userRepo.findByEmail(email).isEmpty();
	}

}
