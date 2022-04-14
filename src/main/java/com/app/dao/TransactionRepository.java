package com.app.dao;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.dto.ITransactionInvoiceDTO;
import com.app.pojos.Transaction;
import com.app.pojos.TransactionStatus;
import com.app.pojos.TransactionType;
import com.app.pojos.User;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
	//Pageable :  Spring Data infrastructure will recognizes this parameter automatically to apply pagination and sorting to database.
	List<Transaction> findByTransactionType(TransactionType type);

	List<Transaction> findByTransactionStatus(TransactionStatus status);

	List<Transaction> findByUser(User user, Pageable pageable);

	Optional<Transaction> findByTransactionNameAndUser(String transactionName, User user);

	@Query("select t from Transaction t where t.user = :usr and t.lastModifiedDate BETWEEN :startDate AND :endDate  ")
	List<Transaction> filterByDate(@Param("usr") User usr, @Param("startDate") LocalDate from_date,
			@Param("endDate") LocalDate to_date, Pageable pageable);

	List<Transaction> findByTransactionStatusAndUser(TransactionStatus status, User user);

	@Query("select COUNT(*) from Transaction t where t.user = :user")
	long getCount(@Param("user") User user);

	List<Transaction> findByTransactionTypeAndUserAndLastModifiedDateBetweenOrderByLastModifiedDate(
			TransactionType type, User user, LocalDate start, LocalDate end);

	@Query("select t from Transaction t where t.user = :user and t.transactionStatus in :status "
			+ "and t.lastModifiedDate BETWEEN :startDate AND :endDate")
	Slice<Transaction> findByUserWithFilters(User user, @Param("status") Collection<TransactionStatus> status,
			LocalDate startDate, LocalDate endDate, Pageable pageable);

	@Query(value = "select Concat(MONTHNAME(last_modified_date),\" \",YEAR(last_modified_date)) as month, SUM(total_amt) as amount from transactions "
			+ "natural join invoice where user_id=:id and transaction_type=:type and last_modified_date Between :start and :end group by month;", nativeQuery = true)
	List<ITransactionInvoiceDTO> getTransactionAmountWithMonth(String type, @Param("id") int user, String start,
			String end);

	@Query(value = "update transactions set transaction_status = :status where transaction_id = :id", nativeQuery = true)
	@Modifying
	int updateTransactionStatus(@Param("id") int id, @Param("status") String status);
}
