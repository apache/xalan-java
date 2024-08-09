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
 * An object of this class, stores few run-time information
 * for evaluation of XPath path expressions having function
 * call suffix.
 * 
 * For e.g, an XPath expression /a/b/funcCall(.) is split into
 * two XPath strings /a/b and funcCall(.). The function call 
 * funcCall(.) is invoked on each node of the nodeset resulting 
 * from evaluation of XPath expression /a/b.    
 *
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPathExprFunctionSuffix {

	private String m_xpathOneStr = null;
	
	private String m_xpathTwoStr = null;

	public String getXPathOneStr() {
		return m_xpathOneStr;
	}

	public void setXPathOneStr(String xpathOneStr) {
		this.m_xpathOneStr = xpathOneStr;
	}

	public String getXPathTwoStr() {
		return m_xpathTwoStr;
	}

	public void setXPathTwoStr(String xpathTwoStr) {
		this.m_xpathTwoStr = xpathTwoStr;
	}

}
