package com.acme.example.time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WebInputRecordConverter implements RecordConverter {
	private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
	
	@Override
	public Record createRecordForToday(String startTime, String endTime) {
		try {
			Date startDate = dateFormat.parse(startTime);
			Date endDate = dateFormat.parse(endTime);
			return new Record(startDate, endDate);
		} catch (ParseException e) {
			throw new RuntimeException("Could not parse date", e);
		}
	}
}
