package com.acme.example.time;

public interface RecordConverter {
	Record createRecordForToday(String startTime, String endTime);
}
