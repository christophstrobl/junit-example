package com.acme.example.time;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.ikeran.test.BeefyRunner;
import org.ikeran.test.Sequential;
import org.ikeran.test.Values;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(BeefyRunner.class)
public class UsingBeefyRunnerParameterized {
	@Test
	@Sequential
	public void durationOfRecordIsConsistentWithStartTimeAndEndTime(
			@Values(strings={"07:00", "07:00"}) String startTime,
			@Values(strings={"07:30", "09:00"}) String endTime,
			@Values(numbers={30*60*1000L, 2*60*60*1000L}) long expectedDuration) {
		WebInputRecordConverter converter = new WebInputRecordConverter();
		
		Record record = converter.createRecordForToday(startTime, endTime);
		
		assertThat(record.getDuration(), is(expectedDuration));
	}
	
	@Test
	public void durationOfRecordMultipleCombinations(
			@Values(strings={"07:00", "07:30", "08:00"}) String startTime,
			@Values(strings={"08:30", "09:00", "10:00"}) String endTime) throws ParseException {
		WebInputRecordConverter converter = new WebInputRecordConverter();
		
		Record record = converter.createRecordForToday(startTime, endTime);

		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		long expectedDuration = sdf.parse(endTime).getTime()-sdf.parse(startTime).getTime();
		assertThat(record.getDuration(), is(expectedDuration));
	}
}
