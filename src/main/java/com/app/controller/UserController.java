package com.app.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.custom_exceptions.ResourceNotFoundException;
import com.app.dto.AuthenticationResponse;
import com.app.dto.ChangePasswordDTO;
import com.app.dto.ResetPasswordDTO;
import com.app.dto.UserInfoDTO;
import com.app.pojos.User;
import com.app.service.UserServiceImpl;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import net.bytebuddy.utility.RandomString;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://3.87.75.52:3000")
public class UserController {

	@Autowired
	UserServiceImpl userService;

	@Value("${EXP_TIMEOUT}")
	private int jwtExpirationMs;

	@Value("${SECRET_KEY}")
	private String jwtSecret;

	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private Configuration configuration;

	@Value("${spring.mail.username}")
	private String email;

	@GetMapping("/getAllUsers/page/{pNo}")
	public ResponseEntity<?> fetchEmployeePages(@PathVariable int pNo, Principal principal) {

		return new ResponseEntity<>(userService.getAllEmployees(pNo, principal), HttpStatus.OK);
	}

	@DeleteMapping("/delete/{name}")
	public ResponseEntity<?> deleteEmployeeByName(@PathVariable String name) {
		userService.deleteEmployeeByName(name);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/changePassword")
	public ResponseEntity<?> changeUserPassword(@RequestBody ChangePasswordDTO obj, Principal principal) {
		User user = userService.changePassword(obj, principal);
		String newJwt = Jwts.builder().setSubject((user.getUserName())).setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.signWith(SignatureAlgorithm.HS512, jwtSecret).claim("role", user.getRoles()).compact();
		return ResponseEntity.ok(new AuthenticationResponse(newJwt));
	}

	@PostMapping("/changeUserInfo")
	public ResponseEntity<?> changeUserInfo(@RequestBody UserInfoDTO newInfo, Principal principal) {

		userService.changeInfo(newInfo, principal);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/forgot_password/{email}")
	public ResponseEntity<?> processForgotPassword(@PathVariable String email) {

		String token = RandomString.make(30); // a random String is generated as reset password token using the
												// RandomString class from the net.bytebuddy.utility package
		User user = userService.updateResetPasswordToken(token, email); // it updates the reset password token field of
																		// the user found with the given email,
																		// otherwise throws an exception whose message
																		// will be shown up in the forgot password form.
		Map<String, String> model = new HashMap<>();
		model.put("customerName", user.getName());
		model.put("token", token);
		sendEmail(email, model);  // it generates a reset password link containing the random token as a URL parameter
		return new ResponseEntity<>(token, HttpStatus.OK);
	}

	// handler method in the User controller class to process new password sent from
	// frontend
	@PostMapping("/reset_password")
	public ResponseEntity<?> processResetPassword(@RequestBody ResetPasswordDTO resetPasswordDto) {

		User user = userService.getByResetPasswordToken(resetPasswordDto.getToken());
		userService.updatePassword(user, resetPasswordDto.getNewPassword());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	// it generates a reset password link containing the random token as a URL parameter and sends the link to user's email
	public void sendEmail(String recipientEmail, Map<String, String> model) {
		String to = recipientEmail;
		String subject = "Password Recovery NattuKaka-InventoryManagement System";
		MimeMessage message = mailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
					StandardCharsets.UTF_8.name());
			Template template = configuration.getTemplate("email.ftl");
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

	@GetMapping("/getUserInfo")
	public ResponseEntity<?> processResetPassword(Principal principal) {
		User user = userService.getUserByUsername(principal.getName());
		return new ResponseEntity<>(new UserInfoDTO(user), HttpStatus.OK);
	}
}
