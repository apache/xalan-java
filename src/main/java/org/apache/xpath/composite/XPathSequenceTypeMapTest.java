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
 * Class definition, to implement XPath 3.1 sequence 
 * type MapTest.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPathSequenceTypeMapTest {

    private boolean m_isAnyMapTest;

    private XPathSequenceType m_keySequenceTypeData;
    
    private XPathSequenceType m_valueSequenceTypeData;

	public boolean isAnyMapTest() {
		return m_isAnyMapTest;
	}
	
	public void setIsAnyMapTest(boolean isAnyMapTest) {
		this.m_isAnyMapTest = isAnyMapTest;
	}

	public XPathSequenceType getKeySequenceTypeData() {
		return m_keySequenceTypeData;
	}

	public void setKeySequenceTypeData(XPathSequenceType keySequenceTypeData) {
		this.m_keySequenceTypeData = keySequenceTypeData;
	}

	public XPathSequenceType getValueSequenceTypeData() {
		return m_valueSequenceTypeData;
	}

	public void setValueSequenceTypeData(XPathSequenceType valueSequenceTypeData) {
		this.m_valueSequenceTypeData = valueSequenceTypeData;
	}

	/**
	 * Method definition, to check whether, the supplied XPathSequenceTypeMapTest 
	 * object instance is equal to this XPathSequenceTypeMapTest object instance.  
	 * 
	 * @param sequenceTypeMapTest2					The supplied XPathSequenceTypeMapTest
	 *                                              object instance. 
	 * @return                                      Boolean value true or false
	 */
	public boolean equal(XPathSequenceTypeMapTest sequenceTypeMapTest2) {
		
		boolean result = false;
		
		if (sequenceTypeMapTest2.isAnyMapTest() && m_isAnyMapTest) {
		   result = true;
		}
		else {
		   XPathSequenceType keySeqType2 = sequenceTypeMapTest2.getKeySequenceTypeData();
		   XPathSequenceType valueSeqType2 = sequenceTypeMapTest2.getValueSequenceTypeData();
		   
		   if (keySeqType2.equal(m_keySequenceTypeData) && valueSeqType2.equal(m_valueSequenceTypeData)) {
			  result = true; 
		   }
		}
		
		return result;
	}

}
