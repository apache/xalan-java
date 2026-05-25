/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.xalan.tests.w3c.xpath3;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xml.utils.Constants;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.PrefixResolverDefault;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Xalan-J XSL 3 test base class, to support Xalan-J W3C 
 * XPath 3.1 test suite driver.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class W3CXPath3TestsUtil { 
	
	protected static final String W3C_XPATH3_TESTS_META_DATA_DIR_HOME = "file:/d:/qt3tests-master/";
			
	/**
	 * Class field representing an XML DocumentBuilderFactory object instance 
	 * needed by this test suite. 
	 */
    protected static DocumentBuilderFactory m_xmlDocumentBuilderFactory = null;
    
    /**
     * Class field representing, an XML DocumentBuilder object instance 
     * needed by this test suite.
     */
    protected static DocumentBuilder m_xmlDocumentBuilder = null;
    
    /**
     * XSL 3 test set file absolute path, with file:/ scheme.
     */
    protected static String m_xslTransformTestSetFilePath = null;

    /**
     * Class constructor.
     */
    public W3CXPath3TestsUtil() {    	    	
    	System.setProperty(Constants.XML_DOCUMENT_BUILDER_FACTORY_KEY, Constants.XML_DOCUMENT_BUILDER_FACTORY_VALUE);
    	
    	m_xmlDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
    	m_xmlDocumentBuilderFactory.setNamespaceAware(true);
    	try {
    	   m_xmlDocumentBuilder = m_xmlDocumentBuilderFactory.newDocumentBuilder();
    	}
    	catch (Exception ex) {
    	   // no op
    	}
    }
    
    /**
     * Method definition, to do string replacement, by replacing
     * expanded XML namespace references with their corresponding
     * abbreviations.
     * 
     * @param xpathExprStr                    The supplied XPath expression 
     *                                        string.
     * @return                                The replacement string
     */
    protected String replaceExpandedNsDecl(String xpathExprStr) {
		
    	String result = xpathExprStr; 
		
		result = result.replace("Q{http://www.w3.org/2005/xpath-functions}", "");
		result = result.replace("Q{http://www.w3.org/2005/xpath-functions/math}", "math:");
		result = result.replace("Q{http://www.w3.org/2005/xpath-functions/map}", "map:");
		result = result.replace("Q{http://www.w3.org/2005/xpath-functions/array}", "array:");
		result = result.replace("Q{http://www.w3.org/2001/XMLSchema}", "xs:");
		
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
    	
    	Document document = m_xmlDocumentBuilder.newDocument();
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
