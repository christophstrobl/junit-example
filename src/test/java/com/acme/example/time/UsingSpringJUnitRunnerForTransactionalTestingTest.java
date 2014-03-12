/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.acme.example.time;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.acme.example.config.AppContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppContext.class })
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class UsingSpringJUnitRunnerForTransactionalTestingTest {

	@Autowired
	private RecordRepository repo;
	
	@Autowired
	private RecordService service;

	@BeforeTransaction
	public void assumeDataBaseIsEmpty() {
		assumeThat(repo.count(), is(0L));
	}

	@AfterTransaction
	public void assertDataBaseIsEmpty() {
		assertThat(repo.count(), is(0L));
	}

	@Test
	public void saveRecordShouldNotBeCommittedToDatabaseWhenTransactionIsRolledBack() {
		
		service.saveRecord(new Record(1L, new Date(), new Date()));
		assertThat(repo.count(), is(1L));
	}
}
