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
package org.apache.xalan.tests.util;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.utils.Constants;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * A class definition, having utility methods to compare various 
 * types of file contents.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FileComparisonUtil {
	
	/**
	 * Class field representing an XML DocumentBuilderFactory object instance 
	 * needed by this test suite. 
	 */
    protected static DocumentBuilderFactory xmlDocumentBuilderFactory = null;
    
    /**
     * Class field representing an XML DocumentBuilder object instance 
     * needed by this test suite.
     */
    protected static DocumentBuilder xmlDocumentBuilder = null;
    
    /**
     * Class field representing, types of files whose contents may be 
     * compared. Possible values are XML, JSON, TEXT and HTML.
     */
    protected String fileComparisonType = XSLTestConstants.XML;
	
	/**
	 * Class constructor.
	 */
	public FileComparisonUtil() {
		System.setProperty(Constants.XML_DOCUMENT_BUILDER_FACTORY_KEY, Constants.XML_DOCUMENT_BUILDER_FACTORY_VALUE);
		
		xmlDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
        xmlDocumentBuilderFactory.setNamespaceAware(true);
        
        try {
            xmlDocumentBuilder = xmlDocumentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {            
            ex.printStackTrace();
        }
	}
	
	/**
	 * Method definition to find whether, contents of two xml documents represented as 
	 * string values, are equal.
	 * 
	 * Instead if directly comparing the supplied XML document string values for equality, 
	 * we do an XML parse of the supplied string values to produce XML dom nodes, and
	 * then use LSSerializer to serialize the XML dom nodes and compare these
	 * string values produced from XML serialization method calls. This ensures that, 
	 * irrelevant whitespaces present within the supplied XML document strings and within 
	 * the expected XML document results do not affect the XML document string comparison 
	 * result.
	 * 
	 * @param xmlStr1			first supplied xml document string
	 * @param xmlStr2			second supplied xml document string	
	 * @return					boolean value indicating the result of
	 * 							XML document comparison.
	 */
	protected boolean isXMLFileContentsEqual(String xmlStr1, String xmlStr2) throws Exception {
		
		boolean result = false;

		StringReader strReader1 = new StringReader(xmlStr1);
		StringReader strReader2 = new StringReader(xmlStr2);
		Document document1 = xmlDocumentBuilder.parse(new InputSource(strReader1));
		Document document2 = xmlDocumentBuilder.parse(new InputSource(strReader2));
		String xmlNormalizedStr1 = XslTransformEvaluationHelper.serializeXmlDomElementNode(
																						document1.getDocumentElement());            	  
		String xmlNormalizedStr2 = XslTransformEvaluationHelper.serializeXmlDomElementNode(
																						document2.getDocumentElement());
		
		result = xmlNormalizedStr1.equals(xmlNormalizedStr2); 

		return result;
	}
	
	/**
	 * Method definition to find whether, contents of two json 
	 * documents represented as string values, are equal.
	 * 
	 * @param jsonStr1			first json document string
	 * @param jsonStr2			second json document string	
	 * @return					boolean value indicating the result of
	 * 							file comparison.
	 */
	protected boolean isJsonFileContentsEqual(String jsonStr1, String jsonStr2) {
		
		boolean result = false;
		
		jsonStr1 = jsonStr1.startsWith("[") ? ("{ \"key1\" : " + jsonStr1 + "}") : jsonStr1;
		jsonStr2 = jsonStr2.startsWith("[") ? ("{ \"key1\" : " + jsonStr2 + "}") : jsonStr2;
		
		JSONObject jsonObj1 = new JSONObject(jsonStr1);
		JSONObject jsonObj2 = new JSONObject(jsonStr2);
		
		result = (jsonObj1.toString()).equals(jsonObj2.toString());
		
		return result;
	}

}
