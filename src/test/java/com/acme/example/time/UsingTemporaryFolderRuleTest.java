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

import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

public class UsingTemporaryFolderRuleTest {

	private static final String FILE_NAME = "RecordExport.txt";
	private static final Record RECORD = new Record(1L, new Date(), new Date());

	@Rule
	public TemporaryFolder tmpFoler = new TemporaryFolder();

	private RecordRepository recordRepositoryMock;
	private RecordServiceImpl recordService;

	@Before
	public void setUp() {
		this.recordRepositoryMock = Mockito.mock(RecordRepository.class);
		this.recordService = new RecordServiceImpl(recordRepositoryMock);
	}

	@Test
	public void exportWritesLinesCorrectlyToFile() throws IOException {

		when(this.recordRepositoryMock.findAll()).thenReturn(
				Arrays.asList(RECORD));

		String folderPath = tmpFoler.getRoot().getAbsolutePath();

		File f = recordService.export(folderPath, FILE_NAME);
		assertThat(FileUtils.readLines(f), hasItems(RECORD.toString()));
	}
}
