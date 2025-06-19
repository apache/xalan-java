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

import org.apache.xpath.objects.XObject;

/**
 * A class representing an XML attribute's name components and value. 
 * The main use for this class is, when an xsl:function constructs 
 * only an attribute node via xsl:attribute instruction.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XMLAttribute extends XObject {
	
	private static final long serialVersionUID = 154222928316145741L;

	/**
	 * An XML attribute's local name.
	 */
	private String m_localName;
	
	/**
	 * An XML attribute's namespace prefix.
	 */
	private String m_prefix;
	
	/**
	 * An XML attribute's namespace uri.
	 */
	private String m_namespaceUri;
	
	/**
	 * An XML attribute's string value.
	 */
	private String m_attrValue;
	
	/**
	 * Class constructor.
	 * 
	 * @param localName							An attribute node's local name
	 * @param prefix							An attribute node's namespace prefix
	 * @param namespaceUri						An attribute node's namespace uri
	 * @param attributeValue					An attribute node's string value
	 */
	public XMLAttribute(String localName, String prefix, String namespaceUri, String attributeValue) {
		this.m_localName = localName;
		this.m_prefix = prefix;
		this.m_namespaceUri = namespaceUri;
		this.m_attrValue = attributeValue;
	}
	
	public String getLocalName() {
	    return this.m_localName;	
	}
	
	public void setLocalName(String locaName) {
		this.m_localName = locaName;
	}
	
	public String getPrefix() {
	    return this.m_prefix;	
	}
	
	public void setPrefix(String prefix) {
		this.m_prefix = prefix;
	}
	
	public String getNamespaceUri() {
		return this.m_namespaceUri;
	}
	
	public void setNamespaceUri(String namespaceUri) {
	   this.m_namespaceUri = namespaceUri;	
	}
	
	public String getAttrValue() {
	    return this.m_attrValue;	
	}
	
	public void setAttrValue(String attrValue) {
	    this.m_attrValue = attrValue;	
	}

}
