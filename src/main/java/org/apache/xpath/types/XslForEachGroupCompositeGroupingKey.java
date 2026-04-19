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
package org.apache.xpath.types;

import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathCollationSupport;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.FuncDeepEqual;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;

import xml.xpath31.processor.types.XSString;

/**
 * An object instance of this class, represents xsl:for-each-group 
 * instruction's run-time value of a composite grouping key for a 
 * particular group that is formed while evaluating xsl:for-each-group 
 * instruction.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XslForEachGroupCompositeGroupingKey {
	
	/**
	 * The sequence that has items for the composite 
	 * grouping key.
	 */
	private ResultSequence m_value;
	
	/**
	 * Collation uri if available.
	 */
	private String m_collationUri;
	
	/**
	 * A non-null XPathCollationSupport object instance.
	 */
	private XPathCollationSupport m_xpathCollationSupport;
	
	/**
	 * A non-null XPathContext object instance. 
	 */
	private XPathContext m_xctxt;
	
	/**
	 * Class constructor.
	 */
	public XslForEachGroupCompositeGroupingKey(XPathContext xctxt, ResultSequence value, String collationUri, 
			                                                                                   XPathCollationSupport xPathCollationSupport) {	   
	   this.m_value = value;
	   this.m_collationUri = collationUri;
	   this.m_xpathCollationSupport = xPathCollationSupport; 
	   this.m_xctxt = xctxt;
	}

	public ResultSequence getValue() {
		return m_value;
	}

	public void setValue(ResultSequence value) {
		this.m_value = value;
	}
	
	public String getCollationUri() {
		return m_collationUri;
	}

	public void setCollationUri(String collationUri) {
		this.m_collationUri = collationUri;
	}
	
	public XPathCollationSupport getXPathCollationSupport() {
		return m_xpathCollationSupport;
	}

	public void setXPathCollationSupport(XPathCollationSupport m_xpathCollationSupport) {
		this.m_xpathCollationSupport = m_xpathCollationSupport;
	}

	public XPathContext getXPathContext() {
		return m_xctxt;
	}

	public void setXPathContext(XPathContext xctxt) {
		this.m_xctxt = xctxt;
	}

	public boolean equals(Object obj2) {
		boolean result = false;
		
		XslForEachGroupCompositeGroupingKey grpKey2 = (XslForEachGroupCompositeGroupingKey)obj2;
		ResultSequence value2 = grpKey2.getValue();
						
		try {
			if (m_value.size() == value2.size()) {
			   FuncDeepEqual funcDeepEqual = new FuncDeepEqual(m_xpathCollationSupport);
			   result = funcDeepEqual.isTwoSequenceDeepEqual(m_xctxt, m_collationUri, m_value, value2);
			}
		} catch (TransformerException ex) {
			// no op
		} catch (Exception ex) {
			// no op			
		} 
		
		return result;
	}
	
	public int hashCode() {
		int result = -100;   // initializing to a likely good value that cannot be a real hashCode value.
		
		if ((XPathCollationSupport.CASE_BLIND_COLLATION_URI).equals(m_collationUri)) {
		   int size1 = m_value.size();
		   ResultSequence rSeq = new ResultSequence();
		   for (int i = 0; i < size1; i++) {
			  XObject xObj1 = m_value.item(i);
			  if (xObj1 instanceof XString) {
				 String str1 = (((XString)xObj1).str()).toLowerCase();
				 rSeq.add(new XString(str1));
			  }
			  else if (xObj1 instanceof XSString) {
				 String str1 = (((XSString)xObj1).stringValue()).toLowerCase();
				 rSeq.add(new XSString(str1));
			  }
			  else {
				  rSeq.add(xObj1); 
			  }
		   }
		   
		   result = rSeq.hashCode();
		}
		else {
		   result = m_value.hashCode();
		}
		
		return result;
	}
}
