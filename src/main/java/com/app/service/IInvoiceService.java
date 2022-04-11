package com.app.service;

import com.app.dto.InvoiceDTO;

public interface IInvoiceService {

	InvoiceDTO getInvoiceDetails(int transactionId);

}
