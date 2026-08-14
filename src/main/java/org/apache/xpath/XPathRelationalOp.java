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
package org.apache.xpath;

import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xml.utils.Constants;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.PrefixResolverDefault;
import org.apache.xpath.operations.XPathOperator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XPathRelationalOp extends XPathOperator {

	private static final long serialVersionUID = 8090025880595978756L;
	
	/**
	 * Method definition, to check whether an XML Schema 1.0, 
	 * supplied built-in type name is numeric.
	 * 
	 * @param typeName					  The supplied XML Schema type 
	 *                                    name string.
	 * @return                            Boolean value true or false
	 */
	protected boolean isXsBuiltInTypeNumeric(java.lang.String typeName) {

		boolean result = false;

		java.lang.String[] built_in_xs1_numeric_type_arr = new java.lang.String[] { "decimal", "double", "float", "integer", "long", 
																					"int", "short", "byte", "nonNegativeInteger", "unsignedLong",
																					"unsignedInt", "unsignedShort", "unsignedByte", "positiveInteger",
																					"nonPositiveInteger", "negativeInteger"};
		List<java.lang.String> strList = Arrays.asList(built_in_xs1_numeric_type_arr);
		if (strList.contains(typeName)) {
			result = true; 
		}

		return result;
	}
	
	/**
     * Method definition, to construct XML namespace PrefixResolver
     * object for XPath expression evaluation. 
     * 
     * @return                      PrefixResolver object instance
     */
    protected PrefixResolver getXMLNsPrefixResolver() {
    	
    	PrefixResolver result = null;
    	
        System.setProperty(Constants.XML_DOCUMENT_BUILDER_FACTORY_KEY, Constants.XML_DOCUMENT_BUILDER_FACTORY_VALUE);
    	
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
    	docBuilderFactory.setNamespaceAware(true);
    	DocumentBuilder docBuilder = null; 
    	try {
    	   docBuilder = docBuilderFactory.newDocumentBuilder();
    	}
    	catch (Exception ex) {
    	   // no op
    	}
    	
    	Document document = docBuilder.newDocument();
    	Element elem = document.createElement("elem1");
    	elem.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:fn", "http://www.w3.org/2005/xpath-functions");
    	elem.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:math", "http://www.w3.org/2005/xpath-functions/math");
    	elem.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:map", "http://www.w3.org/2005/xpath-functions/map");
    	elem.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:array", "http://www.w3.org/2005/xpath-functions/array");
    	elem.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xs", "http://www.w3.org/2001/XMLSchema");
    	
    	document.appendChild(elem);
    	
    	result = new PrefixResolverDefault(elem);
    	
    	return result;
    }
    
}
