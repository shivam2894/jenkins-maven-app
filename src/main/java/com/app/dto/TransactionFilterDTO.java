package com.app.dto;

import java.time.LocalDate;
import java.util.List;

import com.app.pojos.TransactionStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TransactionFilterDTO {

	private LocalDate start;
	private LocalDate end;
	private List<TransactionStatus> status;
//	public LocalDate getStart() {
//		return start;
//	}
//	public void setStart(String start) {
//		this.start = LocalDate.parse(start);
//	}
//	public LocalDate getEnd() {
//		return end;
//	}
//	public void setEnd(String end) {
//		this.end = LocalDate.parse(end);
//	}
//	public List<TransactionStatus> getStatus() {
//		return status;
//	}
//	public void setStatus(List<String> status) {
//		this.status = status.stream().map(s->TransactionStatus.valueOf(s)).collect(Collectors.toList());
//	}
}
