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

import java.text.CollationKey;
import java.text.Collator;

import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathCollationSupport;

/**
 * An object of this class, supports implementation of xsl:for-each-group 
 * instruction's attribute 'collation'.
 * 
 * When an xsl:for-each-group instruction has a string valued grouping key 
 * and a 'collation' attribute, the grouping map (an java.util.Map object instance) 
 * stores the java.util.Map's key as an object instance of this class.
 * 
 * For xsl:for-each-group instruction not having an attribute 'collation',
 * the grouping map (an java.util.Map object instance) stores the key value 
 * typed as java.lang.String.  
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 *  
 * @xsl.usage advanced
 */
public class StringWithCollation {
	
	/**
	 * The string value which may belong to any language locale, supported by
	 * Xalan-J XSL 3 implementation's XPathCollationSupport class.
	 */
	private String m_strValue = null;
	
	/**
	 * Collation uri string, which is the value of 'collation' attribute
	 * of XSLT instruction xsl:for-each-group. 
	 */
	private String m_collationUri = null;
	
	/**
	 * Xalan-J XSL 3 implementation's collation support class.
	 */
	private XPathCollationSupport m_xpathCollationSupport = null;
	
	/**
	 * A string value which may have an error message information, 
	 * during xsl:for-each-group instruction processing considering
	 * 'collation'. 
	 */
	private String m_strCompareErrMesg = null;
	
	/**
	 * Class constructor.
	 * 
	 * @param strValue
	 * @param collationUri
	 * @param xpathCollationSupport
	 */
	public StringWithCollation(String strValue, String collationUri, XPathCollationSupport xpathCollationSupport) {
		this.m_strValue = strValue;
		this.m_collationUri = collationUri;
		this.m_xpathCollationSupport = xpathCollationSupport;
	}

	public String getStrValue() {
		return m_strValue;
	}

	public void setStrValue(String strValue) {
		this.m_strValue = strValue;
	}

	public String getCollation() {
		return m_collationUri;
	}

	public void setCollation(String collationUri) {
		this.m_collationUri = collationUri;
	}

	public XPathCollationSupport getXPathCollationSupport() {
		return m_xpathCollationSupport;
	}

	public void setXPathCollationSupport(XPathCollationSupport xpathCollationSupport) {
		this.m_xpathCollationSupport = xpathCollationSupport;
	}
	
	public boolean equals(Object obj2) {
		boolean result = false;

		if (!(obj2 instanceof StringWithCollation)) {
			result = super.equals(obj2);
		}
		else {
			StringWithCollation strWithCollationObj2 = (StringWithCollation)obj2;
			try {
				int strComparisonResult = m_xpathCollationSupport.compareStringsUsingCollation(m_strValue, strWithCollationObj2.getStrValue(), 
																																			m_collationUri);
				result = (strComparisonResult == 0) ? true : false;
			} 
			catch (TransformerException ex) {
				this.m_strCompareErrMesg = ex.getMessage();
			}
		}

		return result;
	}
	
	public int hashCode() {
		int result = -100;   // initializing to a likely good value that cannot be a real hashCode value.
		
		try {
			Collator collator = m_xpathCollationSupport.getUCACollatorFromCollationUri(m_collationUri);
			CollationKey collationKey = collator.getCollationKey(m_strValue);
			result = collationKey.hashCode();
		} 
		catch (TransformerException ex) {
			this.m_strCompareErrMesg = ex.getMessage();
		}
		
		return result; 
	}
	
	public String getErrorMesg() {
		return this.m_strCompareErrMesg; 
	}

}
