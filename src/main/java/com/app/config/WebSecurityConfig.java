package com.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.app.filters.JwtRequestFilter;

@EnableWebSecurity // Tells SC that this class contains web security config.
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private JwtRequestFilter jwtFilter;

	// for configuring authentication
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		System.out.println(1);
		auth.userDetailsService(userDetailsService);
	}

	// for configuring authorization
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		System.out.println(3);
		// specify our own config
		// enable cors n disable CSRF
		http.cors().and().csrf().disable().authorizeRequests()// authorize all requests
				.antMatchers("/api/user/invite", "/api/user/getAllUsers/**", "/api/products/valuation_by_category",
						"/api/products/countPerCat", "/api/transactions/type/**")
				.hasRole("COMPANYOWNER")
				.antMatchers("/api/user/forgot_password/**","/api/user/reset_password").permitAll()
				.antMatchers("/api/products/**", "/api/transactions/**", "/api/user/**", "/api/companies/**",
						"/api/invoice")
				.hasAnyRole("COMPANYOWNER", "EMPLOYEE")
				.antMatchers("/api/signin", "/api/signup","/usernameCheck/**","/emailCheck/**")
				.permitAll() // shouldn't be changed by anyone
				.antMatchers(HttpMethod.OPTIONS, "/**").permitAll().and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

	}

	@Bean
	public PasswordEncoder encoder() {
		System.out.println(2);
		return new BCryptPasswordEncoder();
	}

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {

		return super.authenticationManagerBean();
	}

}
