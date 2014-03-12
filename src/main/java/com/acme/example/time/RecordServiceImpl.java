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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
class RecordServiceImpl implements RecordService {

	private final RecordRepository recordRepository;

	@Autowired
	RecordServiceImpl(RecordRepository repository) {
		this.recordRepository = repository;
	}

	@Override
	public File export(String path, String filename) {

		List<Record> records = this.recordRepository.findAll();
		return writeRecordsToFile(records,
				createFileAndPotentiallyModifyFilename(path, filename));
	}

	private File createFileAndPotentiallyModifyFilename(String path,
			String filename) {
		String tmpFileName = FilenameUtils.concat(path, filename);
		File file = null;
		while ((file = new File(tmpFileName)).exists()) {
			tmpFileName = FilenameUtils.concat(
					path,
					FilenameUtils.getBaseName(filename) + "-"
							+ UUID.randomUUID().toString()
							+ FilenameUtils.getExtension(filename));
		}
		return file;
	}

	private File writeRecordsToFile(List<Record> records, File file) {
		try {
			FileUtils.writeLines(file, records);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return file;
	}

	@Override
	public Record saveRecord(Record record) {
		// TODO: show expected exceptions if record is null
		// TODO: show expected execptions testing erro messge - eg for error
		// codes
		return this.recordRepository.save(record);
		// TODO: show Assumes if record exists assume false für findAndUpdate
	}

	@Override
	public void batchSave(Iterable<Record> records) {
		// TODO: show time constraints.
	}

}
