package com.app.service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.dao.CompanyRepository;
import com.app.dao.RoleRepository;
import com.app.dao.UserRepository;
import com.app.dto.SignUpRequest;
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
		User persistentUser = userRepo.save(user);// persisted user details in db
		UserResponseDTO dto = new UserResponseDTO();
		BeanUtils.copyProperties(persistentUser, dto);// for sending resp : copied User--->User resp DTO
		System.out.println(dto);
		return dto;
	}

	@Override
	public UserResponseDTO inviteUser(SignUpRequest request, Principal principal) {
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
				.orElseThrow(() -> new RuntimeException("Company Owner information not found!!"));

		user.setCompany(ownerInfo.getCompany());

		Set<Role> roles = request.getRoles().stream()// convert Set<String> : role names ---> Stream<String>
				// mapping roleName --> Role (using RoleRepo)
				.map(roleName -> roleRepo.findByUserRole(UserRoles.valueOf(roleName)).get())
				// collect in a Set<Role>
				.collect(Collectors.toSet());
		user.setRoles(roles);
		user.setActive(true);
		User persistentUser = userRepo.save(user);// persisted user details in db
		UserResponseDTO dto = new UserResponseDTO();
		BeanUtils.copyProperties(persistentUser, dto);// for sending resp : copied User--->User resp DTO
		System.out.println(dto);
		return dto;

	}

	@Override
	public User getUserByUsername(String username) {
		return userRepo.findByUserName(username).orElseThrow(() -> new RuntimeException("User not found"));
	}

}
