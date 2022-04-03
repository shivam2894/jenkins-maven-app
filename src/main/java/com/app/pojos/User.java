package com.app.pojos;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="users")
@Getter
@Setter
public class User extends BaseEntity {
	
	@Column(length=30,unique=true)
	private String userName;
	
	@Column(length=30)
	private String name;
	
	@Column(length=30,unique=true)
	private String email;
	
	@Column(length = 500)
	private String password;
	
	private boolean active;
	
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private LocalDate dob;
	
	@ManyToOne
	@JoinColumn(name="company_id",nullable=false)
	private Company company;
	
	@ManyToMany
	@JoinTable(name = "user_roles", 
	joinColumns = @JoinColumn(name = "user_id"), 
	inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();
}
