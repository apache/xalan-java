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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;

import org.apache.xalan.tests.util.XslTransformTestsUtil;
import org.apache.xalan.xslt.util.StringUtil;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.utils.Constants;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.PrefixResolverDefault;
import org.apache.xml.utils.QName;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.FuncDeepEqual;
import org.apache.xpath.functions.FuncEmpty;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;
import org.apache.xpath.operations.VcEquals;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSString;

/**
 * Xalan-J XSL 3 test base class, to support Xalan-J W3C 
 * XPath 3.1 test suite driver.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class W3CXPath3TestsUtil extends XslTransformTestsUtil { 	
                    
    public static final String TESTRUN = "testrun";
    
    public static final String NAME = "name";
    
    public static final String DATETIME = "dateTime";
    
    public static final String TESTCASE = "test-case";
    
    public static final String ENVIRONMENT = "environment";
    
    public static final String REF = "ref";
    
    public static final String EMPTY = "empty";
    
    public static final String SOURCE = "source";
    
    public static final String FILE = "file";
    
    public static final String DEPENDENCY = "dependency";
    
    public static final String TYPE = "type";
    
    public static final String SPEC = "spec";
    
    public static final String VALUE = "value";
    
    public static final String TESTRESULT = "testResult";
    
    public static final String TESTNAME = "testName";
    
    public static final String TEST = "test";
    
    public static final String STATUS = "status";
    
    public static final String PASS = "pass";
    
    public static final String FAIL = "fail";
    
    public static final String ASSERT_TRUE = "assert-true";
    
    public static final String ASSERT_FALSE = "assert-false";
    
    public static final String ASSERT_TYPE = "assert-type";
    
    public static final String ALL_OF = "all-of";
    
    public static final String ANY_OF = "any-of";
    
    public static final String ERROR = "error";
    
    public static final String SKIPPED = "skipped";
    
    public static final String ASSERT_DEEP_EQ = "assert-deep-eq";
    
    public static final String ASSERT_EQ = "assert-eq";
    
    public static final String ASSERT = "assert";
    
    public static final String ASSERT_COUNT = "assert-count";
    
    public static final String ASSERT_STRING_VALUE = "assert-string-value";
    
    public static final String ASSERT_EMPTY = "assert-empty";
    
    public static final String ASSERT_PERMUTATION = "assert-permutation";
    
    public static final String ASSERT_XML = "assert-xml";
    
    public static final String STATUS_UNKNOWN = "statusUnknown";
    
    public static final String RUN = "run";
    
    public static final String NAMESPACE = "namespace";
    
    public static final String PREFIX = "prefix";
    
    public static final String URI = "uri";
    
    public static final String SUCCESS = "success";
    
    public static final String DESC = "desc";
    
    public static final String XSLT_PROCESSOR = "xslt_processor";
    
    public static final String W3C_XPATH3_TEST_SUITE_RESULTS = "W3C XPath 3.1 test suite results";
    
    public static final String XSLT_PROC_NAME = "Apache Xalan-J XSLT 3.0 development code";
    
    protected static final String W3C_XPATH3_TESTS_META_DATA_DIR_HOME = "file:/d:/qt3tests-master/";
	
	protected static final String W3C_XPATH3_TESTS_CATALOG_FILE_PATH = W3C_XPATH3_TESTS_META_DATA_DIR_HOME + "catalog.xml";
	
	protected static final String W3C_XPATH3_TESTS_RESULT_DIR_HOME = "file:/d:/eclipseWorkspaces/xalanj/xalan-j_xslt3.0_mvn/src/test/java/org/apache/xalan/tests/w3c/xpath3/result/";
			
	/**
	 * Class field representing, an XML DocumentBuilderFactory object instance 
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
            
    protected static String m_resultSubFolderName = null;
    
    protected static String m_testResultFileName = null;
        
    protected static String m_xsl_test_set_base_dir = null;
	
	protected static String[] m_test_set_fileArr = null;
	
	protected static String[] m_test_set_result_fileArr = null;
	
	protected static List<String> m_skipped_tests_list = new ArrayList<String>();
	
	private Document m_xslTestCatalogDocument = null;
    
	
    /**
     * Class constructor.
     */
    public W3CXPath3TestsUtil() {    	    	    	
    	System.setProperty(Constants.XML_DOCUMENT_BUILDER_FACTORY_KEY, Constants.XML_DOCUMENT_BUILDER_FACTORY_VALUE);
    	
    	m_xmlDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
    	m_xmlDocumentBuilderFactory.setNamespaceAware(true);
    	try {
    	   m_xmlDocumentBuilder = m_xmlDocumentBuilderFactory.newDocumentBuilder();
    	   
    	   m_xslTestCatalogDocument = m_xmlDocumentBuilder.parse(W3C_XPATH3_TESTS_CATALOG_FILE_PATH);
    	}
    	catch (Exception ex) {
    	   // no op
    	}
    }
    
    /**
     * Method definition, to run an XPath 3.1 test set and produce
     * XSL test run report.
     */
    protected void runXPathTestSetAndProduceResult() {
    	
    	Document document = null;
    	
    	try {
    		document = m_xmlDocumentBuilder.parse(m_xslTransformTestSetFilePath);
    	} 
    	catch (Exception ex) {
            // no op
    	}
    	
        Element elem1 = document.getDocumentElement();
		
		Element catalogDocElem1 = m_xslTestCatalogDocument.getDocumentElement();    	
    	String testSetName = elem1.getAttribute(NAME);
    	
    	Document testResultDoc = m_xmlDocumentBuilder.newDocument();
    	
		Element elemTestRun = testResultDoc.createElement(TESTRUN);
		String testRunDateStrValue = getDateISOString(new Date());
		elemTestRun.setAttribute(NAME, testSetName);
		elemTestRun.setAttribute(DATETIME, testRunDateStrValue);
		testResultDoc.appendChild(elemTestRun);
    	
		Element docElem1 = document.getDocumentElement();
		
		Node node = docElem1.getFirstChild();		
		while (node != null) {
			Element elemTestResult = null;
			try {    		
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element testCaseElem = (Element)node;
					String nodeName = testCaseElem.getNodeName();
					String expectedErrCode = null;
					String runTimeErrCode = null;
					if (TESTCASE.equals(nodeName)) {						    					
						String testCaseNameStr = testCaseElem.getAttribute(NAME);												
						NodeList envNodeList = testCaseElem.getElementsByTagName(ENVIRONMENT);												
												
						XPathContext xctxt = new XPathContext(true);
						xctxt.setIncremental(false);
					    (xctxt.getDTMManager()).setIncremental(false);
						xctxt.setSource_location(false);
						(xctxt.getDTMManager()).setSource_location(false);
						
						PrefixResolver xmlNsPrefixResolver = null;						
                        String envName = null;                        
                        boolean isxmlNsContextConfigured = false;
                        
                        Map<String, String> roleFileNameMap1 = new HashMap<String, String>();
                        
                        boolean resolveWithCatalog = false;
						
						if (envNodeList.getLength() > 0) {
							Element elem = (Element)(envNodeList.item(0));
							envName = elem.getAttribute(REF);
							if ((envName != null) && !"".equals(envName) && !EMPTY.equals(envName)) {																	
								Node child = docElem1.getFirstChild();
								boolean isEnvNodeResolved = false;
								while (child != null) {
									String envName2 = null;
									Element elem2 = null;
									if (child.getNodeType() == Node.ELEMENT_NODE) {
										elem2 = (Element)child;
										String nodeName2 = elem2.getNodeName();
										if (ENVIRONMENT.equals(nodeName2)) {
											envName2 = elem2.getAttribute(NAME); 
										}
										else {
											child = child.getNextSibling();

											continue;
										}
									}
									else {
										child = child.getNextSibling();

										continue;
									}

									if (envName.equals(envName2)) {										
										NodeList nodeList1 = elem2.getElementsByTagName(SOURCE);
										int size1 = nodeList1.getLength();
										if (size1 > 0) {
											for (int idx = 0; idx < size1; idx++) {
												Element elem3 = (Element)(nodeList1.item(idx)); 
												String srcFileName = elem3.getAttribute(FILE);									 
												if (!"".equals(srcFileName)) {
													Node node1 = elem2.getFirstChild();
													Map<String, String> nsMap = new HashMap<String, String>();
													while (node1 != null) {
														if (node1.getNodeType() == Node.ELEMENT_NODE) {
															Element el1 = (Element)node1;
															if (NAMESPACE.equals(el1.getNodeName())) {
																String prefix = el1.getAttribute(PREFIX);
																String uri = el1.getAttribute(URI);
																nsMap.put(prefix, uri);
															}
														}

														node1 = node1.getNextSibling();
													}

													xmlNsPrefixResolver = getXMLNsPrefixResolver(nsMap);
													xctxt.setNamespaceContext(xmlNsPrefixResolver);

													isxmlNsContextConfigured = true;

													String envFileRoleNameStr = elem3.getAttribute("role");                                            	
													if (".".equals(envFileRoleNameStr)) {
														constructXalanDtmFromXMLFile(srcFileName, xctxt, false);
													}
													else {
														roleFileNameMap1.put(envFileRoleNameStr.substring(1), srcFileName);
													}
												}
										    }
										}
										
										isEnvNodeResolved = true;

										break;
									}								  
									else {
										child = child.getNextSibling();

										continue;
									}
								}
								
								if (!isEnvNodeResolved) {
									child = catalogDocElem1.getFirstChild();
									while (child != null) {
										String envName2 = null;
										Element elem2 = null;
										if (child.getNodeType() == Node.ELEMENT_NODE) {
											elem2 = (Element)child;
											String nodeName2 = elem2.getNodeName();
											if (ENVIRONMENT.equals(nodeName2)) {
												envName2 = elem2.getAttribute(NAME); 
											}
											else {
												child = child.getNextSibling();
	
												continue;
											}
										}
										else {
											child = child.getNextSibling();
	
											continue;
										}
	
										if (envName.equals(envName2)) {
											NodeList nodeList1 = elem2.getElementsByTagName(SOURCE);
											int size1 = nodeList1.getLength();
											if (size1 > 0) {
												for (int idx = 0; idx < size1; idx++) {
													Element elem3 = (Element)(nodeList1.item(idx));
													String srcFileName = elem3.getAttribute(FILE);									 
													if (!"".equals(srcFileName)) {
														Node node1 = elem2.getFirstChild();
														Map<String, String> nsMap = new HashMap<String, String>();
														while (node1 != null) {
															if (node1.getNodeType() == Node.ELEMENT_NODE) {
																Element el1 = (Element)node1;
																if (NAMESPACE.equals(el1.getNodeName())) {
																	String prefix = el1.getAttribute(PREFIX);
																	String uri = el1.getAttribute(URI);
																	nsMap.put(prefix, uri);
																}
															}

															node1 = node1.getNextSibling();
														}

														xmlNsPrefixResolver = getXMLNsPrefixResolver(nsMap);
														xctxt.setNamespaceContext(xmlNsPrefixResolver);

														isxmlNsContextConfigured = true;

														String envFileRoleNameStr = elem3.getAttribute("role");														
														resolveWithCatalog = true;
														
														if (".".equals(envFileRoleNameStr)) {
															constructXalanDtmFromXMLFile(srcFileName, xctxt, true);
														}
														else {
															roleFileNameMap1.put(envFileRoleNameStr.substring(1), srcFileName);
														}
													}
											    }
											}
	
											break;
										}
										else {
											child = child.getNextSibling();
	
											continue;
										}
									}
							    }
							}
						}
						
						Set<Entry<String, String>> roleFileNameEntrySet1 = roleFileNameMap1.entrySet();
						Iterator<Entry<String, String>> iter1 = roleFileNameEntrySet1.iterator();
						
						Map<QName, XObject> xpathVarMap2 = xctxt.getXPathVarMap();
						
						while (iter1.hasNext()) {
							Entry<String, String> entry1 = iter1.next();
							String roleName = entry1.getKey();
							String fileName = entry1.getValue();

							URL resolvedUrl = null;
							URI uri = new URI(fileName);
							if (uri.isAbsolute()) {
								resolvedUrl = new URL(fileName); 
							}
							else {
								URI uri2 = null;
								if (resolveWithCatalog) {
									uri2 = (new URI(W3C_XPATH3_TESTS_CATALOG_FILE_PATH)).resolve(fileName);
								}
								else {
									uri2 = (new URI(m_xslTransformTestSetFilePath)).resolve(fileName);
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
				    		XMLNodeCursorImpl xmlNodeCursorImpl = new XMLNodeCursorImpl(docNodeHandle, xctxt);
				    		
				    		xpathVarMap2.put(new QName(roleName), xmlNodeCursorImpl);
						}
						
						if (!isxmlNsContextConfigured) {
						   xmlNsPrefixResolver = getXMLNsPrefixResolver(new HashMap<String, String>());
						   xctxt.setNamespaceContext(xmlNsPrefixResolver);
						}
						
						NodeList depNodeList = testCaseElem.getElementsByTagName(DEPENDENCY);
						int size1 = depNodeList.getLength();
						XObject xpathResultObj = null;												

						boolean unRecoverableException = false;
						boolean isXslTestXPathAndXQuery = false;
						if (size1 == 0) {
							isXslTestXPathAndXQuery = true;
							size1 = 1;
						}
						
						String xpathExprStr = null;
						
						boolean xPathParseTimeOut = false;
						
						for (int idx = 0; idx < size1; idx++) {
							Element elem3 = null;
							String depType = null;							
							if (!isXslTestXPathAndXQuery) {
							   elem3 = (Element)(depNodeList.item(idx));
							   depType = elem3.getAttribute(TYPE);
							}							
														
							if (isXslTestXPathAndXQuery || (SPEC.equals(depType) && ((elem3.getAttribute(VALUE)).contains("XP31+") ||
									                                                   (elem3.getAttribute(VALUE)).contains("XP30+") ||
									                                                   (elem3.getAttribute(VALUE)).contains("XP20+")))) {
								elemTestResult = testResultDoc.createElement(TESTRESULT);																																		 
								elemTestResult.setAttribute(TESTNAME, testCaseNameStr);
								
								Element elemNode1 = (Element)((testCaseElem.getElementsByTagName(TEST)).item(0));    							
								xpathExprStr = elemNode1.getTextContent();

								xpathExprStr = getXPathNormalizedStr(xpathExprStr);

								try {
									int sourceNode = DTM.NULL;
									if ((envName != null) && !EMPTY.equals(envName)) {
										sourceNode = xctxt.getCurrentNode();
									}
									
									XPath xpathObj = null;
									// To run XPath parse within a specified timeout, to
									// avoid program indefinite wait due to XPath parse inf loop.
									ExecutorService executor = Executors.newSingleThreadExecutor();
									final String xpathExprStr2 = xpathExprStr;
									PrefixResolver xmlNsPrefixResolver2 = xmlNsPrefixResolver; 
									Future<XPath> future = executor.submit(() -> {                              	  
									    XPath xpathObj2 = new XPath(xpathExprStr2, null, xmlNsPrefixResolver2, XPath.SELECT, null);
									    
									    return xpathObj2;
									});
									
									try {
										// XPath parse evaluation timeout of 10 secs
										xpathObj = future.get(10, TimeUnit.SECONDS);
									} 
									catch (TimeoutException ex) {
										future.cancel(true);									    
										xPathParseTimeOut = true;
									}

									if (xpathObj != null) {									   
										xpathResultObj = xpathObj.execute(xctxt, sourceNode, xmlNsPrefixResolver);
										if (!xpathVarMap2.isEmpty()) {
										   xpathVarMap2.clear();
										}
									}
								}
								catch (TransformerException ex) {
									String errMeg = ex.getMessage();									
									if (!errMeg.contains("FOCH0002")) {									
										String[] errMesgParts = errMeg.split(":");
										if (errMesgParts.length > 2) {
											runTimeErrCode = (errMesgParts[1]).trim();
										}
										else if (errMesgParts.length > 1) {
											runTimeErrCode = (errMesgParts[0]).trim();
										}
									}
									else {
										String[] errMesgParts = errMeg.split(":");										
										runTimeErrCode = (errMesgParts[0]).trim();
									}
								}    							
								catch (Exception ex) {																		
									String errMeg = ex.getMessage();
									String[] errMesgParts = errMeg.split(":");
									if (errMesgParts.length > 2) {
										runTimeErrCode = (errMesgParts[1]).trim();
									}
									else if (errMesgParts.length > 1) {
										runTimeErrCode = (errMesgParts[0]).trim();
									}
									
									if (runTimeErrCode == null) {
										unRecoverableException = true;									
										elemTestResult.setAttribute(STATUS, FAIL);
										
										elemTestRun.appendChild(elemTestResult);
										
										node = node.getNextSibling();
									}
								}
								finally {
									xctxt.popCurrentNode();
								}

								break;
							}
						}

						if (unRecoverableException) {
							continue;	
						}

						Node node1 = (testCaseElem.getElementsByTagName("result")).item(0);
						Node child = node1.getFirstChild();
						while (child != null) {
							if (child.getNodeType() == Node.ELEMENT_NODE) {
								Element resultElem1 = (Element)child;
								String nodeName2 = resultElem1.getNodeName();
								String expectedResultStr = null;
								if (ASSERT_XML.equals(nodeName2)) {
								   String fileName = resultElem1.getAttribute("file");
								   if (!"".equals(fileName)) {
									   URI fileUri = new URI(fileName);
									   URL resolvedUrl = null;
									   if (fileUri.isAbsolute()) {
										   resolvedUrl = new URL(fileName); 
									   }
									   else {
										   URI resolvedUri = (new URI(m_xslTransformTestSetFilePath)).resolve(fileName);
										   resolvedUrl = resolvedUri.toURL();
									   }
									   
									   expectedResultStr = StringUtil.getStringContentFromUrl(resolvedUrl); 
								   }
								   else {
									  expectedResultStr = resultElem1.getTextContent(); 
								   }
								}
								else {
								   expectedResultStr = resultElem1.getTextContent();
								}
								
								boolean expectedResultStrUnquoted = false;

								XObject xpathExpectedObj = null;
								if ((xpathResultObj != null) && ASSERT.equals(nodeName2)) {
									expectedResultStr = getXPathNormalizedStr(expectedResultStr);
									Map<QName, XObject> xpathVarMap = xctxt.getXPathVarMap();
									xpathVarMap.put(new QName("result"), xpathResultObj);
									try {
										XPath xpathObj = new XPath(expectedResultStr, null, xctxt.getNamespaceContext(), XPath.SELECT, null);
										xpathExpectedObj = xpathObj.execute(xctxt, DTM.NULL, xmlNsPrefixResolver);
									}
									finally {
										xpathVarMap.remove(new QName("result"));
									}
								}																								
								else if (!(ASSERT_TRUE.equals(nodeName2) || ASSERT_FALSE.equals(nodeName2) || ASSERT_TYPE.equals(nodeName2) || 
										                                                             ALL_OF.equals(nodeName2) || ANY_OF.equals(nodeName2) || 
										                                                             ASSERT_XML.equals(nodeName2) || ERROR.equals(nodeName2)) && 
										                                                                                 (expectedResultStr != null) && !"".equals(expectedResultStr)) {
									if (expectedResultStr.startsWith("\"") && expectedResultStr.endsWith("\"")) {
										int size2 = expectedResultStr.length();
										expectedResultStr = expectedResultStr.substring(1, size2 - 1);
										expectedResultStr = "'" + expectedResultStr + "'"; 
									}
									else if (!expectedResultStr.startsWith("\'") && !expectedResultStr.endsWith("\'")) {
										expectedResultStr = "'" + expectedResultStr + "'";										
										expectedResultStrUnquoted = true;
									}

									XPath xpathObj = new XPath(expectedResultStr, null, xctxt.getNamespaceContext(), XPath.SELECT, null);
									xpathExpectedObj = xpathObj.execute(xctxt, DTM.NULL, xmlNsPrefixResolver);
								}
								else if (ASSERT_STRING_VALUE.equals(nodeName2) && "".equals(expectedResultStr)) {
									XPath xpathObj = new XPath("''", null, xctxt.getNamespaceContext(), XPath.SELECT, null);
									xpathExpectedObj = xpathObj.execute(xctxt, DTM.NULL, xmlNsPrefixResolver);
								}
								
								if (xPathParseTimeOut) {
									elemTestResult.setAttribute(STATUS, SKIPPED);
								}
								else if (ASSERT_DEEP_EQ.equals(nodeName2)) {
									if (xpathResultObj != null) {
										FuncDeepEqual funcDeepEqual = new FuncDeepEqual();
										funcDeepEqual.setArg(xpathResultObj, 0);
										funcDeepEqual.setArg(xpathExpectedObj, 1);

										XObject xObj = funcDeepEqual.execute(xctxt);
										if (xObj.bool()) {
										   elemTestResult.setAttribute(STATUS, PASS);
										}
										else {
										   elemTestResult.setAttribute(STATUS, FAIL);
										}
									}
									else {
										elemTestResult.setAttribute(STATUS, FAIL);
									}
								}
								else if (ASSERT_TRUE.equals(nodeName2)) {
									if ((xpathResultObj != null) && xpathResultObj.bool()) {
									   elemTestResult.setAttribute(STATUS, PASS);
									}
									else {
									   elemTestResult.setAttribute(STATUS, FAIL);
									}
								}
								else if (ASSERT_FALSE.equals(nodeName2)) {
									if ((xpathResultObj != null) && !xpathResultObj.bool()) {
									   elemTestResult.setAttribute(STATUS, PASS);
									}
									else {
									   elemTestResult.setAttribute(STATUS, FAIL);
									} 
								}
								else if (ASSERT_EQ.equals(nodeName2)) {
									boolean isStatusFinal = false;
									
									if (xpathResultObj instanceof ResultSequence) {
									   ResultSequence rSeq = (ResultSequence)xpathResultObj;
									   if (rSeq.size() == 1) {
										  xpathResultObj = rSeq.item(0);  
									   }
									}
									
									if ((xpathResultObj instanceof XNumber) || (xpathResultObj instanceof XSNumericType)) {
										if (expectedResultStrUnquoted) {
										   expectedResultStr = expectedResultStr.substring(1, expectedResultStr.length() - 1);
										}
										
										java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("([0-9]{0,})(\\.)?([0-9]{0,})");
										if ((pattern.matcher(expectedResultStr)).matches()) {
											expectedResultStr = "xs:decimal('" + expectedResultStr + "')";
											
											XPath xpathObj = new XPath(expectedResultStr, null, xctxt.getNamespaceContext(), XPath.SELECT, null);
                                  		    xpathExpectedObj = xpathObj.execute(xctxt, DTM.NULL, xmlNsPrefixResolver);
										}
									}
									
									if ((xpathResultObj instanceof XNumber) || (xpathResultObj instanceof XSNumericType)) {
										if (xpathExpectedObj instanceof XSString || xpathExpectedObj instanceof XString) {
											try {
												String strExpected1 = XslTransformEvaluationHelper.getStrVal(xpathExpectedObj);										   
												String strResult1 = XslTransformEvaluationHelper.getStrVal(xpathResultObj);
												double dbl1 = Double.valueOf(strExpected1);
												double dbl2 = Double.valueOf(strResult1);
												if (dbl1 == dbl2) {
												   elemTestResult.setAttribute(STATUS, PASS);
												}
												else {
												   elemTestResult.setAttribute(STATUS, FAIL);
												}
											}
											catch (NumberFormatException ex) {
												elemTestResult.setAttribute(STATUS, FAIL);
											}
											
											isStatusFinal = true;
										}
									}
									
									if (!isStatusFinal) {
										VcEquals vcEquals = new VcEquals();																				
										if ((xpathResultObj != null) && (vcEquals.operate(xpathResultObj, xpathExpectedObj)).bool()) {
											elemTestResult.setAttribute(STATUS, PASS);
										}
										else {
											elemTestResult.setAttribute(STATUS, FAIL);
										}
									}
								}
								else if (ASSERT_COUNT.equals(nodeName2)) {
									if (xpathResultObj != null) {
										int resultSeqLength = 0;
										if (xpathResultObj instanceof ResultSequence) {
										   resultSeqLength = ((ResultSequence)xpathResultObj).size(); 
										}
										else if (xpathResultObj instanceof XMLNodeCursorImpl) {
										   XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xpathResultObj;
										   resultSeqLength = xmlNodeCursorImpl.getLength();
										}
										else {
										   resultSeqLength = 1;
										}
										
										if (((XNumber)xpathExpectedObj).num() == (double)resultSeqLength) {
										   elemTestResult.setAttribute(STATUS, PASS);
										}
										else {
										   elemTestResult.setAttribute(STATUS, FAIL);
										}
									}
									else {
										elemTestResult.setAttribute(STATUS, FAIL);
									}
								}
                                else if (ASSERT_STRING_VALUE.equals(nodeName2)) {
                                	if (xpathResultObj != null) {
                                	   boolean a1 = false;
                                	   if (m_xslTransformTestSetFilePath.contains("ForClause.xml") && expectedResultStrUnquoted 
                                			                                                       && (xpathResultObj instanceof ResultSequence)) {
                                		   a1 = true;
                                		   
                                		   ResultSequence rSeq = (ResultSequence)xpathResultObj;
                                		   expectedResultStr = expectedResultStr.substring(1, expectedResultStr.length() - 1);
                                		   String[] expectedStrArr = expectedResultStr.split("\\s+");
                                		   if (rSeq.size() == expectedStrArr.length) {
                                			   boolean isXslTestPass = true;
                                			   for (int idx = 0; idx < expectedStrArr.length; idx++) {
                                				   String str1 = expectedStrArr[idx]; 
                                				   XObject xObj1 = rSeq.item(idx);
                                				   String str2 = XslTransformEvaluationHelper.getStrVal(xObj1);
                                				   if (!str1.equals(str2)) {                                					   
                                					   isXslTestPass = false;
                                					   
                                					   break;
                                				   }
                                			   }
                                			   
                                			   if (isXslTestPass) {
                                				  elemTestResult.setAttribute(STATUS, PASS); 
                                			   }
                                			   else {
                                				  elemTestResult.setAttribute(STATUS, FAIL); 
                                			   }
                                		   }
                                		   else {
                                			   elemTestResult.setAttribute(STATUS, FAIL);
                                		   }
                                	   }
                                	   
                                	   if (!a1) {
                                		   String expectedStr1 = XslTransformEvaluationHelper.getStrVal(xpathExpectedObj);
                                		   String resultStr1 = (XslTransformEvaluationHelper.getStrVal(xpathResultObj)).trim();
                                		   if (expectedStr1.equals(resultStr1)) {
                                			   elemTestResult.setAttribute(STATUS, PASS);
                                		   }
                                		   else {
                                			   elemTestResult.setAttribute(STATUS, FAIL);
                                		   }
                                	   }
                                	}
                                	else {
                                	   elemTestResult.setAttribute(STATUS, FAIL);
                                	}
								}                                
                                else if (ASSERT.equals(nodeName2)) {
                                	if ((xpathResultObj != null) && xpathExpectedObj.bool()) {
                                	   elemTestResult.setAttribute(STATUS, PASS);
                                	}
                                	else {
                                	   elemTestResult.setAttribute(STATUS, FAIL);
                                	}
								}
                                else if (ASSERT_XML.equals(nodeName2)) {
                                	if (xpathResultObj != null) {                                 		
                                		if (xpathResultObj instanceof XMLNodeCursorImpl) {
                                			int nodeHandle = ((XMLNodeCursorImpl)xpathResultObj).asNode(xctxt);
                                			if (nodeHandle != DTM.NULL) {
                                				DTM dtm = xctxt.getDTM(nodeHandle);
                                				Node node2 = dtm.getNode(nodeHandle);
                                				String resultXmlStr = XslTransformEvaluationHelper.serializeXmlDomElementNode(node2);
                                				
                                				try {
                                					expectedResultStr = expectedResultStr.trim();
                                					
                                    				byte[] byteArr = expectedResultStr.getBytes(StandardCharsets.UTF_8);
                                    				InputStream inpStream = new ByteArrayInputStream(byteArr);
                                    				
                                					Document document2 = m_xmlDocumentBuilder.parse(inpStream);                                					                                					                                					
                                					
                                					expectedResultStr = XslTransformEvaluationHelper.serializeXmlDomElementNode(document2);                                					
                                					
                                					if (expectedResultStr.equals(resultXmlStr)) {
                                					   elemTestResult.setAttribute(STATUS, PASS);
                                					}
                                					else {
                                					   elemTestResult.setAttribute(STATUS, FAIL);
                                					}
                                				}
                                				catch (Exception ex) {
                                					int idx2 = resultXmlStr.indexOf("?>");
                                					resultXmlStr = resultXmlStr.substring(idx2 + 2);
                                					resultXmlStr = resultXmlStr.replaceAll("\r?\n", "");
                                					
                                					expectedResultStr = expectedResultStr.replaceAll("\r?\n", "");
                                					
                                					if (expectedResultStr.equals(resultXmlStr)) {
                                 					   elemTestResult.setAttribute(STATUS, PASS);
                                 					}
                                 					else {
                                 					   elemTestResult.setAttribute(STATUS, FAIL);
                                 					}                                					
                                				}
                                			}
                                		}
                                	}
                                }
                                else if (ASSERT_TYPE.equals(nodeName2)) {
                                	int sourceNode = DTM.NULL;
                                	if ((envName != null) && !EMPTY.equals(envName)) {
                                		sourceNode = xctxt.getCurrentNode();
                                	}
                              	  
                                	XPath xpathObj = new XPath("(" + xpathExprStr + ") instance of " + expectedResultStr, null, xctxt.getNamespaceContext(), 
                                																												XPath.SELECT, null);
                                	XObject xObj = xpathObj.execute(xctxt, sourceNode, xmlNsPrefixResolver);
                                	if (xObj.bool()) {
                                	   elemTestResult.setAttribute(STATUS, PASS);
                                	}
                                	else {
                                	   elemTestResult.setAttribute(STATUS, FAIL);
                                	}
								}
                                else if (ASSERT_EMPTY.equals(nodeName2)) {
                                	if (xpathResultObj != null) { 
                                	   FuncEmpty funcEmpty = new FuncEmpty();
                                	   funcEmpty.setArg0(xpathResultObj);
                                	   
                                	   XObject xObj = funcEmpty.execute(xctxt);
                                	   if (xObj.bool()) {
                                		  elemTestResult.setAttribute(STATUS, PASS); 
                                	   }
                                	   else {
                                		  elemTestResult.setAttribute(STATUS, FAIL); 
                                	   }
                                	}
                                	else {
                                	   elemTestResult.setAttribute(STATUS, FAIL);
                                	}
								}
                                else if (ALL_OF.equals(nodeName2)) {
                                	NodeList nodeList = resultElem1.getChildNodes();
                                	int size2 = nodeList.getLength();
                                	
                                	boolean isXslTestPass = true;
                                	
                                	for (int idx = 0; idx < size2; idx++) {
                                	   Node node2 = nodeList.item(idx);
                                	   if (node2.getNodeType() == Node.ELEMENT_NODE) {
                                		  Element elNode1 = (Element)node2;
                                		  String nodeName3 = elNode1.getNodeName();
                                		  String expectedResultStr2 = elNode1.getTextContent();                                 		  
                                		  if ((xpathResultObj != null) && ASSERT.equals(nodeName3)) {
                                			  expectedResultStr2 = getXPathNormalizedStr(expectedResultStr2);
                                			  Map<QName, XObject> xpathVarMap = xctxt.getXPathVarMap();
                                			  xpathVarMap.put(new QName("result"), xpathResultObj);
                                			  try {
                                				  XPath xpathObj = new XPath(expectedResultStr2, null, xctxt.getNamespaceContext(), XPath.SELECT, null);
                                				  xpathExpectedObj = xpathObj.execute(xctxt, DTM.NULL, xmlNsPrefixResolver);
                                			  }
                                			  finally {
                                				  xpathVarMap.remove(new QName("result"));
                                			  }
                                			  
                                			  if ((xpathResultObj != null) && !xpathExpectedObj.bool()) {
                                				 isXslTestPass = false;
                                				 
                                				 break;
                                           	  }
                                		  }
                                		  else if (ASSERT_TRUE.equals(nodeName3)) {          									  
          									  if ((xpathResultObj != null) && !xpathResultObj.bool()) {
          										 isXslTestPass = false;
          										 
          										 break;
          									  }
                                		  }
                                          else if (ASSERT_FALSE.equals(nodeName3)) {          									  
          									  if ((xpathResultObj != null) && xpathResultObj.bool()) {
          										 isXslTestPass = false;
          										 
          										 break;
          									  }
                                		  }
                                          else if (ASSERT_TYPE.equals(nodeName3)) {
                                        	  int sourceNode = DTM.NULL;
                                        	  if ((envName != null) && !EMPTY.equals(envName)) {
                                        	     sourceNode = xctxt.getCurrentNode();
                                        	  }
                                        	  
                                        	  XPath xpathObj = new XPath("(" + xpathExprStr + ") instance of " + expectedResultStr2, null, xctxt.getNamespaceContext(), 
                                        			                                                                                                          XPath.SELECT, null);
                                        	  XObject xObj = xpathObj.execute(xctxt, sourceNode, xmlNsPrefixResolver);
                                        	  if (!xObj.bool()) {
                                        		 isXslTestPass = false;
           										 
           										 break;
                                        	  }
                                		  }
                                          else if (ASSERT_DEEP_EQ.equals(nodeName3)) {
                                        	  if (xpathResultObj != null) {
                                        		  XPath xpathObj = new XPath(expectedResultStr2, null, xctxt.getNamespaceContext(), XPath.SELECT, null);
                                				  xpathExpectedObj = xpathObj.execute(xctxt, DTM.NULL, xmlNsPrefixResolver);
                                				  
                                        		  FuncDeepEqual funcDeepEqual = new FuncDeepEqual();
                                        		  funcDeepEqual.setArg(xpathResultObj, 0);
                                        		  funcDeepEqual.setArg(xpathExpectedObj, 1);

                                        		  XObject xObj = funcDeepEqual.execute(xctxt);
                                        		  if (!xObj.bool()) {
                                        			  isXslTestPass = false;

                                        			  break;
                                        		  }
                                        	  } 
                                		  }
                                          else if (ASSERT_EQ.equals(nodeName3)) {                                        	                                          	  
                                        	  if (xpathResultObj instanceof ResultSequence) {
           									     ResultSequence rSeq = (ResultSequence)xpathResultObj;
           									     if (rSeq.size() == 1) {
           										    xpathResultObj = rSeq.item(0);  
           									     }
           									  }
                                        	  
                                        	  if (expectedResultStr2.startsWith("\"") && expectedResultStr2.endsWith("\"")) {
                                        		  int size3 = expectedResultStr2.length();
                                        		  expectedResultStr2 = expectedResultStr2.substring(1, size3 - 1);
                                        		  expectedResultStr2 = "'" + expectedResultStr2 + "'"; 
                                        	  }
                                        	  else if (!expectedResultStr2.startsWith("\'") && !expectedResultStr2.endsWith("\'")) {
                                        		  expectedResultStr2 = "'" + expectedResultStr2 + "'";										
                                        		  expectedResultStrUnquoted = true;
                                        	  }
                                        	  
                                        	  if ((xpathResultObj instanceof XNumber) || (xpathResultObj instanceof XSNumericType)) {
                                        		  if (expectedResultStrUnquoted) {
                                        			 expectedResultStr2 = expectedResultStr2.substring(1, expectedResultStr2.length() - 1);
                                        		  }

                                        		  java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("([0-9]{0,})(\\.)?([0-9]{0,})");
                                        		  if ((pattern.matcher(expectedResultStr2)).matches()) {
                                        			  expectedResultStr2 = "xs:decimal('" + expectedResultStr2 + "')";
                                        		  }
          									  }
                                        	  
                                        	  if (xpathResultObj != null) {                                        		                                          		                                         		  
                                        		  XPath xpathObj = new XPath(expectedResultStr2, null, xctxt.getNamespaceContext(), XPath.SELECT, null);
                                        		  xpathExpectedObj = xpathObj.execute(xctxt, DTM.NULL, xmlNsPrefixResolver);

                                        		  if ((xpathResultObj instanceof XNumber) || (xpathResultObj instanceof XSNumericType)) {
                                        			  if (xpathExpectedObj instanceof XSString || xpathExpectedObj instanceof XString) {
                                        				  try {
                                        					  String strExpected1 = XslTransformEvaluationHelper.getStrVal(xpathExpectedObj);										   
                                        					  String strResult1 = XslTransformEvaluationHelper.getStrVal(xpathResultObj);
                                        					  double dbl1 = Double.valueOf(strExpected1);
                                        					  double dbl2 = Double.valueOf(strResult1);
                                        					  if (dbl1 != dbl2) {
                                        						  isXslTestPass = false;

                                        						  break;
                                        					  }          												
                                        				  }
                                        				  catch (NumberFormatException ex) {
                                        					  isXslTestPass = false;

                                    						  break;
                                        				  }
                                        			  }
                                        		  }
                                        		  
                                        		  VcEquals vcEquals = new VcEquals();
                                        		  if ((xpathResultObj != null) && !((vcEquals.operate(xpathResultObj, xpathExpectedObj)).bool())) {
                                        			  isXslTestPass = false;

                                        			  break;
                                        		  }
                                        	  }
                                		  }
                                          else if (ASSERT_COUNT.equals(nodeName3)) {
                                        	  if (xpathResultObj != null) {
                                        		  XPath xpathObj = new XPath(expectedResultStr2, null, xctxt.getNamespaceContext(), XPath.SELECT, null);
                                        		  xpathExpectedObj = xpathObj.execute(xctxt, DTM.NULL, xmlNsPrefixResolver);
                                        		  
                                        		  int resultSeqLength = 0;
                                        		  if (xpathResultObj instanceof ResultSequence) {
                                        			  resultSeqLength = ((ResultSequence)xpathResultObj).size(); 
                                        		  }
                                        		  else if (xpathResultObj instanceof XMLNodeCursorImpl) {
                                        			  XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xpathResultObj;
                                        			  resultSeqLength = xmlNodeCursorImpl.getLength();
                                        		  }
                                        		  else {
                                        			  resultSeqLength = 1;
                                        		  }

                                        		  if (((XNumber)xpathExpectedObj).num() != (double)resultSeqLength) {
                                        			  isXslTestPass = false;

                                        			  break;
                                        		  }                                        		  
                                        	  }
                                		  }
                                          else if (ASSERT_STRING_VALUE.equals(nodeName3)) {
                                        	  if (xpathResultObj != null) {                                        		                                          		  
                                                  if (m_xslTransformTestSetFilePath.contains("ForClause.xml") && expectedResultStrUnquoted 
                                                		                                                      && (xpathResultObj instanceof ResultSequence)) {
                                                	  ResultSequence rSeq = (ResultSequence)xpathResultObj;
                                                	  expectedResultStr2 = expectedResultStr2.substring(1, expectedResultStr.length() - 1);
                                                	  String[] expectedStrArr = expectedResultStr2.split("\\s+");
                                                	  if (rSeq.size() == expectedStrArr.length) {
                                                		  for (int idx2 = 0; idx2 < expectedStrArr.length; idx2++) {
                                                			  String str1 = expectedStrArr[idx]; 
                                                			  XObject xObj1 = rSeq.item(idx);
                                                			  String str2 = XslTransformEvaluationHelper.getStrVal(xObj1);
                                                			  if (!str1.equals(str2)) {                                					   
                                                				  isXslTestPass = false;

                                                				  break;
                                                			  }
                                                		  }

                                                		  if (!isXslTestPass) {
                                                			  break; 
                                                		  }
                                                	  }
                                                	  else {
                                                		  isXslTestPass = false;

                                        				  break;
                                                	  }
                                        		  }

                                                  if ("".equals(expectedResultStr2)) {
                                                	  expectedResultStr2 = "''";  
                                                  }                                        		                                          		  

                                                  XPath xpathObj = new XPath(expectedResultStr2, null, xctxt.getNamespaceContext(), XPath.SELECT, null);
                                                  xpathExpectedObj = xpathObj.execute(xctxt, DTM.NULL, xmlNsPrefixResolver);

                                                  String expectedStr1 = XslTransformEvaluationHelper.getStrVal(xpathExpectedObj);
                                                  String resultStr1 = (XslTransformEvaluationHelper.getStrVal(xpathResultObj)).trim();
                                                  if (!expectedStr1.equals(resultStr1)) {
                                                	  isXslTestPass = false;

                                                	  break; 
                                                  } 
                                        	  } 
                                		  }
                                          else if (ASSERT_XML.equals(nodeName3)) {
                                        	  if (xpathResultObj != null) {                                 		
                                        		  if (xpathResultObj instanceof XMLNodeCursorImpl) {
                                        			  int nodeHandle = ((XMLNodeCursorImpl)xpathResultObj).asNode(xctxt);
                                        			  if (nodeHandle != DTM.NULL) {
                                        				  DTM dtm = xctxt.getDTM(nodeHandle);
                                        				  Node node3 = dtm.getNode(nodeHandle);
                                        				  String resultXmlStr = XslTransformEvaluationHelper.serializeXmlDomElementNode(node3);

                                        				  try {
                                        					  expectedResultStr2 = expectedResultStr2.trim();

                                        					  byte[] byteArr = expectedResultStr2.getBytes(StandardCharsets.UTF_8);
                                        					  InputStream inpStream = new ByteArrayInputStream(byteArr);

                                        					  Document document2 = m_xmlDocumentBuilder.parse(inpStream);                                					                                					                                					

                                        					  expectedResultStr2 = XslTransformEvaluationHelper.serializeXmlDomElementNode(document2);                                					

                                        					  if (!expectedResultStr2.equals(resultXmlStr)) {
                                        						  isXslTestPass = false;

                                                    			  break;
                                        					  }                                        					  
                                        				  }
                                        				  catch (Exception ex) {
                                        					  int idx2 = resultXmlStr.indexOf("?>");
                                        					  resultXmlStr = resultXmlStr.substring(idx2 + 2);
                                        					  resultXmlStr = resultXmlStr.replaceAll("\r?\n", "");

                                        					  expectedResultStr2 = expectedResultStr2.replaceAll("\r?\n", "");

                                        					  if (!expectedResultStr2.equals(resultXmlStr)) {
                                        						  isXslTestPass = false;

                                                    			  break;
                                        					  }                                        					                                  				
                                        				  }
                                        			  }
                                        		  }
                                        	  }
                                          }
                                          else if (ASSERT_EMPTY.equals(nodeName3)) {
                                        	  if (xpathResultObj != null) { 
                                        		  FuncEmpty funcEmpty = new FuncEmpty();
                                        		  funcEmpty.setArg0(xpathResultObj);

                                        		  XObject xObj = funcEmpty.execute(xctxt);
                                        		  if (!xObj.bool()) {
                                        			  isXslTestPass = false;

                                        			  break; 
                                        		  }                                           	   
                                        	  }
                                		  }
                                          else if (ERROR.equals(nodeName3)) {
                                        	  expectedErrCode = resultElem1.getAttribute("code");
                                        	  if ((runTimeErrCode != null) && !runTimeErrCode.equals(expectedErrCode)) {
                                        		  isXslTestPass = false;

                                    			  break;  
          									  }
                                          }
                                          else if (ASSERT_PERMUTATION.equals(nodeName3)) {
                                        	  // Skipping these XSL test cases, for now 									
                                              m_skipped_tests_list.add(testCaseNameStr);
                                              
                                              elemTestResult.setAttribute(STATUS, SKIPPED);
                                              
                                              isXslTestPass = false;

                                			  break;
                                          }                                		                                  		  
                                	   }
                                	   
                                	   node2 = node2.getNextSibling();
                                	}
                                	
                                	if (isXslTestPass) {
                                	   elemTestResult.setAttribute(STATUS, PASS);
                                	}
                                	else {
                                	   elemTestResult.setAttribute(STATUS, FAIL);
                                	}
								}
                                else if (ANY_OF.equals(nodeName2)) {
                                	NodeList nodeList = resultElem1.getChildNodes();
                                	int size2 = nodeList.getLength();
                                	
                                	boolean isXslTestPass = false;
                                	
                                	for (int idx = 0; idx < size2; idx++) {
                                	   Node node2 = nodeList.item(idx);
                                	   if (node2.getNodeType() == Node.ELEMENT_NODE) {
                                		  Element elNode1 = (Element)node2;
                                		  String nodeName3 = elNode1.getNodeName();
                                		  String expectedResultStr2 = elNode1.getTextContent();                                 		  
                                		  if ((xpathResultObj != null) && ASSERT.equals(nodeName3)) {
                                			  expectedResultStr2 = getXPathNormalizedStr(expectedResultStr2);
                                			  Map<QName, XObject> xpathVarMap = xctxt.getXPathVarMap();
                                			  xpathVarMap.put(new QName("result"), xpathResultObj);
                                			  try {
                                				  XPath xpathObj = new XPath(expectedResultStr2, null, xctxt.getNamespaceContext(), XPath.SELECT, null);
                                				  xpathExpectedObj = xpathObj.execute(xctxt, DTM.NULL, xmlNsPrefixResolver);
                                			  }
                                			  finally {
                                				  xpathVarMap.remove(new QName("result"));
                                			  }
                                			  
                                			  if ((xpathResultObj != null) && xpathExpectedObj.bool()) {
                                				 isXslTestPass = true;
                                				 
                                				 break;
                                           	  }
                                		  }
                                		  else if (ASSERT_TRUE.equals(nodeName3)) {          									  
          									  if ((xpathResultObj != null) && xpathResultObj.bool()) {
          										 isXslTestPass = true;
          										 
          										 break;
          									  }
                                		  }
                                          else if (ASSERT_FALSE.equals(nodeName3)) {          									  
          									  if ((xpathResultObj != null) && !xpathResultObj.bool()) {
          										 isXslTestPass = true;
          										 
          										 break;
          									  }
                                		  }
                                          else if (ASSERT_TYPE.equals(nodeName3)) {
                                        	  int sourceNode = DTM.NULL;
                                        	  if ((envName != null) && !EMPTY.equals(envName)) {
                                        	     sourceNode = xctxt.getCurrentNode();
                                        	  }
                                        	  
                                        	  XPath xpathObj = new XPath("(" + xpathExprStr + ") instance of " + expectedResultStr2, null, xctxt.getNamespaceContext(), 
                                        			                                                                                                          XPath.SELECT, null);
                                        	  XObject xObj = xpathObj.execute(xctxt, sourceNode, xmlNsPrefixResolver);
                                        	  if (xObj.bool()) {
                                        		 isXslTestPass = true;
           										 
           										 break;
                                        	  }
                                		  }
                                          else if (ASSERT_DEEP_EQ.equals(nodeName3)) {
                                        	  if (xpathResultObj != null) {
                                        		  XPath xpathObj = new XPath(expectedResultStr2, null, xctxt.getNamespaceContext(), XPath.SELECT, null);
                                				  xpathExpectedObj = xpathObj.execute(xctxt, DTM.NULL, xmlNsPrefixResolver);
                                				  
                                        		  FuncDeepEqual funcDeepEqual = new FuncDeepEqual();
                                        		  funcDeepEqual.setArg(xpathResultObj, 0);
                                        		  funcDeepEqual.setArg(xpathExpectedObj, 1);

                                        		  XObject xObj = funcDeepEqual.execute(xctxt);
                                        		  if (xObj.bool()) {
                                        			  isXslTestPass = true;

                                        			  break;
                                        		  }
                                        	  } 
                                		  }
                                          else if (ASSERT_EQ.equals(nodeName3)) {
                                        	  if (xpathResultObj instanceof ResultSequence) {
            									     ResultSequence rSeq = (ResultSequence)xpathResultObj;
            									     if (rSeq.size() == 1) {
            										    xpathResultObj = rSeq.item(0);  
            									     }
            									  }
                                         	  
                                         	  if (expectedResultStr2.startsWith("\"") && expectedResultStr2.endsWith("\"")) {
                                         		  int size3 = expectedResultStr2.length();
                                         		  expectedResultStr2 = expectedResultStr2.substring(1, size3 - 1);
                                         		  expectedResultStr2 = "'" + expectedResultStr2 + "'"; 
                                         	  }
                                         	  else if (!expectedResultStr2.startsWith("\'") && !expectedResultStr2.endsWith("\'")) {
                                         		  expectedResultStr2 = "'" + expectedResultStr2 + "'";										
                                         		  expectedResultStrUnquoted = true;
                                         	  }
                                         	  
                                         	  if ((xpathResultObj instanceof XNumber) || (xpathResultObj instanceof XSNumericType)) {
                                         		  if (expectedResultStrUnquoted) {
                                         			 expectedResultStr2 = expectedResultStr2.substring(1, expectedResultStr2.length() - 1);
                                         		  }

                                         		  java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("([0-9]{0,})(\\.)?([0-9]{0,})");
                                         		  if ((pattern.matcher(expectedResultStr2)).matches()) {
                                         			  expectedResultStr2 = "xs:decimal('" + expectedResultStr2 + "')";
                                         		  }
           									  }
                                         	  
                                        	  if (xpathResultObj != null) {                                        		                                          		  
                                        		  XPath xpathObj = new XPath(expectedResultStr2, null, xctxt.getNamespaceContext(), XPath.SELECT, null);
                                        		  xpathExpectedObj = xpathObj.execute(xctxt, DTM.NULL, xmlNsPrefixResolver);

                                        		  if ((xpathResultObj instanceof XNumber) || (xpathResultObj instanceof XSNumericType)) {
                                        			  if (xpathExpectedObj instanceof XSString || xpathExpectedObj instanceof XString) {
                                        				  try {
                                        					  String strExpected1 = XslTransformEvaluationHelper.getStrVal(xpathExpectedObj);										   
                                        					  String strResult1 = XslTransformEvaluationHelper.getStrVal(xpathResultObj);
                                        					  double dbl1 = Double.valueOf(strExpected1);
                                        					  double dbl2 = Double.valueOf(strResult1);
                                        					  if (dbl1 == dbl2) {
                                        						  isXslTestPass = true;

                                        						  break;
                                        					  }          												
                                        				  }
                                        				  catch (NumberFormatException ex) {
                                        					  // no op
                                        				  }
                                        			  }
                                        		  }

                                        		  VcEquals vcEquals = new VcEquals();
                                        		  if ((xpathResultObj != null) && (vcEquals.operate(xpathResultObj, xpathExpectedObj)).bool()) {
                                        			  isXslTestPass = true;

                                        			  break;
                                        		  }
                                        	  }
                                		  }
                                          else if (ASSERT_COUNT.equals(nodeName3)) {
                                        	  if (xpathResultObj != null) {
                                        		  XPath xpathObj = new XPath(expectedResultStr2, null, xctxt.getNamespaceContext(), XPath.SELECT, null);
                                        		  xpathExpectedObj = xpathObj.execute(xctxt, DTM.NULL, xmlNsPrefixResolver);
                                        		  
                                        		  int resultSeqLength = 0;
                                        		  if (xpathResultObj instanceof ResultSequence) {
                                        			  resultSeqLength = ((ResultSequence)xpathResultObj).size(); 
                                        		  }
                                        		  else if (xpathResultObj instanceof XMLNodeCursorImpl) {
                                        			  XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xpathResultObj;
                                        			  resultSeqLength = xmlNodeCursorImpl.getLength();
                                        		  }
                                        		  else {
                                        			  resultSeqLength = 1;
                                        		  }

                                        		  if (((XNumber)xpathExpectedObj).num() == (double)resultSeqLength) {
                                        			  isXslTestPass = true;

                                        			  break;
                                        		  }                                        		  
                                        	  }
                                		  }
                                          else if (ASSERT_STRING_VALUE.equals(nodeName3)) {
                                        	  if (xpathResultObj != null) {                                        		                                          		  
                                                  if (m_xslTransformTestSetFilePath.contains("ForClause.xml") && expectedResultStrUnquoted 
                                                		                                                      && (xpathResultObj instanceof ResultSequence)) {
                                                	  ResultSequence rSeq = (ResultSequence)xpathResultObj;
                                                	  expectedResultStr2 = expectedResultStr2.substring(1, expectedResultStr.length() - 1);
                                                	  String[] expectedStrArr = expectedResultStr2.split("\\s+");
                                                	  boolean isXslTestPass1 = true;
                                                	  if (rSeq.size() == expectedStrArr.length) {
                                                		  for (int idx2 = 0; idx2 < expectedStrArr.length; idx2++) {
                                                			  String str1 = expectedStrArr[idx]; 
                                                			  XObject xObj1 = rSeq.item(idx);
                                                			  String str2 = XslTransformEvaluationHelper.getStrVal(xObj1);
                                                			  if (!str1.equals(str2)) {                                					   
                                                				  isXslTestPass1 = false;

                                                				  break;
                                                			  }
                                                		  }

                                                		  if (isXslTestPass1) {
                                                			  isXslTestPass = true;

                                            				  break; 
                                                		  }
                                                	  }                                                	  
                                        		  }

                                                  if ("".equals(expectedResultStr2)) {
                                                	  expectedResultStr2 = "''";  
                                                  }                                        		                                          		  

                                                  XPath xpathObj = new XPath(expectedResultStr2, null, xctxt.getNamespaceContext(), XPath.SELECT, null);
                                                  xpathExpectedObj = xpathObj.execute(xctxt, DTM.NULL, xmlNsPrefixResolver);

                                                  String expectedStr1 = XslTransformEvaluationHelper.getStrVal(xpathExpectedObj);
                                                  String resultStr1 = (XslTransformEvaluationHelper.getStrVal(xpathResultObj)).trim();
                                                  if (expectedStr1.equals(resultStr1)) {
                                                	  isXslTestPass = true;

                                                	  break; 
                                                  } 
                                        	  } 
                                		  }
                                          else if (ASSERT_XML.equals(nodeName3)) {
                                        	  if (xpathResultObj != null) {                                 		
                                        		  if (xpathResultObj instanceof XMLNodeCursorImpl) {
                                        			  int nodeHandle = ((XMLNodeCursorImpl)xpathResultObj).asNode(xctxt);
                                        			  if (nodeHandle != DTM.NULL) {
                                        				  DTM dtm = xctxt.getDTM(nodeHandle);
                                        				  Node node3 = dtm.getNode(nodeHandle);
                                        				  String resultXmlStr = XslTransformEvaluationHelper.serializeXmlDomElementNode(node3);

                                        				  try {
                                        					  expectedResultStr2 = expectedResultStr2.trim();

                                        					  byte[] byteArr = expectedResultStr2.getBytes(StandardCharsets.UTF_8);
                                        					  InputStream inpStream = new ByteArrayInputStream(byteArr);

                                        					  Document document2 = m_xmlDocumentBuilder.parse(inpStream);                                					                                					                                					

                                        					  expectedResultStr2 = XslTransformEvaluationHelper.serializeXmlDomElementNode(document2);                                					

                                        					  if (expectedResultStr2.equals(resultXmlStr)) {
                                        						  isXslTestPass = true;

                                                    			  break;
                                        					  }                                        					  
                                        				  }
                                        				  catch (Exception ex) {
                                        					  int idx2 = resultXmlStr.indexOf("?>");
                                        					  resultXmlStr = resultXmlStr.substring(idx2 + 2);
                                        					  resultXmlStr = resultXmlStr.replaceAll("\r?\n", "");

                                        					  expectedResultStr2 = expectedResultStr2.replaceAll("\r?\n", "");

                                        					  if (expectedResultStr2.equals(resultXmlStr)) {
                                        						  isXslTestPass = true;

                                                    			  break;
                                        					  }                                        					                                  				
                                        				  }
                                        			  }
                                        		  }
                                        	  }
                                          }
                                          else if (ASSERT_EMPTY.equals(nodeName3)) {
                                        	  if (xpathResultObj != null) { 
                                        		  FuncEmpty funcEmpty = new FuncEmpty();
                                        		  funcEmpty.setArg0(xpathResultObj);

                                        		  XObject xObj = funcEmpty.execute(xctxt);
                                        		  if (xObj.bool()) {
                                        			  isXslTestPass = true;

                                        			  break; 
                                        		  }                                           	   
                                        	  }
                                		  }                                		  
                                          else if (ERROR.equals(nodeName3)) {
                                        	  expectedErrCode = resultElem1.getAttribute("code");
                                        	  if ((runTimeErrCode != null) && runTimeErrCode.equals(expectedErrCode)) {
                                        		  isXslTestPass = true;

                                    			  break;  
          									  }
                                          }
                                          else if (ASSERT_PERMUTATION.equals(nodeName3)) {
                                        	  // Skipping these XSL test cases, for now 									
                                              m_skipped_tests_list.add(testCaseNameStr);
                                              
                                              elemTestResult.setAttribute(STATUS, SKIPPED);

                                			  break;
                                          }                                		                                  		  
                                	   }
                                	   
                                	   node2 = node2.getNextSibling();
                                	}
                                	
                                	if (isXslTestPass) {
                                	   elemTestResult.setAttribute(STATUS, PASS);
                                	}
                                	else {
                                	   elemTestResult.setAttribute(STATUS, FAIL);
                                	}
								}
								else if (ASSERT_PERMUTATION.equals(nodeName2)) {
									// Skipping these XSL test cases, for now 									
                                    m_skipped_tests_list.add(testCaseNameStr);
                                    
                                    elemTestResult.setAttribute(STATUS, SKIPPED);
								}
								else if (ERROR.equals(nodeName2)) {
									expectedErrCode = resultElem1.getAttribute("code");
									if ((runTimeErrCode != null) && runTimeErrCode.equals(expectedErrCode)) {
									   elemTestResult.setAttribute(STATUS, PASS); 
									}
									else {
									   elemTestResult.setAttribute(STATUS, FAIL);
									}
								}
							}

							child = child.getNextSibling();
						}
						
						elemTestRun.appendChild(elemTestResult);
					}    				
				}

				node = node.getNextSibling();
			}
			catch (Exception ex) {
				node = node.getNextSibling();
				if (elemTestResult != null) {
					elemTestResult.setAttribute(STATUS, FAIL);
				}
			}
        }
		
		NodeList nodeList = testResultDoc.getElementsByTagName(TESTRESULT);

		int testsPassCount = 0;
		int testsfailCount = 0;
		int testsSkippedCount = 0;
		int testStatusUnknownCount = 0;

		int length1 = nodeList.getLength();
		for (int idx = 0; idx < length1; idx++) {
			Element element = (Element)(nodeList.item(idx));
			String statusValue = element.getAttribute(STATUS);
			if (PASS.equals(statusValue)) {
				testsPassCount++; 
			}
			else if (FAIL.equals(statusValue)) {
				testsfailCount++; 
			}
			else if (SKIPPED.equals(statusValue)) {
				testsSkippedCount++; 
			}
			else {
				testStatusUnknownCount++; 
			}
		}

		int totalTestsRun = (testsPassCount + testsfailCount + testStatusUnknownCount);

		elemTestRun.setAttribute(PASS, String.valueOf(testsPassCount));
		elemTestRun.setAttribute(FAIL, String.valueOf(testsfailCount));
		
		if (testsSkippedCount > 0) {
		   elemTestRun.setAttribute(SKIPPED, String.valueOf(testsSkippedCount));
		}
		
		elemTestRun.setAttribute(STATUS_UNKNOWN, String.valueOf(testStatusUnknownCount));
		elemTestRun.setAttribute(RUN, String.valueOf(totalTestsRun));

		// Serialize W3C XPath 3.1 test set results file to file system
		try {
			String xslTestResultStr = serializeXmlDomElementNode(testResultDoc);

			File xslAnalyzeStringTestResultFile = new File(new URI(W3C_XPATH3_TESTS_RESULT_DIR_HOME + m_resultSubFolderName + "/" + m_testResultFileName));
			FileOutputStream testResultFos = new FileOutputStream(xslAnalyzeStringTestResultFile);
			testResultFos.write(xslTestResultStr.getBytes());
			testResultFos.flush();
			testResultFos.close();
		}
		catch (Exception ex) {
			// no op
		}
    	
    }
        
    /**
     * Method definition, to run an XPath 3.1 test set collection 
     * and produce XSL test run reports.
     */
    protected void runXPathTestSetCollectionAndProduceResult() {    	
        
    	int length2 = m_test_set_fileArr.length;
    	
    	for (int a1 = 0; a1 < length2; a1++) {    		    		
    		m_xslTransformTestSetFilePath = W3C_XPATH3_TESTS_META_DATA_DIR_HOME + m_resultSubFolderName + "/" + m_test_set_fileArr[a1];
    		m_testResultFileName = m_test_set_result_fileArr[a1];
    		
    		runXPathTestSetAndProduceResult();
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
     * @param nsMap					       java.util.Map object, having XML namespace 
     *                                     prefix and uri mappings. This map object may be
     *                                     empty.
     * 
     * @return                             PrefixResolver object instance
     */
    protected PrefixResolver getXMLNsPrefixResolver(Map<String, String> nsMap) {
    	
    	PrefixResolver result = null;
    	
    	Document document = m_xmlDocumentBuilder.newDocument();
    	Element elem = document.createElement("elem1");
    	elem.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:fn", "http://www.w3.org/2005/xpath-functions");
    	elem.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:math", "http://www.w3.org/2005/xpath-functions/math");
    	elem.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:map", "http://www.w3.org/2005/xpath-functions/map");
    	elem.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:array", "http://www.w3.org/2005/xpath-functions/array");
    	elem.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xs", "http://www.w3.org/2001/XMLSchema");
    	
    	if (nsMap.size() > 0) {
    	   Set<Entry<String, String>> mapEntrySet1 = nsMap.entrySet();
    	   Iterator<Entry<String, String>> iter1 = mapEntrySet1.iterator();
    	   while (iter1.hasNext()) {
    		  Entry<String, String> mapEntry1 = iter1.next();
    		  String prefix = mapEntry1.getKey();
    		  String uri = mapEntry1.getValue();
    		  
    		  elem.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + prefix, uri);
    	   }
    	}
    	
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
    	
    	if (xmlFile != null) {
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
