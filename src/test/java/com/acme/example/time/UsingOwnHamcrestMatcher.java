package com.acme.example.time;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;

import org.hamcrest.CustomMatcher;
import org.hamcrest.Matcher;
import org.junit.Test;

public class UsingOwnHamcrestMatcher {
	private RecordConverter recordConverter = new WebInputRecordConverter();
	
	@Test
	public void testFoo() {
		Record record = recordConverter.createRecordForToday("07:30", "11:30");
		assertThat(record, startsAt("07:30"));
	}

	private Matcher<Record> startsAt(final String startTime) {
		return new CustomMatcher<Record>("Record should start at "+startTime) {
			@Override
			public boolean matches(Object o) {
				if(o instanceof Record) {
					Record record = (Record) o;
					SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
					return dateFormat.format(record.getStartTime()).equals(startTime);
				}
				return false;
			}
		};
	}
}
