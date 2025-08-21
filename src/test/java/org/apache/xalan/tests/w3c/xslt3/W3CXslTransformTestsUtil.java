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
package org.apache.xalan.tests.w3c.xslt3;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.xalan.templates.Constants;
import org.apache.xalan.templates.ElemTemplate;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.tests.util.XSLTestConstants;
import org.apache.xalan.tests.util.XslTestsErrorHandler;
import org.apache.xalan.tests.util.XslTransformTestsUtil;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.transformer.XalanProperties;
import org.apache.xalan.xslt.util.XslTransformData;
import org.apache.xml.dtm.DTM;
import org.apache.xml.utils.QName;
import org.apache.xml.utils.XML11Char;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.regex.Matcher;
import org.apache.xpath.regex.Pattern;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This class implementation, contains common code for Xalan-J's 
 * implementation of W3C XSLT 3.0 test suite driver.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class W3CXslTransformTestsUtil extends XslTransformTestsUtil {	    
	
    /**
     * Class field declarations. 
     */
	
	protected static final String W3C_XSLT3_TESTS_RESULT_DIR_HOME = "file:/d:/eclipseWorkspaces/xalanj/xalan-j_xslt3.0_mvn/src/test/java/org/apache/xalan/tests/w3c/xslt3/result/";
    
    protected static final String W3C_XSLT3_TESTS_META_DATA_DIR_HOME = "file:/d:/xslt30-test-master/tests/";
    
    protected static final String XSL_TRANSFORM_TEST_ALL_OF_TEMPLATE_FILE_PATH =  W3C_XSLT3_TESTS_META_DATA_DIR_HOME + "xsl_transform_assert_all_of_template.xsl";
    
    protected static final String XSL_TRANSFORM_NORMALIZE_NS_FILE_PATH =  W3C_XSLT3_TESTS_META_DATA_DIR_HOME + "xsl_transform_normalize_ns.xsl";
    
	private static final String ELEM_NODE_NAME_TEST_CASE = "test-case";
	
	private static final String ELEM_NODE_NAME_TEST = "test";
	
	private static final String ELEM_NODE_NAME_INITIAL_TEMPLATE = "initial-template";
	
	private static final String NAME_ATTR = "name";
	
	private static final String FILE_ATTR = "file";
	
	private static final String TRUE = "true";
    
    private static final String EXPECTED_NODE_KIND_ASSERT_ALL_OF = "all-of";
    
    private static final String EXPECTED_NODE_KIND_ASSERT_ANY_OF = "any-of";
    
    private static final String EXPECTED_NODE_KIND_ASSERT = "assert";
    
    private static final String EXPECTED_NODE_KIND_ASSERT_XML = "assert-xml";
    
    private static final String EXPECTED_NODE_KIND_ERROR = "error";
    
    private static final String SERIALIZATION_MATCHES = "serialization-matches";
    
    private static final String ASSERT_SERIALIZATION = "assert-serialization";
    
    private static final String W3C_XSLT3_TEST_CATALOG_NS = "http://www.w3.org/2012/10/xslt-test-catalog";
    
    protected static String m_xslTransformTestSetFilePath = null;
    
    protected static String m_resultSubFolderName = null;
    
    protected static String m_testResultFileName = null;
    
    protected static List<String> m_skipped_tests_list = new ArrayList<String>();
	
	/**
	 * Method definition to run, all XSL transformation tests from 
	 * a W3C XSLT 3.0 test set.
	 */
    public void runXslTestSet() {    	
    	
    	Document xslTestSetDoc = null;
    	
    	FileOutputStream testResultFos = null;
    	
    	Document testResultDoc = null;    	
    	Element elemTestRun = null;
    	
    	try {
    	   // An XML parse of W3C XSLT 3.0 test set file
    	   xslTestSetDoc = m_xmlDocumentBuilder.parse(m_xslTransformTestSetFilePath);
    	   
    	   Element docElem = xslTestSetDoc.getDocumentElement();
    	   
		   String testSetName = docElem.getAttribute(NAME_ATTR);
		   testResultDoc = m_xmlDocumentBuilder.newDocument();
    	   elemTestRun = testResultDoc.createElement("testrun");
    	   String testRunDateStrValue = getDateISOString(new Date());
    	   elemTestRun.setAttribute(NAME_ATTR, testSetName);
    	   elemTestRun.setAttribute("dateTime", testRunDateStrValue);
    	   testResultDoc.appendChild(elemTestRun);    	   
    	   
    	   NodeList nodeList = xslTestSetDoc.getElementsByTagNameNS(W3C_XSLT3_TEST_CATALOG_NS, ELEM_NODE_NAME_TEST_CASE);
    	   for (int idx = 0; idx < nodeList.getLength(); idx++) {
    		   Node node = nodeList.item(idx);
    		   String testCaseName = ((Element)node).getAttribute(NAME_ATTR);     		   
    		   if (isXslt2OnlyTestCase(node)) {
    			  // We skip running XSLT 2.0 only test cases
    			  Element elemTestResult = testResultDoc.createElement("testResult");
    			  elemTestResult.setAttribute("testName", testCaseName);
    			  elemTestResult.setAttribute("status", "skipped");
    			  elemTestResult.setAttribute("xsltVersion", "xslt 2.0 only");
    			  elemTestRun.appendChild(elemTestResult);
    			  
    			  continue; 
    		   }
    		   else if (isStreamingFeatureTestCase(node)) {
     			  // We skip running XSLT 3.0 streaming feature test cases
       			  Element elemTestResult = testResultDoc.createElement("testResult");
       			  elemTestResult.setAttribute("testName", testCaseName);
       			  elemTestResult.setAttribute("status", "skipped");
       			  elemTestResult.setAttribute("feature", "streaming");
       			  elemTestRun.appendChild(elemTestResult);
       			  
       			  continue; 
     		   }
    		   else if (m_skipped_tests_list.contains(testCaseName)) {
    			   /**
    			    * We skip running W3C XSLT 3.0 test cases available within this
    			    * test suite, that're configured to be skipped within this XSLT test
    			    * suite driver and approved by XSLT WG and Xalan-J's analysis.
    			    */
    			   Element elemTestResult = testResultDoc.createElement("testResult");
    			   elemTestResult.setAttribute("testName", testCaseName);
    			   elemTestResult.setAttribute("status", "skipped");
    			   elemTestResult.setAttribute("reason", "allowed by xslt 3.0 test suite");
    			   elemTestRun.appendChild(elemTestResult);

    			   continue;  
    		   }    		   
    		       		   
    		   Object envRef = getTestCaseEnvironment(node);
    		   NodeList nodeList2 = null;
    		   
    		   String xmlDocInpStr = null;
    		   
    		   if (!(envRef instanceof Element)) {
    			   nodeList2 = docElem.getChildNodes();    			   
    			   for (int idx2 = 0; idx2 < nodeList2.getLength(); idx2++) {
    				   Node node2 = nodeList2.item(idx2);
    				   if (node2.getNodeType() == Node.ELEMENT_NODE) {
    					   Element elemNode = (Element)node2;
    					   if ("environment".equals(elemNode.getLocalName())) {
    						   String envName = elemNode.getAttribute(NAME_ATTR);
    						   if (envName.equals(envRef)) {
    							   xmlDocInpStr = getXMLInputDocStr(elemNode);

    							   break;
    						   }
    					   }
    				   }
    			   }
    		   }
    		   else {
    			  Element envElem = (Element)envRef;
    			  Element envSrcElem = (Element)((envElem.getFirstChild()).getNextSibling());
    			  String envFileName = envSrcElem.getAttribute(FILE_ATTR);
    			  if (!"".equals(envFileName)) {
    				  URI uri = new URI(m_xslTransformTestSetFilePath);
					  uri = uri.resolve(envFileName);
					  xmlDocInpStr = getStringContentFromUrl(uri.toURL());					  
    			  }
    			  else {
    				  Element xmlContentElem = (Element)((envSrcElem.getFirstChild()).getNextSibling());
    				  xmlDocInpStr = xmlContentElem.getTextContent();     				  
    			  }
    		   }
    		   
    		   nodeList2 = node.getChildNodes();
    		   
    		   String xslStylesheetUriStr = null;
    		   Element expectedResultElem = null;
    		   for (int idx2 = 0; idx2 < nodeList2.getLength(); idx2++) {
    			   Node node2 = nodeList2.item(idx2);
    			   if (node2.getNodeType() == Node.ELEMENT_NODE) {
    				   Element elemNode = (Element)node2;
    				   if (ELEM_NODE_NAME_TEST.equals(elemNode.getLocalName())) {    					   
    					   NodeList nodeList3 = elemNode.getElementsByTagName("stylesheet");
    					   Element elemNode2 = (Element)(nodeList3.item(0));    					   
    					   String fileName = elemNode2.getAttribute(FILE_ATTR);
    					   URI uri = new URI(m_xslTransformTestSetFilePath);
   						   uri = uri.resolve(fileName);
   						   xslStylesheetUriStr = uri.toString();
   						   
   						   NodeList nodeList4 = elemNode.getElementsByTagName(ELEM_NODE_NAME_INITIAL_TEMPLATE);
   						   if (nodeList4.getLength() == 1) {
   							   Element elemNode3 = (Element)(nodeList4.item(0));
   							   m_initTemplateName = elemNode3.getAttribute(NAME_ATTR);
   						   }
   						   
   						   Node siblingNode = elemNode.getNextSibling();
   						   expectedResultElem = (Element)(siblingNode.getNextSibling());   						   
   						   
   						   break;
    				   }
    			   }
    		   }
    		   
    		   DOMSource xmlInpDomSource = null;
    		   
    		   if (xmlDocInpStr != null) {
    			   byte[] byteArr = xmlDocInpStr.getBytes(StandardCharsets.UTF_8);
    			   InputStream inpStream = new ByteArrayInputStream(byteArr);    		       		   
    			   xmlInpDomSource = new DOMSource(m_xmlDocumentBuilder.parse(inpStream));
    		   }
    		   
    		   StreamSource xslStreamSrc = new StreamSource(xslStylesheetUriStr);
    		   
    		   try {
    			  Map<String, XObject> xslParamMap = new HashMap<String, XObject>();
    		      runW3CXSLTTestSuiteXslTransformAndEmitResult(testCaseName, xmlInpDomSource, xslStreamSrc, xslParamMap, 
    		    		                                                     expectedResultElem, elemTestRun, testResultDoc);
    		   }
    		   catch (Exception ex) {
    			  System.out.println("Test case name : " + testCaseName + ", Exception message : " + ex.getMessage()); 
    		   }       		   
    	   }    	   
    	}
    	catch (Exception ex) {
    	   System.out.println(ex.getMessage());
    	}
    	finally {    	   	
    	   try {
    		   NodeList nodeList = testResultDoc.getElementsByTagName("testResult");
        	   
    		   int testsPassCount = 0;
        	   int testsfailCount = 0;
        	   int testsSkippedCount = 0;
        	   int testStatusUnknownCount = 0;
        	   
        	   for (int idx = 0; idx < nodeList.getLength(); idx++) {
        		  Element element = (Element)(nodeList.item(idx));
        		  String statusValue = element.getAttribute("status");
        		  if ("pass".equals(statusValue)) {
        			  testsPassCount++; 
        		  }
        		  else if ("fail".equals(statusValue)) {
        			  testsfailCount++; 
        		  }
        		  else if ("skipped".equals(statusValue)) {
        			  testsSkippedCount++; 
        		  }
        		  else {
        			  testStatusUnknownCount++; 
        		  }
        	   }
        	   
        	   int totalTestsRun = (testsPassCount + testsfailCount + testStatusUnknownCount);    
        	   
        	   elemTestRun.setAttribute("pass", String.valueOf(testsPassCount));
        	   elemTestRun.setAttribute("fail", String.valueOf(testsfailCount));
        	   elemTestRun.setAttribute("skipped", String.valueOf(testsSkippedCount));
        	   elemTestRun.setAttribute("statusUnknown", String.valueOf(testStatusUnknownCount));
        	   elemTestRun.setAttribute("totalRun", String.valueOf(totalTestsRun));
        	   
    		   // Serialize W3C XSLT 3.0 test set results file to file system
    		   String xslTestResultStr = serializeXmlDomElementNode(testResultDoc);
        	   
        	   File xslAnalyzeStringTestResultFile = new File(new URI(W3C_XSLT3_TESTS_RESULT_DIR_HOME + m_resultSubFolderName + "/" + m_testResultFileName));
        	   testResultFos = new FileOutputStream(xslAnalyzeStringTestResultFile);
        	   testResultFos.write(xslTestResultStr.getBytes());
        	   testResultFos.flush();
			   testResultFos.close();
		   } 
    	   catch (Exception ex) {
			   ex.printStackTrace();
		   }
    	}
    }
    
    /**
	 * Method definition to run, W3C XSLT 3.0 fn:json-to-xml test set.
	 */
    public void runXslJsonToXmlTestSet() {    	
    	
    	Document xslTestSetDoc = null;
    	
    	FileOutputStream testResultFos = null;
    	
    	Document testResultDoc = null;    	
    	Element elemTestRun = null;
    	
    	try {
    	   // An XML parse of W3C XSLT 3.0 test set file
    	   xslTestSetDoc = m_xmlDocumentBuilder.parse(m_xslTransformTestSetFilePath);
    	   
    	   Element docElem = xslTestSetDoc.getDocumentElement();
    	   
		   String testSetName = docElem.getAttribute(NAME_ATTR);
		   testResultDoc = m_xmlDocumentBuilder.newDocument();
    	   elemTestRun = testResultDoc.createElement("testrun");
    	   String testRunDateStrValue = getDateISOString(new Date());
    	   elemTestRun.setAttribute(NAME_ATTR, testSetName);
    	   elemTestRun.setAttribute("dateTime", testRunDateStrValue);
    	   testResultDoc.appendChild(elemTestRun);    	   
    	   
    	   NodeList nodeList = xslTestSetDoc.getElementsByTagNameNS(W3C_XSLT3_TEST_CATALOG_NS, ELEM_NODE_NAME_TEST_CASE);
    	   for (int idx = 0; idx < nodeList.getLength(); idx++) {
    		   Node node = nodeList.item(idx);
    		   String testCaseName = ((Element)node).getAttribute(NAME_ATTR);     		   
    		   if (isXslt2OnlyTestCase(node)) {
    			  // We skip running XSLT 2.0 only test cases
    			  Element elemTestResult = testResultDoc.createElement("testResult");
    			  elemTestResult.setAttribute("testName", testCaseName);
    			  elemTestResult.setAttribute("status", "skipped");
    			  elemTestResult.setAttribute("xsltVersion", "xslt 2.0 only");
    			  elemTestRun.appendChild(elemTestResult);
    			  
    			  continue; 
    		   }
    		   else if (isStreamingFeatureTestCase(node)) {
     			  // We skip running XSLT 3.0 streaming feature test cases
       			  Element elemTestResult = testResultDoc.createElement("testResult");
       			  elemTestResult.setAttribute("testName", testCaseName);
       			  elemTestResult.setAttribute("status", "skipped");
       			  elemTestResult.setAttribute("feature", "streaming");
       			  elemTestRun.appendChild(elemTestResult);
       			  
       			  continue; 
     		   }
    		   else if (m_skipped_tests_list.contains(testCaseName)) {
    			   /**
    			    * We skip running W3C XSLT 3.0 test cases available within this
    			    * test suite, that're configured to be skipped within this XSLT test
    			    * suite driver and approved by XSLT WG and Xalan-J's analysis.
    			    */
    			   Element elemTestResult = testResultDoc.createElement("testResult");
    			   elemTestResult.setAttribute("testName", testCaseName);
    			   elemTestResult.setAttribute("status", "skipped");
    			   elemTestResult.setAttribute("reason", "allowed by xslt 3.0 test suite");
    			   elemTestRun.appendChild(elemTestResult);

    			   continue;  
    		   }    		   
    		       		   
    		   Object envRef = getTestCaseEnvironment(node);    		   
    		   String xslStylesheetEnvInpStr = null;
    		   
    		   if (envRef != null) {
    			   NodeList nodeList2 = docElem.getChildNodes();    			   
    			   for (int idx2 = 0; idx2 < nodeList2.getLength(); idx2++) {
    				   Node node2 = nodeList2.item(idx2);
    				   if (node2.getNodeType() == Node.ELEMENT_NODE) {
    					   Element elemNode = (Element)node2;
    					   if ("environment".equals(elemNode.getLocalName())) {
    						   String envName = elemNode.getAttribute(NAME_ATTR);
    						   if (envName.equals(envRef)) {
    							   Node envChildNode = elemNode.getFirstChild();
    							   while (envChildNode != null) {
    								   if (envChildNode.getNodeType() == Node.ELEMENT_NODE) {
    									   String xslStylesheetFileName = ((Element)envChildNode).getAttribute("file");    									      									  
    									   URI uri = new URI(m_xslTransformTestSetFilePath);
    									   uri = uri.resolve(xslStylesheetFileName);    						
    									   xslStylesheetEnvInpStr = getStringContentFromUrl(uri.toURL());    			    					  

    									   break;
    								   }

    								   envChildNode = envChildNode.getNextSibling();
    							   }

    							   if (xslStylesheetEnvInpStr != null) {
    								   break;
    							   }
    						   }
    					   }
    				   }
    			   }
    		   }
    		       		   
    		   Map<String,XObject> xslParamMap = new HashMap<String,XObject>();
    		   Element expectedResultElem = null;
    		   
    		   Node childNode = node.getFirstChild();
    		   while (childNode != null) {
    			   if (childNode.getNodeType() == Node.ELEMENT_NODE) {
    				   Element elemNode = (Element)childNode;
    				   if ("test".equals(elemNode.getLocalName())) {
    					   Node childNode2 = elemNode.getFirstChild();
    					   while (childNode2 != null) {
    						   if (childNode2.getNodeType() == Node.ELEMENT_NODE) {
    							   Element elemNode2 = (Element)childNode2;
    							   String elemLocalName = elemNode2.getLocalName();
    							   if ("stylesheet".equals(elemLocalName)) {
    								   String xslStylesheetFileName = ((Element)elemNode2).getAttribute("file");    									      									  
									   URI uri = new URI(m_xslTransformTestSetFilePath);
									   uri = uri.resolve(xslStylesheetFileName);    						
									   xslStylesheetEnvInpStr = getStringContentFromUrl(uri.toURL()); 
    							   }
    							   else if ("initial-template".equals(elemLocalName)) {
    								   m_initTemplateName = ((Element)elemNode2).getAttribute("name"); 
    							   }
                                   else if ("param".equals(elemLocalName)) {
    								   String paramNameStr = ((Element)elemNode2).getAttribute("name");
    								   String paramXPathStr = ((Element)elemNode2).getAttribute("select");    								   
    								   XPathContext xctxt = new XPathContext(false); 
    								   org.apache.xpath.XPath paramXPathObj = new org.apache.xpath.XPath(paramXPathStr, null, xctxt.getNamespaceContext(), 
    										                                                                           org.apache.xpath.XPath.SELECT, null);
    								   XObject xObj = paramXPathObj.execute(xctxt, DTM.NULL, null);    								   
    								   xslParamMap.put(paramNameStr, xObj);
    							   }
    						   }
    						   
    						   childNode2 = childNode2.getNextSibling();
    					   }
    				   }
    				   else if ("result".equals(elemNode.getLocalName())) {
    					   expectedResultElem = elemNode; 
    				   }
    			   }
    			   
    			   childNode = childNode.getNextSibling();
    		   }
    		   
               DOMSource xmlInpDomSource = null;
    		   
    		   if (xslStylesheetEnvInpStr != null) {
    			   byte[] byteArr = xslStylesheetEnvInpStr.getBytes(StandardCharsets.UTF_8);
    			   InputStream inpStream = new ByteArrayInputStream(byteArr);    		       		   
    			   xmlInpDomSource = new DOMSource(m_xmlDocumentBuilder.parse(inpStream));

    			   if (m_initTemplateName == null) {
    				   Transformer transformer = m_xslTransformerFactory.newTransformer(xmlInpDomSource);
    				   TransformerImpl transformerImpl = (TransformerImpl)transformer;
    				   StylesheetRoot stylesheetRoot = transformerImpl.getStylesheet();
    				   ElemTemplate xslInitialTemplate = stylesheetRoot.getTemplateComposed(new QName(Constants.S_XSLNAMESPACEURL, "initial-template"));
    				   if (xslInitialTemplate != null) {
    					  m_initTemplateName = "xsl:initial-template";
    				   }
    			   }
    		   }
    		   
               StreamSource xslStreamSrc = new StreamSource(new StringReader(xslStylesheetEnvInpStr), m_xslTransformTestSetFilePath);
    		   
    		   try {
    		      runW3CXSLTTestSuiteXslTransformAndEmitResult(testCaseName, xmlInpDomSource, xslStreamSrc, xslParamMap, 
    		    		                                                     expectedResultElem, elemTestRun, testResultDoc);
    		   }
    		   catch (Exception ex) {
    			  System.out.println("Test case name : " + testCaseName + ", Exception message : " + ex.getMessage()); 
    		   }
    		   finally {
    			  m_initTemplateName = null; 
    		   }
    	   }    	   
    	}
    	catch (Exception ex) {
    	   System.out.println(ex.getMessage());
    	}
    	finally {    	   	
    	   try {
    		   NodeList nodeList = testResultDoc.getElementsByTagName("testResult");
        	   
    		   int testsPassCount = 0;
        	   int testsfailCount = 0;
        	   int testsSkippedCount = 0;
        	   int testStatusUnknownCount = 0;
        	   
        	   for (int idx = 0; idx < nodeList.getLength(); idx++) {
        		  Element element = (Element)(nodeList.item(idx));
        		  String statusValue = element.getAttribute("status");
        		  if ("pass".equals(statusValue)) {
        			  testsPassCount++; 
        		  }
        		  else if ("fail".equals(statusValue)) {
        			  testsfailCount++; 
        		  }
        		  else if ("skipped".equals(statusValue)) {
        			  testsSkippedCount++; 
        		  }
        		  else {
        			  testStatusUnknownCount++; 
        		  }
        	   }
        	   
        	   int totalTestsRun = (testsPassCount + testsfailCount + testStatusUnknownCount);    
        	   
        	   elemTestRun.setAttribute("pass", String.valueOf(testsPassCount));
        	   elemTestRun.setAttribute("fail", String.valueOf(testsfailCount));
        	   elemTestRun.setAttribute("skipped", String.valueOf(testsSkippedCount));
        	   elemTestRun.setAttribute("statusUnknown", String.valueOf(testStatusUnknownCount));
        	   elemTestRun.setAttribute("totalRun", String.valueOf(totalTestsRun));
        	   
    		   // Serialize W3C XSLT 3.0 test set results file to file system
    		   String xslTestResultStr = serializeXmlDomElementNode(testResultDoc);
        	   
        	   File xslAnalyzeStringTestResultFile = new File(new URI(W3C_XSLT3_TESTS_RESULT_DIR_HOME + m_resultSubFolderName + "/" + m_testResultFileName));
        	   testResultFos = new FileOutputStream(xslAnalyzeStringTestResultFile);
        	   testResultFos.write(xslTestResultStr.getBytes());
        	   testResultFos.flush();
			   testResultFos.close();
		   } 
    	   catch (Exception ex) {
			   ex.printStackTrace();
		   }
    	}
    }
    
    /**
     * Method definition to run, a particular W3C XSLT 3.0 test case within a test set.
     */
    private void runW3CXSLTTestSuiteXslTransformAndEmitResult(String testCaseName, DOMSource xmlInpDomSource, 
    		                                                  StreamSource xslStreamSrc, Map<String, XObject> xslParamMap, 
    		                                                  Element expectedResultElem, Element elemTestRun, Document testResultDoc) throws Exception {    	    	

    	Element elemTestResult = testResultDoc.createElement("testResult");
    	
    	XslTestsErrorHandler xslTransformErrHandler = new XslTestsErrorHandler();
		List<String> trfErrorList = xslTransformErrHandler.getTrfErrorList();
		List<String> trfFatalErrorList = xslTransformErrHandler.getTrfFatalErrorList();
		
		String expErrCodeName = null;
		
		elemTestResult.setAttribute("testName", testCaseName);
    	
    	try {
    		m_xslTransformerFactory.setErrorListener(xslTransformErrHandler);
    		
    		if (m_initTemplateName != null) {
    		   m_xslTransformerFactory.setAttribute(XalanProperties.INIT_TEMPLATE, m_initTemplateName);
    		}    		    		
    		
    		Transformer transformer = m_xslTransformerFactory.newTransformer(xslStreamSrc);    		
    		if (xslParamMap.size() > 0) {
    			Set<String> keySet = xslParamMap.keySet();
    			Iterator<String> keyIter = keySet.iterator();
    			while (keyIter.hasNext()) {
    			   String key = keyIter.next();
    			   XObject value = xslParamMap.get(key);
    			   ((TransformerImpl)transformer).setParameter(key, value);
    			}
    		}
    		
    		Node nodeExpected = (expectedResultElem.getFirstChild()).getNextSibling();
    		String expectedNodeKindName = nodeExpected.getNodeName();
    		
    		if (EXPECTED_NODE_KIND_ERROR.equals(expectedNodeKindName)) {
    			expErrCodeName = ((Element)nodeExpected).getAttribute("code");
    		}
    		    		
    		StringWriter resultStrWriter = new StringWriter();    		    		    		
    		
    		if (transformer != null) {
    			transformer.setErrorListener(xslTransformErrHandler);
    		    setXslTransformProperties(transformer);
    		}
    		else {    		    		        		    
    		    if (EXPECTED_NODE_KIND_ERROR.equals(expectedNodeKindName)) {
        			handleExpectedXslTransformationError(testResultDoc, elemTestResult, trfErrorList, 
        					                             trfFatalErrorList, expErrCodeName, resultStrWriter);
        			
        			return;
        		}
    		    else if (!SERIALIZATION_MATCHES.equals(expectedNodeKindName)) {
    		    	elemTestResult.setAttribute("status", "fail");
    				
    				Element resultOutElem = testResultDoc.createElement("outResult");
    				resultOutElem.setTextContent(resultStrWriter.toString());
    				elemTestResult.appendChild(resultOutElem);
    				
    				return;
    		    }
    		}
    		
    		Source xmlInpSrc = null;
    		if ((m_initTemplateName != null) && (xmlInpDomSource == null)) {
    			xmlInpSrc = xslStreamSrc;
    			((TransformerImpl)transformer).setXMLSourceAbsent(true);
    		}
    		else {
    			xmlInpSrc = xmlInpDomSource; 
    		}
    		
    		transformer.transform(xmlInpSrc, new StreamResult(resultStrWriter));
    		
    		if (EXPECTED_NODE_KIND_ASSERT_ALL_OF.equals(expectedNodeKindName) || SERIALIZATION_MATCHES.equals(expectedNodeKindName) || 
    				                                                             ASSERT_SERIALIZATION.equals(expectedNodeKindName)) {    			
    			NodeList nodeList = ((Element)nodeExpected).getElementsByTagName(SERIALIZATION_MATCHES);
    			int nodeListLength = nodeList.getLength();
    			
    			String xslTransformMethod = transformer.getOutputProperty(OutputKeys.METHOD);
    			
    			if (SERIALIZATION_MATCHES.equals(expectedNodeKindName)) {
    				testCaseOneSerializationMatchCheck(elemTestResult, nodeExpected, resultStrWriter, xslTransformMethod);
    			}
    			else if (ASSERT_SERIALIZATION.equals(expectedNodeKindName)) {    				
    				String fileName = ((Element)nodeExpected).getAttribute("file");
    				String serMethod = ((Element)nodeExpected).getAttribute("method");    // assuming this to be "text"
    				String encoding = ((Element)nodeExpected).getAttribute("encoding");
    				int idx = m_xslTransformTestSetFilePath.lastIndexOf('/');
    				String fileUriStr = m_xslTransformTestSetFilePath.substring(0, idx) + '/' + fileName;    				
    				FileInputStream fileInpStr = new FileInputStream(new File(new URI(fileUriStr)));
    				byte[] byteArr = new byte[512];
    				int bytesRead = 0;
    				StringBuffer strBuff = new StringBuffer();
    				Charset charSet = null;
    				if (!"".equals(encoding)) {
    				   charSet = Charset.forName(encoding);
    				}
    				while ((bytesRead = fileInpStr.read(byteArr)) != -1) {
    				   String str1 = null;
    				   if (charSet != null) {
    				      str1 = new String(byteArr, 0, bytesRead, charSet);
    				   }
    				   else {
    					  str1 = new String(byteArr, 0, bytesRead);  
    				   }
    				   strBuff.append(str1);
    				}
    				
    				fileInpStr.close();
    				
    				String expectedResultStr = strBuff.toString();
    				if ((resultStrWriter.toString()).equals(expectedResultStr)) {
    					elemTestResult.setAttribute("status", "pass");
    				}
    				else {
    				    elemTestResult.setAttribute("status", "fail");
    				}    				
    			}
    			else if (nodeListLength > 0) {
    			    testCaseMultipleSerializationMatchChecks(elemTestResult, resultStrWriter, nodeList, xslTransformMethod);
    			}
    			else {
    				testCaseExpectedAssertXPathList(elemTestResult, nodeExpected, resultStrWriter);
    		    }
    		}
    		else if (EXPECTED_NODE_KIND_ASSERT_ANY_OF.equals(expectedNodeKindName)) {
    			Node childNode = nodeExpected.getFirstChild();
    			boolean isTestCasePass = false;
    			boolean isAssertXml = false;
    			while (childNode != null) {
    				if ((childNode instanceof Element) && EXPECTED_NODE_KIND_ASSERT_XML.equals(((Element)childNode).getNodeName())) {
    					isAssertXml = true;
    					String xmlStr1 = ((Element)childNode).getTextContent();
    					String xmlStr2 = resultStrWriter.toString();
    					int idx = xmlStr2.indexOf("?>");
    					if (idx != -1) {
    					   xmlStr2 = xmlStr2.substring(idx + 2);
    					}
    					if (isTwoXmlHtmlStrEqual(xmlStr1, xmlStr2)) {
    						elemTestResult.setAttribute("status", "pass");
    						isTestCasePass = true;
    						break;
    					}    					
    				}
    				else if ((childNode instanceof Element) && EXPECTED_NODE_KIND_ERROR.equals(((Element)childNode).getNodeName())) {
    					expErrCodeName = ((Element)childNode).getAttribute("code"); 
    					handleExpectedXslTransformationError(testResultDoc, elemTestResult, trfErrorList, 
				                                             trfFatalErrorList, expErrCodeName, resultStrWriter);
    				}
    				
    				childNode = childNode.getNextSibling();
    			}
    			
    			if (isAssertXml && !isTestCasePass && !"pass".equals(elemTestResult.getAttribute("status"))) {
    			   elemTestResult.setAttribute("status", "fail");
    			}
    		}
            else if (EXPECTED_NODE_KIND_ASSERT_XML.equals(expectedNodeKindName)) {
            	Element elemNode = (Element)nodeExpected;
            	String fileName = elemNode.getAttribute(FILE_ATTR);
            	String expectedResultStr = null;
            	if (!"".equals(fileName)) {
            		URI uri = new URI(m_xslTransformTestSetFilePath);
            		uri = uri.resolve(fileName);
            		expectedResultStr = getStringContentFromUrl(uri.toURL());
            	}
            	else {
            		expectedResultStr = elemNode.getTextContent();            		
            	}
            	
            	String ignorePrefixesStr = elemNode.getAttribute("ignore-prefixes");

            	Document xmlInpDoc1 = m_xmlDocumentBuilder.parse(new ByteArrayInputStream((resultStrWriter.toString()).getBytes()));            	
            	normalizeXmlDocumentText(xmlInpDoc1);
            	
            	TransformerFactory xslTransformerFactory = TransformerFactory.newInstance();
            	String xmlHtmlStr1 = null;
            	if ("true".equals(ignorePrefixesStr)) {
            		Transformer transformer2 = xslTransformerFactory.newTransformer(new StreamSource(XSL_TRANSFORM_NORMALIZE_NS_FILE_PATH));
            		StringWriter strWriter = new StringWriter();
            		transformer2.transform(new DOMSource(xmlInpDoc1), new StreamResult(strWriter));
            		xmlHtmlStr1 = strWriter.toString(); 
            	}
            	else {
            		xmlHtmlStr1 = serializeXmlDomElementNode(xmlInpDoc1);
            	}

            	Document xmlInpDoc2 = m_xmlDocumentBuilder.parse(new ByteArrayInputStream((expectedResultStr).getBytes()));
            	normalizeXmlDocumentText(xmlInpDoc2);
            	String xmlHtmlStr2 = null;
            	if ("true".equals(ignorePrefixesStr)) {
            		Transformer transformer2 = xslTransformerFactory.newTransformer(new StreamSource(XSL_TRANSFORM_NORMALIZE_NS_FILE_PATH));
            		StringWriter strWriter = new StringWriter();
            		transformer2.transform(new DOMSource(xmlInpDoc2), new StreamResult(strWriter));
            		xmlHtmlStr2 = strWriter.toString();
            	}
            	else {
            		xmlHtmlStr2 = serializeXmlDomElementNode(xmlInpDoc2);
            	}

            	if (isTwoXmlHtmlStrEqual(xmlHtmlStr1, xmlHtmlStr2)) {            		
            		elemTestResult.setAttribute("status", "pass");
            	}
            	else {
            		elemTestResult.setAttribute("status", "fail");
            	}
    		}
            else if (EXPECTED_NODE_KIND_ASSERT.equals(expectedNodeKindName)) {
            	Element elemNode = (Element)nodeExpected;
            	String fileName = elemNode.getAttribute(FILE_ATTR);
            	String expectedXPathStr = null;
            	if (!"".equals(fileName)) {
            		URI uri = new URI(m_xslTransformTestSetFilePath);
            		uri = uri.resolve(fileName);
            		expectedXPathStr = getStringContentFromUrl(uri.toURL());
            	}
            	else {
            		expectedXPathStr = elemNode.getTextContent();            		
            	}

            	Document xmlInpDoc1 = m_xmlDocumentBuilder.parse(new ByteArrayInputStream((resultStrWriter.toString()).getBytes()));
            	String xmlStr1 = serializeXmlDomElementNode(xmlInpDoc1);
            	
            	StringReader strReader = new StringReader(xmlStr1);             	            	
            	
            	if (XML11Char.isXML11ValidNCName(expectedXPathStr)) {
            		expectedXPathStr = "exists(" + expectedXPathStr + ")";
            	}
            	
            	System.setProperty(Constants.XML_DOCUMENT_BUILDER_FACTORY_KEY, Constants.XML_DOCUMENT_BUILDER_FACTORY_VALUE);            	            	
            	
            	DocumentBuilderFactory dBuilderFactory = DocumentBuilderFactory.newInstance();
            	dBuilderFactory.setNamespaceAware(true);
            	DocumentBuilder docBuilder = dBuilderFactory.newDocumentBuilder();
            	
            	Document document = docBuilder.parse(new InputSource(strReader));
            	
            	expectedXPathStr = expectedXPathStr.replace('\'', '"');
            	
            	String xslStylesheetStr = "<?xml version=\"1.0\"?>" +
					            			"<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" version=\"3.0\">" +
					            			"  <xsl:output method=\"text\"/>" +
					            			"  <xsl:template match=\"/\">" +
					            			"     <xsl:value-of select='" + expectedXPathStr + "'/>" +
					            			"  </xsl:template>" +
					            			"</xsl:stylesheet>";            	
            	
            	Document xslDocument = docBuilder.parse(new InputSource(new StringReader(xslStylesheetStr)));
            	
            	System.setProperty(XSLTestConstants.XSLT_TRANSFORMER_FACTORY_KEY, XSLTestConstants.XSLT_TRANSFORMER_FACTORY_VALUE);
                
            	TransformerFactory trfFactory2 = TransformerFactory.newInstance();
            	
            	Transformer transformer2 = trfFactory2.newTransformer(new DOMSource(xslDocument));
            	resultStrWriter = new StringWriter();
            	transformer2.transform(new DOMSource(document), new StreamResult(resultStrWriter));

            	String str1 = resultStrWriter.toString();
            	
            	if (TRUE.equals(str1)) {            		
            		elemTestResult.setAttribute("status", "pass");
            	}
            	else {
            		elemTestResult.setAttribute("status", "fail");
            	}
    		}
            else if (EXPECTED_NODE_KIND_ERROR.equals(expectedNodeKindName)) {
            	handleExpectedXslTransformationError(testResultDoc, elemTestResult, trfErrorList, 
                        							                trfFatalErrorList, expErrCodeName, resultStrWriter);
            }
    	}
    	catch (SAXException ex) {
    		handleTestCaseFailException(testResultDoc, trfErrorList, trfFatalErrorList, 
    				                                                                 elemTestResult, expErrCodeName);
    	}
    	catch (Exception ex) {    		
            handleTestCaseFailException(testResultDoc, trfErrorList, trfFatalErrorList, 
            		                                                                 elemTestResult, expErrCodeName);    	
    	}
    	finally {
    		elemTestRun.appendChild(elemTestResult);    		
    		trfErrorList.clear();
    		trfFatalErrorList.clear();
    		if (m_initTemplateName != null) {
     		   m_xslTransformerFactory.setAttribute(XalanProperties.INIT_TEMPLATE, null);
     		   m_initTemplateName = null;
     		}
    	}    	    	
    }

    /**
     * An W3C XSL test case implementation, when expected output is available as 
     * one or more XPath expression strings that need to verify test case's actual
     * output. 
     */
	private void testCaseExpectedAssertXPathList(Element elemTestResult, Node nodeExpected, StringWriter resultStrWriter)
																						throws IOException, URISyntaxException, 
																						       SAXException, TransformerConfigurationException,
																						       TransformerException, Exception {
		
		byte[] fileBytes = Files.readAllBytes(Paths.get(new URI(XSL_TRANSFORM_TEST_ALL_OF_TEMPLATE_FILE_PATH)));
		
		String verificationXslTemplateStr = new String(fileBytes);
		NodeList nodeList = nodeExpected.getChildNodes();
		StringBuffer replacementStrBuff = new StringBuffer();
		StringBuffer expectedResultStrBuff = new StringBuffer();
		expectedResultStrBuff.append("<result>");
		for (int idx = 0; idx < nodeList.getLength(); idx++) {
			Node node = nodeList.item(idx);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				String assertStr = ((Element)node).getTextContent();    					
				if (assertStr.contains("'")) {
					assertStr = assertStr.replace("'", "\"");
				}
				String strValue = "<xpath><xsl:value-of select='" + assertStr + "'/></xpath>\n";

				replacementStrBuff.append(strValue);

				expectedResultStrBuff.append("<xpath>true</xpath>\n");
			}
		}

		String verificationXslStylesheetStr = verificationXslTemplateStr.replace("{{XPATH_ASSERT_LIST}}", replacementStrBuff.toString());

		NamedNodeMap attrNamedNodeMap = nodeExpected.getAttributes();
		int attrCount = attrNamedNodeMap.getLength();
		StringBuffer attrDeclstrBuff = new StringBuffer();
		for (int idx = 0; idx < attrCount; idx++) {
			Node attrNode = attrNamedNodeMap.item(idx);
			String attrName = attrNode.getNodeName();
			String attrValue = attrNode.getNodeValue();
			attrDeclstrBuff.append(attrName + "=\"" + attrValue + "\"");
		}

		verificationXslStylesheetStr = verificationXslStylesheetStr.replace("{{NS_DECL}}", attrDeclstrBuff.toString());

		expectedResultStrBuff.append("</result>");

		Document expectedResultDoc = m_xmlDocumentBuilder.parse(new ByteArrayInputStream((expectedResultStrBuff.toString()).getBytes()));    			    			

		Document verificationXslDoc = m_xmlDocumentBuilder.parse(new ByteArrayInputStream(verificationXslStylesheetStr.getBytes()));
		
		Object xslInitTemplateVal = m_xslTransformerFactory.getAttribute(XalanProperties.INIT_TEMPLATE);
		m_xslTransformerFactory.setAttribute(XalanProperties.INIT_TEMPLATE, null);

		Transformer transformer = m_xslTransformerFactory.newTransformer(new DOMSource(verificationXslDoc));
		
		m_xslTransformerFactory.setAttribute(XalanProperties.INIT_TEMPLATE, xslInitTemplateVal);

		StringWriter strWriter = new StringWriter();
		
		Document documentToBeTransformed = m_xmlDocumentBuilder.parse(new ByteArrayInputStream((resultStrWriter.toString()).getBytes()));
		
		XslTransformData.m_is_xsl_test_invocation = true;
		try {
		   transformer.transform(new DOMSource(documentToBeTransformed), new StreamResult(strWriter));
		}
		finally {
		   XslTransformData.m_is_xsl_test_invocation = false;
		}
		
		String str2 = strWriter.toString();

		Document xmlInpDoc1 = m_xmlDocumentBuilder.parse(new ByteArrayInputStream(str2.getBytes()));
		
		String xmlStr1 = serializeXmlDomElementNode(xmlInpDoc1);
		
		String xmlStr2 = serializeXmlDomElementNode(expectedResultDoc);

		if (isTwoXmlHtmlStrEqual(xmlStr1, xmlStr2)) {
			elemTestResult.setAttribute("status", "pass");
		}
		else {
			elemTestResult.setAttribute("status", "fail");
		}
	}

	/**
     * An W3C XSL test case implementation, when test case's expected output is 
     * available as more than one XML serialization-matches elements that require 
     * doing checks with regex.  
     */
	private void testCaseMultipleSerializationMatchChecks(Element elemTestResult, StringWriter resultStrWriter, 
			                                              NodeList nodeList, String xslTransformMethod)
			                                                                                     throws SAXException, IOException, 
			                                                                                            TransformerException, Exception {
		
		List<XslSerializationMatchesMetaData> serMatchesMetaDataList = new ArrayList<XslSerializationMatchesMetaData>();
		
		for (int idx = 0; idx < nodeList.getLength(); idx++) {
			Element elemNode = (Element)(nodeList.item(idx));
			String txtContextStr = elemNode.getTextContent();
			String[] strArr = txtContextStr.split("=");    		
			XslSerializationMatchesMetaData serMatchesMetaData = null;
			if (strArr.length > 1) {
				serMatchesMetaData = new XslSerializationMatchesMetaData(strArr, null);  
			}
			else {
				serMatchesMetaData = new XslSerializationMatchesMetaData(null, txtContextStr);
			}

			serMatchesMetaDataList.add(serMatchesMetaData);    				  
		}
		
		String xslTransformResultStr = resultStrWriter.toString();		
		if ((org.apache.xml.serializer.Method.HTML).equals(xslTransformMethod)) {
			xslTransformResultStr = resultStrWriter.toString();			
			xslTransformResultStr = xslTransformResultStr.replace("<META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">", "");
		}

		Document xmlResultDoc = m_xmlDocumentBuilder.parse(new ByteArrayInputStream(xslTransformResultStr.getBytes()));

		List<Element> elemNodeList = new ArrayList<Element>();
		elemNodeList.add(xmlResultDoc.getDocumentElement());

		// Get list of all XML element nodes within a DOM object
		getXmlDomElemNodes(xmlResultDoc.getDocumentElement(), elemNodeList);

		boolean isTestCasePass = false;

		for (int idx = 0; idx < elemNodeList.size(); idx++) {
			Element elemNode = elemNodeList.get(idx);
			String elemNodeStrValue = null; 
			if ((org.apache.xml.serializer.Method.HTML).equals(xslTransformMethod)) {
				// Handling only HTML tag <a> for now in this test suite driver
				if ("a".equals(elemNode.getNodeName())) {
					elemNodeStrValue = elemNode.getTextContent();					    
				}
				else {										
					continue;
				}
			}
			else if ((elemNode.getFirstChild() != null) && ((Node)(elemNode.getFirstChild()).getFirstChild() != null)) {
				Node node = elemNode.getFirstChild();
				node = node.getFirstChild();
				elemNodeStrValue = serializeXmlDomElementNode(node);

				int i = elemNodeStrValue.indexOf("?>");
				if (i > -1) {
					elemNodeStrValue = (elemNodeStrValue.substring(i + 2)).trim();
				}
			}
			else {
				elemNodeStrValue = elemNode.getTextContent(); 
			}

			List<Boolean> boolList = new ArrayList<Boolean>();
			for (int idx2 = 0; idx2 < serMatchesMetaDataList.size(); idx2++) {
				XslSerializationMatchesMetaData xslSerializationMatchesMetaData = serMatchesMetaDataList.get(idx2);
				String[] strArray = xslSerializationMatchesMetaData.getStrArr();
				String strValue = xslSerializationMatchesMetaData.getStrValue();
				if (strValue != null) {
					if ((org.apache.xml.serializer.Method.HTML).equals(xslTransformMethod)) {
					   if (strValue.startsWith(">")) {
						   strValue = strValue.substring(1); 
					   }
					}
					
					Pattern pattern = Pattern.compile(strValue);   							 
					Matcher matcher = pattern.matcher(elemNodeStrValue);   							 
					if (strValue.equals(elemNodeStrValue) || matcher.matches()) {
						boolList.add(Boolean.valueOf(true));
					}
				}
				else {
					String attrName = strArray[0];
					String attrValue = strArray[1];   							  
					Pattern pattern1 = Pattern.compile(attrName);   							   							  
					Pattern pattern2 = Pattern.compile(attrValue);

					NamedNodeMap namedNodeMap = null;
					if ((elemNode.getFirstChild() != null) && ((Node)(elemNode.getFirstChild()).getFirstChild() != null)) {
						elemNode = (Element)(elemNode.getFirstChild());
						namedNodeMap = elemNode.getAttributes();
					}
					else {
						namedNodeMap = elemNode.getAttributes();
					}

					for (int idx3 = 0; idx3 < namedNodeMap.getLength(); idx3++) {
						Node attrNode = namedNodeMap.item(idx3);
						String atrName2 = attrNode.getNodeName();
						String attrValue2 = attrNode.getNodeValue();
						attrValue2 = "'" + attrValue2 + "'";  
						Matcher matcher1 = pattern1.matcher(atrName2);
						Matcher matcher2 = pattern2.matcher(attrValue2);
						if (matcher1.matches() && matcher2.matches()) {
							boolList.add(Boolean.valueOf(true));
						}
						else if (attrName.equals(atrName2) && attrValue.equals(attrValue2)) {
							boolList.add(Boolean.valueOf(true)); 
						}
						else if (attrName.equals(atrName2) && matcher2.matches()) {
							boolList.add(Boolean.valueOf(true));
						}
						else if (attrValue.equals(attrValue2) && matcher1.matches()) {
							boolList.add(Boolean.valueOf(true));
						}
					}
				}
			}

			if ((boolList.size() > 0) && (boolList.size() == serMatchesMetaDataList.size())) {
				isTestCasePass = true;

				break;
			}
		}

		if (isTestCasePass) {
			elemTestResult.setAttribute("status", "pass");
		}
		else {
			elemTestResult.setAttribute("status", "fail");
		}
	}

	/**
     * An W3C XSL test case implementation, when test case's expected output is 
     * available as one XML serialization-matches element that require doing a 
     * check with regex.  
     */
	private void testCaseOneSerializationMatchCheck(Element elemTestResult, Node nodeExpected,
												    StringWriter resultStrWriter, String xslTransformMethod) 
												    		                          throws SAXException, IOException, 
	                                                                                         TransformerException, Exception {
		Element elemNode = (Element)nodeExpected;
		String txtContextStr = elemNode.getTextContent();
		String[] strArr = txtContextStr.split("=");    		
		XslSerializationMatchesMetaData serMatchesMetaData = null;
		if (strArr.length > 1) {
			serMatchesMetaData = new XslSerializationMatchesMetaData(strArr, null);  
		}
		else {
			serMatchesMetaData = new XslSerializationMatchesMetaData(null, txtContextStr);
		}

		boolean isTestCasePass = false;
		
		String xslTransformResultStr = resultStrWriter.toString();		
		if ((org.apache.xml.serializer.Method.HTML).equals(xslTransformMethod)) {
			xslTransformResultStr = resultStrWriter.toString();			
			xslTransformResultStr = xslTransformResultStr.replace("<META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">", "");
		}
		
		if (!(org.apache.xml.serializer.Method.TEXT).equals(xslTransformMethod)) {
	    	Document xmlResultDoc = m_xmlDocumentBuilder.parse(new ByteArrayInputStream(xslTransformResultStr.getBytes()));

	    	List<Element> elemNodeList = new ArrayList<Element>();
	    	elemNodeList.add(xmlResultDoc.getDocumentElement());

	    	// Get list of all XML element nodes within DOM object xmlResultDoc
	    	getXmlDomElemNodes(xmlResultDoc.getDocumentElement(), elemNodeList);		

	    	for (int idx = 0; idx < elemNodeList.size(); idx++) {
	    		elemNode = elemNodeList.get(idx);
	    		String elemNodeStrValue = null;
	    		String elemNodeStrValue2 = null; 
	    		if ((elemNode.getFirstChild() != null) && ((Node)(elemNode.getFirstChild()).getFirstChild() != null)) {
	    			Node node = elemNode.getFirstChild();
	    			node = node.getFirstChild();
	    			elemNodeStrValue = serializeXmlDomElementNode(node);
	    			int i = elemNodeStrValue.indexOf("?>");
	    			if (i > -1) {
	    				elemNodeStrValue = (elemNodeStrValue.substring(i + 2)).trim();
	    			}
	    		}
	    		else {
	    			elemNodeStrValue = elemNode.getTextContent();
	    			elemNodeStrValue2 = serializeXmlDomElementNode(elemNode);
	    			int i = elemNodeStrValue2.indexOf("?>");
	    			if (i > -1) {
	    				elemNodeStrValue2 = (elemNodeStrValue2.substring(i + 2)).trim();
	    			}
	    		}

	    		String[] strArray = serMatchesMetaData.getStrArr();
	    		String strValue = serMatchesMetaData.getStrValue();
	    		if (strValue != null) {
	    			Pattern pattern = Pattern.compile(strValue);   							 
	    			Matcher matcher = pattern.matcher(elemNodeStrValue);
	    			Matcher matcher2 = pattern.matcher(elemNodeStrValue2);  
	    			if (strValue.equals(elemNodeStrValue) || matcher.matches()) {
	    				isTestCasePass = true;
	    			}
	    			else if (strValue.equals(elemNodeStrValue2) || matcher2.matches()) {
	    				isTestCasePass = true; 
	    			}
	    		}
	    		else {
	    			String attrName = strArray[0];
	    			String attrValue = strArray[1];   							  
	    			Pattern pattern1 = Pattern.compile(attrName);   							   							  
	    			Pattern pattern2 = Pattern.compile(attrValue);

	    			NamedNodeMap namedNodeMap = null;
	    			if ((elemNode.getFirstChild() != null) && ((Node)(elemNode.getFirstChild()).getFirstChild() != null)) {
	    				elemNode = (Element)(elemNode.getFirstChild());
	    				namedNodeMap = elemNode.getAttributes();
	    			}
	    			else {
	    				namedNodeMap = elemNode.getAttributes();
	    			}

	    			for (int idx3 = 0; idx3 < namedNodeMap.getLength(); idx3++) {
	    				Node attrNode = namedNodeMap.item(idx3);
	    				String atrName2 = attrNode.getNodeName();
	    				String attrValue2 = attrNode.getNodeValue();
	    				attrValue2 = "'" + attrValue2 + "'";  
	    				Matcher matcher1 = pattern1.matcher(atrName2);
	    				Matcher matcher2 = pattern2.matcher(attrValue2);
	    				if (matcher1.matches() && matcher2.matches()) {
	    					isTestCasePass = true;
	    				}
	    				else if (attrName.equals(atrName2) && attrValue.equals(attrValue2)) {
	    					isTestCasePass = true; 
	    				}
	    				else if (attrName.equals(atrName2) && matcher2.matches()) {
	    					isTestCasePass = true;
	    				}
	    				else if (attrValue.equals(attrValue2) && matcher1.matches()) {
	    					isTestCasePass = true;
	    				}
	    			}
	    		}

	    		if (isTestCasePass) {
	    			break;
	    		}
	    	}
	    }
	    else {
	    	String expectedResultRegexStr = serMatchesMetaData.getStrValue();
	    	
	    	Pattern pattern = null;
	    	if (expectedResultRegexStr != null) {
	    	    pattern = Pattern.compile(expectedResultRegexStr);
	    	}
	    	else {	    	    
	    	    strArr = serMatchesMetaData.getStrArr();
	    		expectedResultRegexStr = strArr[0] + "=" + strArr[1];
	    		expectedResultRegexStr = expectedResultRegexStr.replace("\\", "");
	    		pattern = Pattern.compile(expectedResultRegexStr, Pattern.LITERAL);
	    	}
	    	
			Matcher matcher = pattern.matcher(xslTransformResultStr);
			if (matcher.matches()) {
			   isTestCasePass = true;
			}
	    }

		if (isTestCasePass) {
			elemTestResult.setAttribute("status", "pass");
		}
		else {
			elemTestResult.setAttribute("status", "fail");
		}
	}
    
    /**
     * Method definition to get, a list of all XML element nodes within 
     * the supplied XML element node, including the supplied node as well.
     * 
     * @param elemNode			   An XML supplied element node
     * @param result               A list object to contain information produced
     *                             by this method definition, which the caller
     *                             of this method uses to access this information.
     * @return                     
     */
    private void getXmlDomElemNodes(Element elemNode, List<Element> result) {
    	
    	NodeList nodeList = elemNode.getChildNodes();
    	for (int idx = 0; idx < nodeList.getLength(); idx++) {
    	    Node node = nodeList.item(idx);
    	    if (node.getNodeType() == Node.ELEMENT_NODE) {
    	    	result.add((Element)node);
    	    	getXmlDomElemNodes((Element)node, result);
    	    }
    	}
	}

	/**
     * Given an XSL tests with an XML element local name "environment", 
     * find an XML document input string from the supplied element 
     * node.
     * 
     * @param envElemNode           An XML element DOM node with local name 
     *                              "environment".
     * @return                      XML document string corresponding to the 
     *                              supplied element node.
     */
    private String getXMLInputDocStr(Element envElemNode) {
		
    	String result = null;
		
		NodeList nodeList = envElemNode.getChildNodes();
    	for (int idx = 0; idx < nodeList.getLength(); idx++) {
    		Node node = nodeList.item(idx);
    		if (node.getNodeType() == Node.ELEMENT_NODE) {
    			Element elemNode = (Element)node;
    			if ("source".equals(elemNode.getLocalName())) {
    			    String fileName = elemNode.getAttribute(FILE_ATTR);    			       			    
    				if ("".equals(fileName)) {
    					NodeList nodeList2 = elemNode.getChildNodes();
    					for (int idx2 = 0; idx2 < nodeList2.getLength(); idx2++) {
    						Node node2 = nodeList2.item(idx2);
    						if (node2.getNodeType() == Node.ELEMENT_NODE) {
    							Element elemNode2 = (Element)node2;
    							if ("content".equals(elemNode2.getLocalName())) {
    								result = elemNode2.getTextContent();
    							}
    						}
    					}	    					
    				}
    				else {    					    					    					
    					try {
    						URI uri = new URI(m_xslTransformTestSetFilePath);
    						uri = uri.resolve(fileName);    						
							result = getStringContentFromUrl(uri.toURL());
						} catch (Exception ex) {
							ex.printStackTrace();
						}
    				}
    				
    				break;
    			}
    		}
    	}
		
		return result;
	}

	/**
     * Given an XML element with local name "test-case", find name of 
     * its environment ref (which gives access to an XML input document 
     * to be transformed by an XSL stylesheet).
     * 
     * @param testCaseNode			An XML element DOM node with local name 
     *                              "test-case".
     * @return						Name of environment ref for an XSL test-case 
     * 								node, or XML element object instance 'environment' if 
     *                              XML element 'environment's attribute 'ref' doesn't 
     *                              exist.
     */
    private Object getTestCaseEnvironment(Node testCaseNode) {
    	
    	Object result = null;
    	
    	NodeList nodeList = testCaseNode.getChildNodes();
    	for (int idx = 0; idx < nodeList.getLength(); idx++) {
    		Node node = nodeList.item(idx);
    		if (node.getNodeType() == Node.ELEMENT_NODE) {
    			Element elemNode = (Element)node;
    			if ("environment".equals(elemNode.getLocalName())) {
    				Attr attrNode = elemNode.getAttributeNode("ref");
    				if (attrNode != null) {
    					String envRefName = attrNode.getValue();
    				    result = envRefName;
    			    }
    			    else {
    			    	result = elemNode;
    			    }
    				
    				break;
    			}
    		}
    	}
    	
    	return result;
    }
    
    /**
     * Method definition to get, an ISO formatted date string for the supplied 
     * java.util.Date value.
     *  
     * @param date				The supplied java.util.Date object value
     * @return					The formatted date value string
     */
    private String getDateISOString(Date dateValue) {
    	String result = null;
    	
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        
        result = sdf.format(dateValue); 
        
        return result;
    }
    
    /**
     * Method definition to handle, an XSL transformation expected error.
     * 
     * @param testResultDoc
     * @param elemTestResult
     * @param trfErrorList
     * @param trfFatalErrorList
     * @param expErrCodeName
     * @param resultStrWriter
     */
	private void handleExpectedXslTransformationError(Document testResultDoc, Element elemTestResult, List<String> trfErrorList, 
			                                          List<String> trfFatalErrorList, String expErrCodeName, StringWriter resultStrWriter) {
		
		if ((trfErrorList.size() > 0) || (trfFatalErrorList.size() > 0)) {
			boolean isXslTransformErrorOk = false;
			for (int idx = 0; idx < trfErrorList.size(); idx++) {
				String errInfo = trfErrorList.get(idx);
				if (errInfo.contains(expErrCodeName)) {
					isXslTransformErrorOk = true;    						
					break;
				}
			}
			if (!isXslTransformErrorOk) {
				for (int idx = 0; idx < trfFatalErrorList.size(); idx++) {
					String errInfo = trfFatalErrorList.get(idx);
					if (errInfo.contains(expErrCodeName)) {
						isXslTransformErrorOk = true;
						break;
					}
				}
			}
			
			if (isXslTransformErrorOk) {
				elemTestResult.setAttribute("status", "pass");
			}
			else {
				handleTestCaseFailException(testResultDoc, trfErrorList, trfFatalErrorList, 
						                                                               elemTestResult, expErrCodeName);
			}
		}
		else {
			// An XSL transformation did not result in a dynamic error.
			// i.e, this test case has failed.
			elemTestResult.setAttribute("status", "fail");
			
			Element resultOutElem = testResultDoc.createElement("outResult");
			resultOutElem.setTextContent(resultStrWriter.toString());
			elemTestResult.appendChild(resultOutElem);
		}
	}
	
	/**
     * Method definition, to normalize an XML document content
     * when an XML document's top most element contains only text 
     * node children.
     * 
     * @param document					 The supplied XML document
     */
	private void normalizeXmlDocumentText(Document document) {
		Element docElem = document.getDocumentElement();
		StringBuffer strBuff = new StringBuffer();
		NodeList nodeList = docElem.getChildNodes();
		boolean isAllTextNode = true;
		for (int i = 0; i < nodeList.getLength(); i++) {
		    Node node = nodeList.item(i);
		    if (node.getNodeType() == Node.TEXT_NODE) {
		    	strBuff.append(node.getTextContent());
		    }
		    else {
		    	isAllTextNode = false;
		    	break;
		    }
		}
		
		if (isAllTextNode) {
		    String normalizedTextNodeValue = (strBuff.toString()).trim();
		    for (int i = 0; i < nodeList.getLength(); i++) {
		    	Node node = nodeList.item(i);
		    	docElem.removeChild(node);            	    	
		    }
		    
		    Text textNode = document.createTextNode(normalizedTextNodeValue);
		    docElem.appendChild(textNode);
		}
	}
	
	/**
	 * An object of this class, contains information for XSL test case's expected 
	 * run-time result, when the test case specifies expected XSL transformation 
	 * result using an XML meta-data tag "serialization-matches".
	 */
	public class XslSerializationMatchesMetaData {
		
		// One of the variable m_strArr or m_strValue will be null, 
		// whereas the other will be non-null.
		
		// String array information to assert on an XML 
		// attribute name and value.
		private String[] m_strArr = null;
		
		// String value to assert on an XML element node's text context
		private String m_strValue = null;
				
		/**
		 * Class constructor.
		 */
		public XslSerializationMatchesMetaData(String[] arr1, String strValue) {
			this.m_strArr = arr1;
			this.m_strValue = strValue;
		}

		public String[] getStrArr() {
			return m_strArr;
		}

		public void setStrArr(String[] arr) {
			this.m_strArr = arr;
		}

		public String getStrValue() {
			return m_strValue;
		}

		public void setStrValue(String strValue) {
			this.m_strValue = strValue;
		}
	}

}
