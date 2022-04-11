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
@CrossOrigin(origins = "http://localhost:3000")
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

	@DeleteMapping("/delete/{nm}")
	public ResponseEntity<?> deleteEmployeeById(@PathVariable String nm) {
		userService.deleteEmployeeByName(nm);
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

		String token = RandomString.make(30);
		User user = userService.updateResetPasswordToken(token, email);
		Map<String, String> model = new HashMap<>();
		model.put("customerName", user.getName());
		model.put("token", token);
		sendEmail(email,model);
		return new ResponseEntity<>(token, HttpStatus.OK);
	}

	@PostMapping("/reset_password")
	public ResponseEntity<?> processResetPassword(@RequestBody ResetPasswordDTO resetPasswordDto) {

		User user = userService.getByResetPasswordToken(resetPasswordDto.getToken());
		userService.updatePassword(user, resetPasswordDto.getNewPassword());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	public void sendEmail(String recipientEmail,Map<String, String> model) {
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
