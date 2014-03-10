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

import org.junit.Test;

public class RecordServiceImpl implements RecordService {

	@Override
	public File export() {
		// TODO: test showing temp file
		return null;
	}

	@Override
	public Record saveRecord(Record record) {
		// TODO: show expected exceptions if record is null
		// TODO: show expected execptions testing erro messge - eg for error
		// codes
		// TODO: show Spring JUnit Runner transactional testing
		// TODO: show Assumes if record exists assume false für findAndUpdate
		return null;
	}

	@Override
	public void batchSave(Iterable<Record> records) {
		// TODO: show time constraints.
	}

}
