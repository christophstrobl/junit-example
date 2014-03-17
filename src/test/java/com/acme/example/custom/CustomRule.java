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
package com.acme.example.custom;

import org.junit.rules.ExternalResource;

public class CustomRule extends ExternalResource {
	
	public String originalValue;
	private String key;
	private String value;

	public CustomRule(String key, String value) {
		this.key = key;
		this.value = value;
	}

	@Override
	protected void before() throws Throwable {

		originalValue = System.getProperty(key);
		System.setProperty(key, value);
	}

	@Override
	protected void after() {

		if (originalValue == null) {
			System.clearProperty(key);
		} else {
			System.setProperty(key, originalValue);
		}
	}
}
