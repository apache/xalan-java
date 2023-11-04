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

import java.util.List;

/**
 * An object of this class stores, an XSLT transformation run-time
 * information for an occurrence of a XPath 3.1 sequence type
 * FunctionTest.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class SequenceTypeFunctionTest {

    private boolean fIsAnyFunctionTest;

    private List<String> typedFunctionTestPrefixList;
    
    private String typedFunctionTestReturnType;

	public boolean isAnyFunctionTest() {
		return fIsAnyFunctionTest;
	}

	public void setIsAnyFunctionTest(boolean isAnyFunctionTest) {
		this.fIsAnyFunctionTest = isAnyFunctionTest;
	}

	public List<String> getTypedFunctionTestPrefixList() {
		return typedFunctionTestPrefixList;
	}

	public void setTypedFunctionTestPrefixList(List<String> typedFunctionTestPrefix) {
		this.typedFunctionTestPrefixList = typedFunctionTestPrefix;
	}

	public String getTypedFunctionTestReturnType() {
		return typedFunctionTestReturnType;
	}

	public void setTypedFunctionTestReturnType(String typedFunctionTestReturnType) {
		this.typedFunctionTestReturnType = typedFunctionTestReturnType;
	} 

}
