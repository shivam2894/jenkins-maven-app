package com.app.pojos;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "roles")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Role extends BaseEntity {
	
	@Enumerated(EnumType.STRING)
	@Column(length = 20,name="name")
	private UserRoles userRole;
	
	@Override
    public boolean equals(Object obj) {
        if(obj instanceof Role)
            return ((Role)obj).getUserRole().equals(userRole);
        return false;
    }

    @Override
    public int hashCode() {
        return userRole.hashCode();
    }

}
