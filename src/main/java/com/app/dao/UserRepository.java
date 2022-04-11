package com.app.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.pojos.Company;
import com.app.pojos.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	@Query("select distinct u from User u left outer join fetch u.roles where u.userName=:nm")
	Optional<User> findByUserName(@Param("nm") String userName);

	@Query("select  u from User u where u.userName=:nm")
	Optional<User> fetchUserDetails(@Param("nm") String userName);

	List<User> findByCompany(Company company, Pageable pageable);

	long deleteByUserName(String userName);

	Optional<User> findByResetPasswordToken(String token);
	
	@Query(value="select u.* from users u join companies c on c.company_id = u.company_id "
			+ "join user_roles r on u.id=r.user_id where r.role_id=1 and u.company_id=:compId",nativeQuery = true)
	Optional<User> findOwner(int compId);
	
	Optional<User> findByEmail(String email);
}
