package com.app.pojos;



import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="companies")
@AttributeOverride(column = @Column(name = "company_id"), name = "id")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Company extends BaseEntity {
	
	@Column(name="name")
	private String companyName;
	
	@Column(length = 15)
	private String gstin;
	
	@Column(length = 100)
	private String address;
	
	@Column(name="contact_of")
	private int contactOf; 
}
