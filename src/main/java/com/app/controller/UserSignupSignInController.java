package com.app.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.custom_exceptions.ResourceNotFoundException;
import com.app.dto.AuthenticationRequest;
import com.app.dto.AuthenticationResponse;
import com.app.dto.SignUpRequest;
import com.app.dto.UserResponseDTO;
import com.app.jwt_utils.JwtUtils;
import com.app.pojos.User;
import com.app.service.IUserService;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://3.87.75.52:3000")
public class UserSignupSignInController {

	// auto wire Authentication Manager for user authentication , created in
	// Security Config class
	// (currently based upon user details service)
	@Autowired
	private AuthenticationManager authManager;
	// auto wire JwtUtils for sending signed JWT back to the clnt
	@Autowired
	private JwtUtils jwtUtils;
	@Autowired
	private IUserService userService;

	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private Configuration configuration;

	@Value("${spring.mail.username}")
	private String email;

	//end point for user registration
	@PostMapping("/signup")
	public ResponseEntity<?> userRegistration(@RequestBody SignUpRequest request) {
		System.out.println("in user reg " + request);
		return ResponseEntity.ok(userService.registerUser(request));
	}

	//end point for user authentication
	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@RequestBody AuthenticationRequest request) {
		System.out.println("in auth " + request);
		try {
			// Tries to authenticate the passed Authentication object, returning a fully
			// populated Authentication object (including granted authorities)if successful.
			Authentication authenticate = authManager.authenticate
			// An o.s.s.c.Authentication i/f implementation used for simple presentation of
			// a username and password.
			// Actual dao based authentication takes place here internally(first username
			// for authentication

			// n then pwd n then authorities gets validated)
			(new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPassword()));
			// => successful authentication : create JWT n send it to the client in the
			// response.
			System.out.println("auth success " + authenticate);
			return ResponseEntity.ok(new AuthenticationResponse(jwtUtils.generateJwtToken(authenticate)));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("User authentication Failed", e);
		}
	}

	//end point for inviting employee
	@PostMapping("/invite")
	public ResponseEntity<?> employeeInvite(@RequestBody SignUpRequest request, Principal principal) {
		System.out.println("in employee invite " + request);
		User persistentUser = userService.inviteUser(request, principal);
		sendEmail(persistentUser, request.getPassword());
		UserResponseDTO dto = new UserResponseDTO();
		BeanUtils.copyProperties(persistentUser, dto);// for sending resp : copied User--->User resp DTO
		return ResponseEntity.ok(dto);
	}
	
	//method to send mail to the invited employee with his credentials
	public void sendEmail(User user, String textPassword) {
		Map<String, String> model = new HashMap<>();
		model.put("customerName", user.getName());
		model.put("companyName", user.getCompany().getCompanyName());
		model.put("textPassword", textPassword);
		model.put("userName", user.getUserName());
		String to = user.getEmail();
		String subject = "Employee Invite NattuKaka-InventoryManagement System";
		MimeMessage message = mailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
					StandardCharsets.UTF_8.name());
			Template template = configuration.getTemplate("invite.ftl");
			String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
			helper.setTo(to);
			helper.setFrom(email);
			helper.setSubject(subject);
			helper.setText(html, true);
			mailSender.send(message);
		} catch (MessagingException | IOException | TemplateException e) {
			throw new ResourceNotFoundException(e.getMessage());
		}
	}
	
	@GetMapping("/usernameCheck/{username}")
    public ResponseEntity<?> checkUsername(@PathVariable String username) {    
        boolean exists = userService.checkUsernameExists(username);    
        return ResponseEntity.ok(exists);    
    }
    
    @GetMapping("/emailCheck/{email}")
    public ResponseEntity<?> checkEmail(@PathVariable String email) {    
        boolean exists = userService.checkEmailExists(email);    
        return ResponseEntity.ok(exists);    
    }
}
