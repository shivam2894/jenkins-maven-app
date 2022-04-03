package com.app.pojos;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="categories")
@AttributeOverride(name = "id",column = @Column(name="category_id"))
public class Category extends BaseEntity{
		
	@Column(name = "category_name",length = 30)
	private String name;
	
	@ManyToMany(fetch = FetchType.EAGER) //mandatory , o.w will get MappingExc
    @JoinTable(name ="categories_users",joinColumns =@JoinColumn(name="category_id"),
    inverseJoinColumns =  @JoinColumn(name="user_id"))
    private Set<User> users = new HashSet<>();
	
	public Category(String name, User user) {
		this.name = name;
		this.users.add(user);
	}
	
	@Override
	public String toString() {
		return "Category [id="+getId()+", name=" + name + "]";
	}

}
