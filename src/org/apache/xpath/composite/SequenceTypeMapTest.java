/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.xpath.composite;

/**
 * An object of this class stores, an XSLT transformation run-time
 * information for an occurrence of a XPath 3.1 sequence type 
 * MapTest.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class SequenceTypeMapTest {

    private boolean fIsAnyMapTest;

    private SequenceTypeData keySequenceTypeData;
    
    private SequenceTypeData valueSequenceTypeData;

	public boolean isfIsAnyMapTest() {
		return fIsAnyMapTest;
	}

	public boolean getIsAnyMapTest() {
		return fIsAnyMapTest;
	}
	
	public void setIsAnyMapTest(boolean isAnyMapTest) {
		this.fIsAnyMapTest = isAnyMapTest;
	}

	public SequenceTypeData getKeySequenceTypeData() {
		return keySequenceTypeData;
	}

	public void setKeySequenceTypeData(SequenceTypeData keySequenceTypeData) {
		this.keySequenceTypeData = keySequenceTypeData;
	}

	public SequenceTypeData getValueSequenceTypeData() {
		return valueSequenceTypeData;
	}

	public void setValueSequenceTypeData(SequenceTypeData valueSequenceTypeData) {
		this.valueSequenceTypeData = valueSequenceTypeData;
	}

}
