package com.app.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.custom_exceptions.ResourceNotFoundException;
import com.app.dao.InvoiceRepository;
import com.app.dao.TransactionRepository;
import com.app.dto.InvoiceDTO;
import com.app.dto.InvoiceProductListDTO;
import com.app.pojos.Invoice;
import com.app.pojos.Product;
import com.app.pojos.ProductList;
import com.app.pojos.Transaction;

@Service
@Transactional
public class InvoiceServiceImpl implements IInvoiceService {

	@Autowired
	InvoiceRepository invoiceRepo;
	@Autowired
	private TransactionRepository transactionRepo;

	@Override
	public InvoiceDTO getInvoiceDetails(int transactionId) {
		Transaction transaction = transactionRepo.getById(transactionId);
		Invoice invoice = invoiceRepo.findByTransaction(transaction)
				.orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));
		InvoiceDTO invoiceDTO = new InvoiceDTO();
		invoiceDTO.setOwnerCompanyName(transaction.getUser().getCompany().getCompanyName());
		invoiceDTO.setOwnerCompanyAddress(transaction.getUser().getCompany().getAddress());
		invoiceDTO.setOrderDate(invoice.getDateTime());
		invoiceDTO.setInvoiceId(invoice.getId());
		invoiceDTO.setCompanyAddress(invoice.getShippingAddress());
		invoiceDTO.setTotal(invoice.getTotalAmount());
		invoiceDTO.setOwnerEmail(transaction.getUser().getEmail());

		List<ProductList> productList = invoice.getProductList();
		List<InvoiceProductListDTO> list = new ArrayList<>();
		for (ProductList p : productList) {
			Product product = p.getProduct();
			list.add(new InvoiceProductListDTO(product.getId(), product.getProductName(), p.getProductPrice(),
					p.getProductCount()));
		}
		invoiceDTO.setProductList(list);
		return invoiceDTO;
	}

}
