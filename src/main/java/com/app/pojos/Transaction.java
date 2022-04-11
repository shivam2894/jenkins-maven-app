package com.app.pojos;

import java.time.LocalDate;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

@Table(name = "transactions")
@AttributeOverride(column = @Column(name = "transaction_id"), name = "id")
public class Transaction extends BaseEntity {

	@Column(name = "transaction_name", length = 50, nullable = false)
	private String transactionName;

	@Enumerated(value = EnumType.STRING)
	@Column(name = "transaction_type", nullable = false, length = 30)
	private TransactionType transactionType;

	@Enumerated(value = EnumType.STRING)
	@Column(name = "transaction_status", nullable = false, length = 30)
	private TransactionStatus transactionStatus;

	@Column(name = "last_modified_date", nullable = false)	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate lastModifiedDate;
	
	@Column(nullable = false, length = 50)
	private String companyName;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Override
	public String toString() {
		return "Transaction [transactionName=" + transactionName + ", transactionType=" + transactionType
				+ ", goodsStatus=" + transactionStatus + ", lastModifiedDate=" + lastModifiedDate + "]";
	}

}
