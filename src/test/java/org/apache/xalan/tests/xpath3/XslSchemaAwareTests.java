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
package org.apache.xalan.tests.xpath3;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.xalan.templates.Constants;
import org.apache.xalan.tests.util.FileComparisonUtil;
import org.apache.xalan.tests.util.XSLTestConstants;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xml.utils.DefaultErrorHandler;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import junit.framework.Assert;

/**
 * XSL 3 stylesheet test cases, to test XPath 3.1 schema 
 * aware feature.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XslSchemaAwareTests extends FileComparisonUtil {        
    
    private static final String XSL_TRANSFORM_INPUT_DIRPATH = XSLTestConstants.XSL_TRANSFORM_INPUT_DIRPATH_PREFIX + "xsl_schema_aware/";
    
    private static TransformerFactory m_xslTransformerFactory = null;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    	System.setProperty(Constants.XML_DOCUMENT_BUILDER_FACTORY_KEY, Constants.XML_DOCUMENT_BUILDER_FACTORY_VALUE);    	
    	System.setProperty(Constants.XSL_TRANSFORM_FACTORY_KEY, Constants.XSL_TRANSFORM_FACTORY_VALUE);
    	
    	m_xslTransformerFactory = TransformerFactory.newInstance();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {        
        // no op
    }

    @Test
    public void xslSchemaAwareTest1() {
    	String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1.xml"; 
    	String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1.xsl";                

    	InputSource inpSrc = new InputSource(xmlFilePath);

    	TransformerFactory tfactory = TransformerFactory.newInstance();
    	tfactory.setErrorListener(new DefaultErrorHandler(true));
    	
    	try {
    		String xslDocumentUriStr = ((new File(xslFilePath)).toURI()).toString();
    		StreamSource xslStreamSrc = new StreamSource(xslDocumentUriStr);
    		
    		Transformer transformer = m_xslTransformerFactory.newTransformer(xslStreamSrc);

    		DOMParser parser = new DOMParser();
    		parser.setFeature(Constants.XML_VALIDATION_FEATURE, true);
    		parser.setFeature(Constants.XML_SCHEMA_VALIDATION_FEATURE, true);
    		parser.setFeature(Constants.XML_SCHEMA_FULL_CHECKING_FEATURE, true);

    		parser.setProperty(Constants.XML_DOM_DOCUMENT_CLASS_NAME, Constants.XERCES_PSVI_DOCUMENT_IMPL);
    		
    		parser.parse(inpSrc);
    		Document document = parser.getDocument();

    		DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
    		dfactory.setCoalescing(true);
    		dfactory.setNamespaceAware(true);

    		StringWriter resultStrWriter = new StringWriter();
            StreamResult streamResult = new StreamResult(resultStrWriter);					  						  

    		transformer.transform(new DOMSource(document, xmlFilePath), streamResult);
    		
    		String actualResultStr = resultStrWriter.toString();    		    		
            
            String expectedResultStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><result>\n"
													            		+ "  <one isInteger=\"true\">7</one>\n"
													            		+ "  <two isDate=\"true\">2010-11-15</two>\n"
													            		+ "</result>\n";
            
            boolean isTestPass = isXMLFileContentsEqual(actualResultStr, expectedResultStr);
            
            assertTrue(isTestPass);
    	}
    	catch (Exception ex) {
    		Assert.fail();
    	}
    }

}
