package com.app.service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.app.custom_exceptions.ResourceNotFoundException;
import com.app.dao.InvoiceRepository;
import com.app.dao.ProductRepository;
import com.app.dao.TransactionRepository;
import com.app.dao.UserRepository;
import com.app.dto.ITransactionInvoiceDTO;
import com.app.dto.MonthChartDataDTO;
import com.app.dto.ProductListDTO;
import com.app.dto.TransactionDTO;
import com.app.dto.TransactionFilterDTO;
import com.app.dto.TransactionInvoiceDTO;
import com.app.dto.TransactionResponseDTO;
import com.app.pojos.Invoice;
import com.app.pojos.Product;
import com.app.pojos.ProductList;
import com.app.pojos.Role;
import com.app.pojos.Transaction;
import com.app.pojos.TransactionStatus;
import com.app.pojos.TransactionType;
import com.app.pojos.User;
import com.app.pojos.UserRoles;

@Service
@Transactional
public class TransactionServiceImpl implements ITransactionService {
	
	//tx boundary => success => session flushed => auto dirty checking =>
    //returns detached entity
	
	@Autowired
	private TransactionRepository transactionRepo;
	@Autowired
	private UserRepository userRepo;
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

	// TransactionResponseDTO have info about the page we are sending, i.e data, page no , no of elements on current page
	// and does next page exist
	
	// TransactionFilterDTO contains information of all the filters requested
	
