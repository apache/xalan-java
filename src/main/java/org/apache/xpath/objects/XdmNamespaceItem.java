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
 * Class definition, to represent an xdm namespace 
 * node item.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XdmNamespaceItem extends XObject {

	private static final long serialVersionUID = -796575556890931503L;

	/**
	 * Class field, representing an XML namespace node's 
	 * ncname value.
	 */
	private String m_NsName = null;
	
	/**
	 * Class field, representing an XML namespace node's 
	 * string value. 
	 */
	private String m_NsStrValue = null;
	
	/**
	 * Class constructor.
	 * 
	 * @param nsName				  An XML namespace node's name
	 * @param nsValue                 An XML namespace node's string 
	 *                                value.
	 */
	public XdmNamespaceItem(String nsName, String nsValue) {
		m_NsName = nsName;
		m_NsStrValue = nsValue; 
	}

	public String getNamespaceNodeName() {
		return m_NsName;
	}

	public void setNamespaceNodeName(String nsName) {
		this.m_NsName = nsName;
	}

	public String getNsNodeValue() {
		return m_NsStrValue;
	}

	public void setNsNodeValue(String nsValue) {
		this.m_NsStrValue = nsValue;
	}

}
