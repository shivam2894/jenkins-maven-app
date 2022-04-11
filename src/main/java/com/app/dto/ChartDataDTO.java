package com.app.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ChartDataDTO {
	
	private List<Integer> data;
	private List<String> labels;
	
	public ChartDataDTO(){
		data= new ArrayList<Integer>();
		labels = new ArrayList<String>();
	}
}
