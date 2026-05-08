/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.xalan.xslt.util;

/**
 * This class definition, specifies data structure for Xalan Java 
 * XSLT 3.0 regex processing. An object instance of this class 
 * stores a pair of integer values that specify word match boundary 
 * within another context string.
 * 
 * A list of objects of this class, is used to implement the
 * relevant XSL specification features that use regex.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage general
 */
public class RegexMatchInfo {

	private int startIdx;

	private int endIdx;

	/**
	 * Class constructor.
	 */
	public RegexMatchInfo() {
		// no op
	}

	public int getStartIdx() {
		return startIdx;
	}

	public void setStartIdx(int startIdx) {
		this.startIdx = startIdx;
	}

	public int getEndIdx() {
		return endIdx;
	}

	public void setEndIdx(int endIdx) {
		this.endIdx = endIdx;
	}
}
