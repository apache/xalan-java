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
package org.apache.xpath.objects;

/**
 * Class definition, to represent an xdm attribute 
 * node item.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XdmAttributeItem extends XObject {
	
	private static final long serialVersionUID = -1364342059577570615L;

	/**
	 * Class field, representing an XML attribute's 
	 * local name. 
	 */
	private String m_attrLocalName = null;
	
	/**
	 * Class field, representing an XML attribute's 
	 * namespace. 
	 */
	private String m_attrNodeNs = null;
	
	/**
	 * Class field, representing an XML attribute's 
	 * string value. 
	 */
	private String m_attrStrValue = null;
	
	/**
	 * Class field, representing value returned by
	 * XPath fn:generate-id function for this xdm node.
	 */
	private String m_id = null;
	
	/**
	 * Class constructor.
	 * 
	 * @param attrLocalName				An XML attribute's local name
	 * @param attrNs                    An XML attribute's namespace
	 * @param attrValue                 An XML attribute's string value
	 */
	public XdmAttributeItem(String attrLocalName, String attrNs, String attrValue) {
		m_attrLocalName = attrLocalName;
		m_attrNodeNs = attrNs;
		m_attrStrValue = attrValue;
		
		m_id = getIdValue();
	}

	public String getAttrLocalName() {
		return m_attrLocalName;
	}

	public void setAttrLocalName(String attrLocalName) {
		this.m_attrLocalName = attrLocalName;
	}

	public String getAttrNodeNs() {
		return m_attrNodeNs;
	}

	public void setAttrNodeNs(String attrNodeNs) {
		this.m_attrNodeNs = attrNodeNs;
	}

	public String getAttrStrValue() {
		return m_attrStrValue;
	}

	public void setAttrStrValue(String attrStrValue) {
		this.m_attrStrValue = attrStrValue;
	}
	
	public String str() {
		return m_attrStrValue;
	}
	
	public String getIdValue() {
		
		String result = null;

		if (m_id != null) {
			result = m_id;
		}
		else {
			double dblValue = Math.random();
			String dblStr1 = String.valueOf(dblValue);
			String str1 = dblStr1.substring(2, 7);
			
			result = "N" + Integer.toHexString(Integer.valueOf(str1)).toUpperCase();
		}

		return result;
	}

}
