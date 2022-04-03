package com.app.service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.app.dao.CompanyRepository;
import com.app.dao.InvoiceRepository;
import com.app.dao.ProductRepository;
import com.app.dao.TransactionRepo;
import com.app.dao.UserRepository;
import com.app.dto.ProductListDTO;
import com.app.dto.TransactionDTO;
import com.app.dto.TransactionInvoiceDTO;
import com.app.pojos.Company;
import com.app.pojos.Invoice;
import com.app.pojos.Product;
import com.app.pojos.ProductList;
import com.app.pojos.Transaction;
import com.app.pojos.TransactionStatus;
import com.app.pojos.TransactionType;
import com.app.pojos.User;

@Service
@Transactional
public class TransactionServiceImpl implements ITransactionService {

	@Autowired
	TransactionRepo transactionRepo;
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private CompanyRepository companyRepo;
	@Autowired
	private ProductRepository productRepo;
	@Autowired
	private InvoiceRepository invoiceRepo;

	@Override
	public List<Transaction> transactionsByTransactionType(String transactionType) {
		return transactionRepo.findByTransactionType(TransactionType.valueOf(transactionType.toUpperCase()));
	}

	@Override
	public List<Transaction> transactionsByTransactionStatus(String transactionStatus) {
		return transactionRepo.findByTransactionStatus(TransactionStatus.valueOf(transactionStatus.toUpperCase()));
	}

	@Override
	public String saveTransaction(Transaction transaction) {
		transactionRepo.save(transaction);
		return "The Transaction is Successfully Saved ";

	}

	@Override
	public List<TransactionDTO> getTransactionsByUser(int pNo, String userName) {
		final int PAGE_SIZE = 10;
		User user = userRepo.findByUserName(userName).orElseThrow(() -> new RuntimeException("User Not Found"));
		return transactionRepo
				.findByUser(user, PageRequest.of(pNo, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "lastModifiedDate")))
				.stream().map(e -> new TransactionDTO(e)).collect(Collectors.toList());
	}

	@Override
	public TransactionDTO getTransactionByName(String name, Principal principal) {
		User user = userRepo.findByUserName(principal.getName())
				.orElseThrow(() -> new RuntimeException("User not found"));
		Transaction t = transactionRepo.findByTransactionNameAndUser(name, user)
				.orElseThrow(() -> new RuntimeException("No transactions found for given transaction name"));
		return new TransactionDTO(t);
	}

	@Override
	public TransactionDTO getTransactionByTransactionId(int id) {
		Transaction t = transactionRepo.findById(id).orElseThrow(() -> new RuntimeException("No transactions found"));
		return new TransactionDTO(t);
	}

	@Override
	public List<TransactionDTO> getTransactionByCompName(String compName, Principal principal) {
		User user = userRepo.findByUserName(principal.getName())
				.orElseThrow(() -> new RuntimeException("User not found"));
		Company company = companyRepo.findByCompanyName(compName)
				.orElseThrow(() -> new RuntimeException("Company not found"));
		return transactionRepo.findByCompanyAndUser(company, user).stream().map(e -> new TransactionDTO(e))
				.collect(Collectors.toList());

	}

	@Override
	public List<TransactionDTO> filterTranByDate(LocalDate from_date, LocalDate to_date, int pNo, String userName) {
		final int PAGE_SIZE = 10;
		User user = userRepo.findByUserName(userName).orElseThrow(() -> new RuntimeException("User not found"));

		return transactionRepo
				.filterByDate(user, from_date, to_date,
						PageRequest.of(pNo, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "lastModifiedDate")))
				.stream().map(e -> new TransactionDTO(e)).collect(Collectors.toList());
	}

	@Override
	public List<TransactionDTO> getByTransactionStatus(String status, Principal principal) {
		User user = userRepo.findByUserName(principal.getName())
				.orElseThrow(() -> new RuntimeException("User not found"));
		return transactionRepo.findByTransactionStatusAndUser(TransactionStatus.valueOf(status.toUpperCase()), user)
				.stream().map(e -> new TransactionDTO(e)).collect(Collectors.toList());

	}

	@Override
	public Invoice createTransactionAndInvoice(TransactionInvoiceDTO tranDTO, Principal principal) {
		User user = userRepo.findByUserName(principal.getName())
				.orElseThrow(() -> new RuntimeException("User not found"));
		Optional<Company> company = companyRepo.findByCompanyName(tranDTO.getCompanyName());

		Transaction trans = new Transaction();
		trans.setTransactionName(tranDTO.getTransactionName());
		trans.setTransactionStatus(TransactionStatus.valueOf(tranDTO.getTransactionStatus().toUpperCase()));
		trans.setTransactionType(TransactionType.valueOf(tranDTO.getTransactionType().toUpperCase()));
		if (company.isPresent())
			trans.setCompany(company.get());
		else
			trans.setCompany(new Company(tranDTO.getCompanyName(), null, null, user.getCompany().getId()));
		trans.setUser(user);
		trans.setLastModifiedDate(LocalDate.now());

		Transaction savedTrans = transactionRepo.save(trans);

		List<ProductList> productList = tranDTO.getProductList().stream().map((ProductListDTO p) -> {
			Product product = productRepo.getById(p.getProductId());
			return new ProductList(p.getCount(), product);
		}).collect(Collectors.toList());
		
		Double totalAmount= productList.stream().map(p-> p.getProduct().getPrice()*p.getProductCount()).mapToDouble(Double::intValue).sum();
		Invoice invoice = new Invoice(totalAmount,tranDTO.getShippingAddress(),LocalDate.now(),savedTrans,productList);
		
		Invoice savedInvoice = invoiceRepo.save(invoice);
				
		return savedInvoice;
	}
	
	@Override
	public long countOfTransactions(Principal principal) {
		User user = userRepo.findByUserName(principal.getName())
				.orElseThrow(() -> new RuntimeException("User not found")); 
		return transactionRepo.getCount(user);
	}

}
