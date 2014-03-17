package com.acme.example.time;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.core.Is.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class UsingJunitParameterized {
	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {{"07:00", "07:30", 30*60*1000L}, {"07:00", "09:00", 2*60*60*1000L}});
	}
	
	@Parameter(value=0) public String startTime;
	@Parameter(value=1) public String endTime;
	@Parameter(value=2) public long expectedDuration;
	
	@Test
	public void durationOfRecordIsConsistentWithStartTimeAndEndTime() {
		WebInputRecordConverter converter = new WebInputRecordConverter();
		
		Record record = converter.createRecordForToday(startTime, endTime);
		
		assertThat(record.getDuration(), is(expectedDuration));
	}
}
