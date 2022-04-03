package com.app.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.pojos.Company;
import com.app.pojos.Transaction;
import com.app.pojos.TransactionStatus;
import com.app.pojos.TransactionType;
import com.app.pojos.User;

public interface TransactionRepo extends JpaRepository<Transaction, Integer> {

	List<Transaction> findByTransactionType(TransactionType type);

	List<Transaction> findByTransactionStatus(TransactionStatus status);

	List<Transaction> findByUser(User user, Pageable pageable);

	Optional<Transaction> findByTransactionNameAndUser(String transactionName, User user);

	List<Transaction> findByCompanyAndUser(Company company, User user);

	@Query("select t from Transaction t where t.user = :usr and t.lastModifiedDate BETWEEN :startDate AND :endDate  ")
	List<Transaction> filterByDate(@Param("usr") User usr, @Param("startDate") LocalDate from_date,
			@Param("endDate") LocalDate to_date, Pageable pageable);
	
	List<Transaction> findByTransactionStatusAndUser(TransactionStatus status, User user);
	
	@Query("select COUNT(*) from Transaction t where t.user = :user")
	long getCount(@Param("user")User user);
}
