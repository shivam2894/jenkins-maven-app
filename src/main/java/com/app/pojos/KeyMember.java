package com.app.pojos;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="key_members")
@Getter
@Setter
public class KeyMember extends BaseEntity {
	
	@Column(length = 100)
	private String name;
	
	@Column(length = 100)
	private String position;
	
	@Column(length = 30)
	private String email;
	
	@Column(name="phone_number",length = 15)
	private String phoneNumber;
	
	@ManyToOne
	@JoinColumn(name="company_id",nullable=false)
	private Company company;

}
