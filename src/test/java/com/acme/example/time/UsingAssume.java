package com.acme.example.time;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeNotNull;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.acme.example.config.AppContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppContext.class })
@Transactional
public class UsingAssume {
	@Autowired
	private RecordRepository repo;
	
	@Autowired
	private RecordService service;

	@Test
	public void badTestThatTestsAllAspectsAtOnce() {
		Record recordToSave = new Record(new Date(), new Date());
		
		Record record = service.saveRecord(recordToSave);
		assertNotNull(record);
		assertNotNull(record.getId());
		
		assertThat(record.getId(), greaterThan(0L));
	}
	
	@Test
	public void saveRecordDoesNotReturnNullWhenSavingAValidRecord() {
		Record validRecord = new Record(new Date(), new Date());
		
		Record record = service.saveRecord(validRecord);
		
		assertNotNull(record);
	}

	@Test
	public void saveRecordCreatesANonNullIdWhenSavingAValidRecord() {
		Record validRecord = new Record(new Date(), new Date());
		
		Record record = service.saveRecord(validRecord);
		
		assumeNotNull(record);
		assertNotNull(record.getId());
	}

	@Test
	public void saveRecordCreatesAnIdGreaterThanZeroWhenSavingAValidRecord() {
		Record recordToSave = new Record(new Date(), new Date());
		
		Record record = service.saveRecord(recordToSave);
		assumeNotNull(record);
		assumeNotNull(record.getId());
		
		assertThat(record.getId(), greaterThan(0L));
	}
	
}
