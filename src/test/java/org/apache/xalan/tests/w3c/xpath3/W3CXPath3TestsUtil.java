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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.apache.xalan.tests.util.XslTransformTestsUtil;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.utils.Constants;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.PrefixResolverDefault;
import org.apache.xpath.XPathContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Xalan-J XSL 3 test base class, to support Xalan-J W3C 
 * XPath 3.1 test suite driver.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class W3CXPath3TestsUtil extends XslTransformTestsUtil { 
	
	protected static final String W3C_XPATH3_TESTS_META_DATA_DIR_HOME = "file:/d:/qt3tests-master/";
	
	protected static final String W3C_XPATH3_TESTS_CATALOG_FILE_PATH = W3C_XPATH3_TESTS_META_DATA_DIR_HOME + "catalog.xml";
	
	protected static final String W3C_XPATH3_TESTS_RESULT_DIR_HOME = "file:/d:/eclipseWorkspaces/xalanj/xalan-j_xslt3.0_mvn/src/test/java/org/apache/xalan/tests/w3c/xpath3/result/";
			
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
    
    protected static List<String> m_skipped_tests_list = new ArrayList<String>();
    
    protected static String m_resultSubFolderName = null;
    
    protected static String m_testResultFileName = null;

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
     * Method definition, to get an XPath normalized string
     * by replacing all substrings of form "..." to '...', to 
     * make them XPath legal string literals. 
     * 
     * @param xpathExprStr                The supplied XPath string value
     * @return                            An XPath normalized string value
     */
    protected String getXPathNormalizedStr(String xpathExprStr) {
		
		String result = null;
		
		xpathExprStr = replaceExpandedNsDecl(xpathExprStr);

		int idx1 = xpathExprStr.indexOf("\"");
		int idx2 = -1;
		int strLength = xpathExprStr.length();
		if (strLength > 1) {
			while (idx1 != -1) {
				String str1 = xpathExprStr.substring(0, idx1); 
				String str2 = xpathExprStr.substring(idx1 + 1);    									 
				idx2 = str2.indexOf("\"");
				String xpathExprStrNew = null;
				if (idx2 != -1) {
					String x1 = str2.substring(0, idx2);
					str2 = "'" + x1 + "'";
					String prefixStr = str1 + str2;
					String suffixStr = xpathExprStr.substring(prefixStr.length());
					xpathExprStrNew = prefixStr + suffixStr;    									       									   
				}

				if (xpathExprStrNew != null) {
					xpathExprStr = xpathExprStrNew; 
					idx1 = xpathExprStr.indexOf("\"");
				}
				else {
					break;	
				}
			}
		}
		
		result = xpathExprStr;
		
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
    
    /**
     * Method definition, to construct Xalan-J DTM object instance using
     * the supplied XML document file. Xalan-J DTM is constructed, and 
     * the supplied XPath context object is set with XML document node
     * as the context node.
     * 
     * @param xmlFile							The supplied XML document file name
     * @param xctxt                             The supplied XPath context object
     * @param resolveWithCatalog                Boolean value indicating, whether to
     *                                          resolve XML input document with catalog
     *                                          file or not.
     * @throws URISyntaxException
     * @throws MalformedURLException
     * @throws SAXException
     * @throws IOException
     */
    protected void constructXalanDtmFromXMLFile(String xmlFile, XPathContext xctxt, boolean resolveWithCatalog) 
    		                                                                                                throws URISyntaxException, MalformedURLException, 
    																										                    SAXException, IOException {
    	URI uri = new URI(xmlFile);
    	URL resolvedUrl = null;
    	if (uri.isAbsolute()) {
    		resolvedUrl = new URL(xmlFile); 
    	}
    	else {
    		URI uri2 = null;
    		if (resolveWithCatalog) {
    		   uri2 = (new URI(W3C_XPATH3_TESTS_CATALOG_FILE_PATH)).resolve(xmlFile);
    		}
    		else {
    		   uri2 = (new URI(m_xslTransformTestSetFilePath)).resolve(xmlFile);
    		}
    		
    		resolvedUrl = uri2.toURL();
    	}

    	String sourceDocUrlStr = resolvedUrl.toString();									 									 
    	Document document2 = m_xmlDocumentBuilder.parse(sourceDocUrlStr);

    	DOMSource domSource = new DOMSource(document2);
    	Source source = (Source)domSource;
    	source.setSystemId(sourceDocUrlStr);

    	DTMManager dtmManager = xctxt.getDTMManager();
    	DTM dtm = dtmManager.getDTM((Source)domSource, true, null, false, false);
    	dtm.setDocumentBaseURI(sourceDocUrlStr);

    	int docNodeHandle = dtm.getDocument();
    	xctxt.pushCurrentNode(docNodeHandle);
    }
    
    /**
     * Method definition, to get an ISO formatted date string for the supplied 
     * java.util.Date value.
     *  
     * @param date				The supplied java.util.Date object value
     * @return					The formatted date value string
     */
    protected String getDateISOString(Date dateValue) {
    	String result = null;
    	
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        
        result = sdf.format(dateValue); 
        
        return result;
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
    private String replaceExpandedNsDecl(String xpathExprStr) {
		
    	String result = null; 
		
    	xpathExprStr = xpathExprStr.replace("Q{http://www.w3.org/2005/xpath-functions}", "");
    	xpathExprStr = xpathExprStr.replace("Q{http://www.w3.org/2005/xpath-functions/math}", "math:");
    	xpathExprStr = xpathExprStr.replace("Q{http://www.w3.org/2005/xpath-functions/map}", "map:");
    	xpathExprStr = xpathExprStr.replace("Q{http://www.w3.org/2005/xpath-functions/array}", "array:");
    	xpathExprStr = xpathExprStr.replace("Q{http://www.w3.org/2001/XMLSchema}", "xs:");
    	
    	result = xpathExprStr; 
		
		return result;
	}

}
