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
 * Class definition, to represent an xdm comment 
 * node item.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XdmCommentItem extends XObject {

	private static final long serialVersionUID = 3267848491891237013L;
	
	/**
	 * Represents text value of comment.
	 */
	private String m_value = null;
	
	/**
	 * Class field, representing value returned by
	 * XPath fn:generate-id function for this xdm node.
	 */
	private String m_id = null;
	
	/**
	 * Class constructor.
	 */
	public XdmCommentItem(String value) {
		m_value = value;
		
		m_id = getIdValue();
	}

	public String getValue() {
		return m_value;
	}

	public void setValue(String value) {
		this.m_value = value;
	}
	
	public String str() {
		return m_value;
	}
	
	public String getIdValue() {
		String result = null;

		if (m_id != null) {
			result = m_id;
		}
		else {
			double dblValue = Math.random();
			String dblStr1 = String.valueOf(dblValue);
			result = "N" + dblStr1.substring(2, 7);
		}

		return result;
	}
	
}
