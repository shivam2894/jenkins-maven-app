package com.app.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class MonthChartDataDTO {
	
	private List<Double> data;
	private List<String> labels;
	
	public MonthChartDataDTO(){
		data= new ArrayList<Double>();
		labels = new ArrayList<String>();
		for(int i=0;i<13;i++) {
			data.add(0.0);
		}
	}
}