	@Override
	public TransactionResponseDTO getTransactionsByUser(int pNo, String userName, TransactionFilterDTO filters) {
		final int PAGE_SIZE = 10;
		User user = userRepo.findByUserName(userName)
				.orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
		if (!user.getRoles().contains(new Role(UserRoles.ROLE_COMPANYOWNER))) {
			user = userRepo.findOwner(user.getCompany().getId())
					.orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
		}
		TransactionFilterDTO filtersFound = new TransactionFilterDTO(LocalDate.of(1800, 1, 1), LocalDate.now(),
				new ArrayList<>(Arrays.asList(TransactionStatus.DISPATCHED, TransactionStatus.NOTRECEIVED,
						TransactionStatus.RECEIVED, TransactionStatus.NOTDISPATCHED)));
//		System.out.println(filters);
		if (filters.getStart() != null) {
			filtersFound.setStart(filters.getStart());
		}
		if (filters.getEnd() != null)
			filtersFound.setEnd(filters.getEnd());
		if (filters.getStatus() != null) {
			filtersFound.setStatus(filters.getStatus());
		}
		System.out.println(filtersFound);
		Slice<Transaction> slice = transactionRepo.findByUserWithFilters(user, filtersFound.getStatus(),
				filtersFound.getStart(), filtersFound.getEnd(),
				PageRequest.of(pNo, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "lastModifiedDate")));
		List<TransactionDTO> tranList = slice.getContent().stream().map(e -> new TransactionDTO(e))
				.collect(Collectors.toList());
		int pageNo = slice.getNumber();
		int numberOfElements = slice.getNumberOfElements();
		boolean nextExists = slice.hasNext();
		return new TransactionResponseDTO(tranList, pageNo, numberOfElements, nextExists);
	}

	@Override
	public TransactionDTO getTransactionByName(String name, Principal principal) {
		User user = userRepo.findByUserName(principal.getName())
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
		Transaction t = transactionRepo.findByTransactionNameAndUser(name, user)
				.orElseThrow(() -> new ResourceNotFoundException("No transactions found for given transaction name"));
		return new TransactionDTO(t);
	}

	@Override
	public TransactionDTO getTransactionByTransactionId(int id) {
		Transaction t = transactionRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No transactions found"));
		return new TransactionDTO(t);
	}
	
	@Override
	public List<TransactionDTO> getByTransactionStatus(String status, Principal principal) {
		User user = userRepo.findByUserName(principal.getName())
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
		if (!user.getRoles().contains(new Role(UserRoles.ROLE_COMPANYOWNER))) {
			user = userRepo.findOwner(user.getCompany().getId())
					.orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
		}
		return transactionRepo.findByTransactionStatusAndUser(TransactionStatus.valueOf(status.toUpperCase()), user)
				.stream().map(e -> new TransactionDTO(e)).collect(Collectors.toList());

	}

	@Override
	public void createTransactionAndInvoice(TransactionInvoiceDTO tranDTO, Principal principal) {
		User user = userRepo.findByUserName(principal.getName())
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
		if (!user.getRoles().contains(new Role(UserRoles.ROLE_COMPANYOWNER))) {
			user = userRepo.findOwner(user.getCompany().getId())
					.orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
		}

		// creating a transaction
		Transaction trans = new Transaction();
		trans.setTransactionName(tranDTO.getTransactionName());
		trans.setTransactionStatus(TransactionStatus.valueOf(tranDTO.getTransactionStatus().toUpperCase()));
		trans.setTransactionType(TransactionType.valueOf(tranDTO.getTransactionType().toUpperCase()));
		trans.setCompanyName(tranDTO.getCompanyName());
		trans.setUser(user);
		trans.setLastModifiedDate(LocalDate.now());

		Transaction savedTrans = transactionRepo.save(trans);

		// creating invoice
		List<ProductList> productList = tranDTO.getProductList().stream().map((ProductListDTO p) -> {
			Product product = productRepo.getById(p.getProductId());
			if (tranDTO.getTransactionType().equals("PURCHASE"))
				product.setStocks(product.getStocks() + p.getCount());
			else
				product.setStocks(product.getStocks() - p.getCount());
			productRepo.save(product);
			return new ProductList(p.getCount(), product, product.getPrice());
		}).collect(Collectors.toList());

		Double totalAmount = productList.stream().map(p -> p.getProduct().getPrice() * p.getProductCount())
				.mapToDouble(Double::intValue).sum();
		Invoice invoice = new Invoice(totalAmount, tranDTO.getShippingAddress(), LocalDate.now(), savedTrans,
				productList);

		invoiceRepo.save(invoice);
	}

	@Override
	public long countOfTransactions(Principal principal) {
		User user = userRepo.findByUserName(principal.getName())
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
		if (!user.getRoles().contains(new Role(UserRoles.ROLE_COMPANYOWNER))) {
			user = userRepo.findOwner(user.getCompany().getId())
					.orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
		}
		return transactionRepo.getCount(user);
	}
	
	// MonthChartDataDTO contains data and labels which are compatible with react-chart-js input

	@Override
	public MonthChartDataDTO transactionValueByMonthAndType(TransactionType transactionType, Principal principal) {
		User user = userRepo.findByUserName(principal.getName())
				.orElseThrow(() -> new RuntimeException("User not found"));
		if (!user.getRoles().contains(new Role(UserRoles.ROLE_COMPANYOWNER))) {
			user = userRepo.findOwner(user.getCompany().getId())
					.orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
		}
		MonthChartDataDTO out = new MonthChartDataDTO();
		LocalDate now = LocalDate.now();
		LocalDate end = LocalDate.of(now.getYear(), now.getMonthValue(), 1).minusDays(1);
		LocalDate start = LocalDate.of(end.getYear() - 1, end.getMonth(), 1);
		List<ITransactionInvoiceDTO> listTransaction = transactionRepo
				.getTransactionAmountWithMonth(transactionType.name(), user.getId(), start.toString(), end.toString());
		for (int i = 0; i < 13; i++) {
			out.getLabels().add(start.plusMonths(i).getMonth() + " " + start.plusMonths(i).getYear());
		}
		System.out.println(start);
		System.out.println(end);
		listTransaction.forEach(e -> {
			out.getData().set(out.getLabels().indexOf(e.getMonth().toUpperCase()), e.getAmount());
		});
		return out;
	}

	@Override
	public void deleteTransactionById(int id) {
		invoiceRepo.deleteById(id);
		transactionRepo.deleteById(id);
	}

	@Override
	public void updateTransactionStatus(int id, String status) {
		transactionRepo.updateTransactionStatus(id, status);
	}

}
