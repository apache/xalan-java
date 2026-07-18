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
import java.util.HashSet;
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

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;

import org.apache.xalan.tests.util.XslTransformTestsUtil;
import org.apache.xalan.xslt.util.StringUtil;
import org.apache.xalan.xslt.util.XslTransformData;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.utils.Constants;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.PrefixResolverDefault;
import org.apache.xml.utils.QName;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathStaticContext;
import org.apache.xpath.functions.FuncDeepEqual;
import org.apache.xpath.functions.FuncEmpty;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.objects.XString;
import org.apache.xpath.operations.VcEquals;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSNormalizedString;
import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSString;
import xml.xpath31.processor.types.XSToken;

/**
 * Xalan-J XSL 3 tests utility class, to support Xalan-J W3C 
 * XPath 3.1 test suite driver implementation.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class W3CXPath3TestsUtil extends XslTransformTestsUtil { 	
                    
    public static final String TESTRUN = "testrun";
    
    public static final String NAME = "name";
    
    public static final String TESTCASE = "test-case";
    
    public static final String ENVIRONMENT = "environment";
    
    public static final String REF = "ref";
    
    public static final String EMPTY = "empty";
    
    public static final String SOURCE = "source";
    
    public static final String FILE = "file";
    
    public static final String DEPENDENCY = "dependency";
    
    public static final String DATETIME = "dateTime";
    
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
    
    public static final String TRUE = "true";
    
    public static final String IGNORE_PREFIXES = "ignore-prefixes";
    
    public static final String XALAN_ERR_CODE_ABSENT = "xalan_err_code_absent";
    
    public static final String DESC = "desc";
    
    public static final String XSLT_PROCESSOR = "xslt_processor";
    
    public static final String FEATURE = "feature";
    
    public static final String XPATH10_COMPATIBILITY_MODE = "xpath-1.0-compatibility";
    
    public static final String XS_SCHEMA_VALIDATION = "schemaValidation";
    
    public static final String REASON = "reason";
    
    public static final String XPATH31 = "XP31+";
    
    public static final String XPATH30 = "XP30+";
    
    public static final String XPATH20 = "XP20+";
    
    public static final String XML_VERSION = "xml-version";
    
    public static final String XSD_VERSION = "xsd-version";
    
    public static final String UNICODE_VERSION = "unicode-version";
    
    public static final String HIGHER_ORDER_FUNC = "higherOrderFunctions";
    
    public static final String RESULT = "result";
    
    public static final String ROLE = "role";
    
    public static final String EMPTY_STRING = "";
    
    public static final String XS_COLON = "xs:";
    
    public static final String FN_COLON = "fn:";
    
    public static final String UNLIKELY_XML_ELEM_START_TAG = "<unlikely_xml_elem_name>";
    
    public static final String UNLIKELY_XML_ELEM_END_TAG = "</unlikely_xml_elem_name>";
    
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
						XPathContext xctxt = null;

						try {
							String testCaseNameStr = testCaseElem.getAttribute(NAME);												
							NodeList envNodeList = testCaseElem.getElementsByTagName(ENVIRONMENT);							

							xctxt = new XPathContext(true);
							xctxt.setIncremental(false);
							(xctxt.getDTMManager()).setIncremental(false);
							xctxt.setSource_location(false);
							(xctxt.getDTMManager()).setSource_location(false);

							PrefixResolver xmlNsPrefixResolver = null;						
							String envName = null;                        
							boolean isXmlNsContextConf = false;

							Map<String, String> roleFileNameMap1 = new HashMap<String, String>();

							boolean resolveWithCatalog = false;

							if (envNodeList.getLength() > 0) {
								Element elem = (Element)(envNodeList.item(0));
								envName = elem.getAttribute(REF);
								if ((envName != null) && !EMPTY_STRING.equals(envName) && !EMPTY.equals(envName)) {																	
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
													if (!EMPTY_STRING.equals(srcFileName)) {
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

														isXmlNsContextConf = true;

														String envFileRoleNameStr = elem3.getAttribute(ROLE);                                            	
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
														if (!EMPTY_STRING.equals(srcFileName)) {
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

															isXmlNsContextConf = true;

															String envFileRoleNameStr = elem3.getAttribute(ROLE);														
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

							if (!isXmlNsContextConf) {
								xmlNsPrefixResolver = getXMLNsPrefixResolver(new HashMap<String, String>());
								xctxt.setNamespaceContext(xmlNsPrefixResolver);
							}

							NodeList depNodeList = testCaseElem.getElementsByTagName(DEPENDENCY);
							int size1 = depNodeList.getLength();
							XObject xpathResultObj = null;												

							boolean dependencySpecified = true;
							if (size1 == 0) {
								dependencySpecified = false;							
								size1 = 1;
							}							

							String xpathExprStr = null;

							boolean xpathParseTimeOut = false;

							elemTestResult = testResultDoc.createElement(TESTRESULT);																																		 
							elemTestResult.setAttribute(TESTNAME, testCaseNameStr);

							boolean isNonXPathTest = false;

							if (dependencySpecified) {
								for (int idx = 0; idx < size1; idx++) {
									Element elem3 = (Element)(depNodeList.item(idx));
									String depType = elem3.getAttribute(TYPE);
									String depValue = elem3.getAttribute(VALUE);
									if (FEATURE.equals(depType) && XPATH10_COMPATIBILITY_MODE.equals(depValue)) {
										elemTestResult.setAttribute(STATUS, SKIPPED);
										elemTestResult.setAttribute(REASON, XPATH10_COMPATIBILITY_MODE);

										elemTestRun.appendChild(elemTestResult);

										break;
									}
									else if (FEATURE.equals(depType) && XS_SCHEMA_VALIDATION.equals(depValue)) {
										elemTestResult.setAttribute(STATUS, SKIPPED);
										elemTestResult.setAttribute(REASON, XS_SCHEMA_VALIDATION);

										elemTestRun.appendChild(elemTestResult);

										break;
									}
									else if ("xs-double-004".equals(testCaseNameStr) || "xs-float-004".equals(testCaseNameStr)) {
										elemTestResult.setAttribute(STATUS, SKIPPED);
										elemTestResult.setAttribute(REASON, "Xalan implements XSD 1.1 rule for +INF");

										elemTestRun.appendChild(elemTestResult);

										break;
									}
								}						   						   

								for (int idx = 0; idx < size1; idx++) {
									Element elem3 = (Element)(depNodeList.item(idx));
									String depType = elem3.getAttribute(TYPE);
									String depValue = elem3.getAttribute(VALUE);							   
									if (SPEC.equals(depType) && !(depValue.contains(XPATH31) || depValue.contains(XPATH30) 
																														|| depValue.contains(XPATH20))) {								   
										isNonXPathTest = true;

										break;
									}
								}
							}

							if (isNonXPathTest || SKIPPED.equals(elemTestResult.getAttribute(STATUS))) {
								node = node.getNextSibling();

								continue;
							}

							for (int idx = 0; idx < size1; idx++) {
								Element elem3 = null;
								String depType = null;
								String depValue = null;

								if (dependencySpecified) {
									elem3 = (Element)(depNodeList.item(idx));
									depType = elem3.getAttribute(TYPE);
									depValue = elem3.getAttribute(VALUE);
								}

								if (!dependencySpecified || XML_VERSION.equals(depType) || XSD_VERSION.equals(depType) || UNICODE_VERSION.equals(depType) 
										                                                || (SPEC.equals(depType) && (depValue.contains(XPATH31) 
										                                                || depValue.contains(XPATH30) 
										                                                || depValue.contains(XPATH20)))
										                                                || (FEATURE.equals(depType) && HIGHER_ORDER_FUNC.equals(depValue))) {								

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
										// avoid XPath parse inf loop.

										ExecutorService executorService = Executors.newSingleThreadExecutor();

										final String xpathExprStr2 = xpathExprStr;
										PrefixResolver xmlNsPrefixResolver2 = xmlNsPrefixResolver;

										Future<XPath> future1 = executorService.submit(() -> {                              	  
											XPath xpathObj2 = new XPath(xpathExprStr2, null, xmlNsPrefixResolver2, XPath.SELECT, null);

											return xpathObj2;
										});

										try {
											// Configuring, XPath parse evaluation timeout
											long timeOut = 10;
											if (m_xslTransformTestSetFilePath.contains("matches.xml") || 
																					m_xslTransformTestSetFilePath.contains("tokenize.xml") || 
																					m_xslTransformTestSetFilePath.contains("replace.xml")) {
												// XPath parse timeout configuration for, functions fn:matches, fn:tokenize, fn:replace
												// which use regex.											
												timeOut = 15;
											}

											xpathObj = future1.get(timeOut, TimeUnit.SECONDS);
										} 
										catch (TimeoutException ex) {
											future1.cancel(true);									    
											xpathParseTimeOut = true;
										}

										if (xpathObj != null) {									   
											xpathResultObj = xpathObj.execute(xctxt, sourceNode, xmlNsPrefixResolver);											
										}									
									}
									catch (TransformerException ex) {
										String errMeg = ex.getMessage();									
										if (!errMeg.contains("FOCH0002")) {									
											String[] errMesgParts = errMeg.split(":");
											if (errMesgParts.length > 2) {
												runTimeErrCode = (errMesgParts[1]).trim();
												if (runTimeErrCode.contains(" ") && (runTimeErrCode.length() > 8)) {
													runTimeErrCode =(errMesgParts[0]).trim(); 
												}
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
											runTimeErrCode = XALAN_ERR_CODE_ABSENT;
										}
									}
									finally {
										if (!xpathVarMap2.isEmpty()) {
											xpathVarMap2.clear();
										}
									}

									break;
								}
							}						

							Node node1 = (testCaseElem.getElementsByTagName(RESULT)).item(0);
							Node child = node1.getFirstChild();
							while (child != null) {
								if (child.getNodeType() == Node.ELEMENT_NODE) {
									Element resultElem1 = (Element)child;
									String nodeName2 = resultElem1.getNodeName();
									String expectedResultStr = null;
									if (ASSERT_XML.equals(nodeName2)) {
										String fileName = resultElem1.getAttribute(FILE);
										if (!EMPTY_STRING.equals(fileName)) {
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
										xpathVarMap.put(new QName(RESULT), xpathResultObj);
										
										try {
											XPath xpathObj = new XPath(expectedResultStr, null, xctxt.getNamespaceContext(), XPath.SELECT, null);
											xpathExpectedObj = xpathObj.execute(xctxt, DTM.NULL, xmlNsPrefixResolver);
										}
										finally {
											xpathVarMap.remove(new QName(RESULT));
										}
									}
									else if ((xpathResultObj != null) && ASSERT_DEEP_EQ.equals(nodeName2)) {
										expectedResultStr = getXPathNormalizedStr(expectedResultStr);
										
										if ((expectedResultStr != null) && !EMPTY_STRING.equals(expectedResultStr)) {
											boolean isExpResultStrFinal = false;
											if (xpathResultObj instanceof ResultSequence) {									   
												isExpResultStrFinal = true;
												if (!expectedResultStr.startsWith("(") && !expectedResultStr.endsWith(")")) {
												   expectedResultStr = "(" + expectedResultStr + ")";
												}
											}											
											else if (xpathResultObj instanceof XPathArray) {
												isExpResultStrFinal = true;
												if (!expectedResultStr.startsWith("[") && !expectedResultStr.endsWith("]")) {
												   expectedResultStr = "[" + expectedResultStr + "]"; 
												}
											}
											
											if (!isExpResultStrFinal) {
												if (expectedResultStr.startsWith("\"") && expectedResultStr.endsWith("\"")) {
													int size2 = expectedResultStr.length();
													expectedResultStr = expectedResultStr.substring(1, size2 - 1);
													expectedResultStr = "'" + expectedResultStr + "'"; 
												}
												else if (!expectedResultStr.startsWith("\'") && !expectedResultStr.endsWith("\'")) {
													expectedResultStr = "'" + expectedResultStr + "'";										
													expectedResultStrUnquoted = true;
												}
											}

											XPath xpathObj = new XPath(expectedResultStr, null, xctxt.getNamespaceContext(), XPath.SELECT, null);
											xpathExpectedObj = xpathObj.execute(xctxt, DTM.NULL, xmlNsPrefixResolver);
										}
									}
									else if ((xpathResultObj != null) && ASSERT_PERMUTATION.equals(nodeName2)) {
										expectedResultStr = "(" + getXPathNormalizedStr(expectedResultStr) + ")";
										
										XPath xpathObj = new XPath(expectedResultStr, null, xctxt.getNamespaceContext(), XPath.SELECT, null);
										xpathExpectedObj = xpathObj.execute(xctxt, DTM.NULL, xmlNsPrefixResolver);
									}
									else if ((xpathResultObj != null) && ASSERT_COUNT.equals(nodeName2)) {										
										XPath xpathObj = new XPath(expectedResultStr, null, xctxt.getNamespaceContext(), XPath.SELECT, null);
										xpathExpectedObj = xpathObj.execute(xctxt, DTM.NULL, xmlNsPrefixResolver);
									}
									else if (!(ASSERT_TRUE.equals(nodeName2) || ASSERT_FALSE.equals(nodeName2) || ASSERT_TYPE.equals(nodeName2) || 
																												  ALL_OF.equals(nodeName2) || ANY_OF.equals(nodeName2) || 
																												  ASSERT_XML.equals(nodeName2) || ERROR.equals(nodeName2)) && 
																												                     (expectedResultStr != null) && !EMPTY_STRING.equals(expectedResultStr)) {
										if (!expectedResultStr.startsWith(XS_COLON) && !expectedResultStr.startsWith(FN_COLON)) {
											if (expectedResultStr.startsWith("\"") && expectedResultStr.endsWith("\"")) {
												int size2 = expectedResultStr.length();
												expectedResultStr = expectedResultStr.substring(1, size2 - 1);
												expectedResultStr = "'" + expectedResultStr + "'"; 
											}
											else if (!expectedResultStr.startsWith("\'") && !expectedResultStr.endsWith("\'")) {
												expectedResultStr = "'" + expectedResultStr + "'";										
												expectedResultStrUnquoted = true;
											}
										}

										XPath xpathObj = new XPath(expectedResultStr, null, xctxt.getNamespaceContext(), XPath.SELECT, null);
										xpathExpectedObj = xpathObj.execute(xctxt, DTM.NULL, xmlNsPrefixResolver);
									}
									else if (ASSERT_STRING_VALUE.equals(nodeName2) && EMPTY_STRING.equals(expectedResultStr)) {
										XPath xpathObj = new XPath("''", null, xctxt.getNamespaceContext(), XPath.SELECT, null);
										xpathExpectedObj = xpathObj.execute(xctxt, DTM.NULL, xmlNsPrefixResolver);
									}

									if (xpathParseTimeOut) {
										elemTestResult.setAttribute(STATUS, SKIPPED);
									}								
									else if (ASSERT_DEEP_EQ.equals(nodeName2)) {
										if (xpathResultObj != null) {
											FuncDeepEqual funcDeepEqual = new FuncDeepEqual();
											funcDeepEqual.setArg(xpathResultObj, 0);
											
											boolean isCompOk = false;
											if (xpathResultObj instanceof XSNumericType) {
												if ((xpathExpectedObj instanceof XSString) || (xpathExpectedObj instanceof XString)) {
												   String str2 = XslTransformEvaluationHelper.getStrVal(xpathExpectedObj);
												   XSDouble xsDouble2 = null;
												   try {
												      xsDouble2 = new XSDouble(str2);
												      funcDeepEqual.setArg(xsDouble2, 1);												      
												      isCompOk = true;
												   }
												   catch (TransformerException ex) {
													  // no op 
												   }
												}
											}
											
											if (!isCompOk) {
											   funcDeepEqual.setArg(xpathExpectedObj, 1);
											}

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

											java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("[\\-]?([0-9]{0,})(\\.)?([0-9]{0,})");
											if ((pattern.matcher(expectedResultStr)).matches()) {
												expectedResultStr = "xs:decimal('" + expectedResultStr + "')";

												XPath xpathObj = new XPath(expectedResultStr, null, xctxt.getNamespaceContext(), XPath.SELECT, null);
												xpathExpectedObj = xpathObj.execute(xctxt, DTM.NULL, xmlNsPrefixResolver);
											}
											else if (expectedResultStr.startsWith(XS_COLON)) {
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
												//String resultStr1 = (XslTransformEvaluationHelper.getStrVal(xpathResultObj)).trim();
												String resultStr1 = null;
												if ((xpathResultObj instanceof XSNormalizedString) || (xpathResultObj instanceof XSToken)) {
												   resultStr1 = (XslTransformEvaluationHelper.getStrVal(xpathResultObj));
												}
												else {
												   resultStr1 = (XslTransformEvaluationHelper.getStrVal(xpathResultObj)).trim();
												}
												
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
											if (xpathResultObj instanceof ResultSequence) {
												ResultSequence rSeq = (ResultSequence)xpathResultObj;
												if (rSeq.size() == 1) {
													xpathResultObj = rSeq.item(0);   
												}
											}

											StringBuffer strBuff = new StringBuffer();                                		
											boolean isXmlCmpSupported = false;                                		
											if (xpathResultObj instanceof ResultSequence) {
												ResultSequence rSeq = (ResultSequence)xpathResultObj;
												int size2 = rSeq.size();
												for (int idx = 0; idx < size2; idx++) {
													XObject xObj = rSeq.item(idx);
													if (xObj instanceof XMLNodeCursorImpl) {
														isXmlCmpSupported = true;

														int nodeHandle = ((XMLNodeCursorImpl)xObj).asNode(xctxt);
														DTM dtm = xctxt.getDTM(nodeHandle);
														Node node2 = dtm.getNode(nodeHandle);
														String xmlStr = XslTransformEvaluationHelper.serializeXmlDomElementNode(node2);
														int idx1 = xmlStr.indexOf("?>");
														xmlStr = (xmlStr.substring(idx1 + 2)).trim();
														strBuff.append(xmlStr);
													}
													else {
														// xpathResultObj, which is a sequence not having 
														// all its items as nodes, isn't supported here.                                				  
														isXmlCmpSupported = false;

														break; 
													}
												}                                		                                   		   
											}

											if (isXmlCmpSupported) {
												String resultXmlFragStr = strBuff.toString();
												expectedResultStr = expectedResultStr.trim();

												if (resultXmlFragStr.equals(expectedResultStr)) {
													elemTestResult.setAttribute(STATUS, PASS);
												}
												else {
													elemTestResult.setAttribute(STATUS, FAIL);
												}
											}                                		
											else if (xpathResultObj instanceof XMLNodeCursorImpl) {																																			
												expectedResultStr = expectedResultStr.replaceAll(">\\s*<", "><");
												expectedResultStr = expectedResultStr.trim();

												XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xpathResultObj;
												DTMCursorIterator dtmCursorIterator = xmlNodeCursorImpl.iter();
												StringBuffer strBuff2 = new StringBuffer();
												int nextNode = DTM.NULL;
												while ((nextNode = dtmCursorIterator.nextNode()) != DTM.NULL) {
													DTM dtm = xctxt.getDTM(nextNode);
													short nodeType = dtm.getNodeType(nextNode);
													if ((nodeType == DTM.ELEMENT_NODE) || (nodeType == DTM.PROCESSING_INSTRUCTION_NODE)
															                           || (nodeType == DTM.COMMENT_NODE)) {
														Node node2 = dtm.getNode(nextNode);
														String str1 = XslTransformEvaluationHelper.serializeXmlDomElementNode(node2);
														str1 = str1.replaceAll(">\\s*<", "><");
														int idx = str1.indexOf("?>");
														str1 = str1.substring(idx + 2);
														str1 = str1.trim();
														strBuff2.append(str1);
													}
													else if (nodeType == DTM.TEXT_NODE) {
														Node node2 = dtm.getNode(nextNode);
														String str1 = node2.getTextContent();
														str1 = str1.trim();
														strBuff2.append(str1);
													}
												}

												String resultXmlStr = strBuff2.toString();

												if (m_xslTransformTestSetFilePath.contains("analyze-string.xml")) {
													String ignorePrefixesStr = resultElem1.getAttribute(IGNORE_PREFIXES);
													if (TRUE.equals(ignorePrefixesStr)) {
														expectedResultStr = expectedResultStr.replace(org.apache.xalan.templates.Constants.XMLNS_COLON + "fn", XMLConstants.XMLNS_ATTRIBUTE);
														expectedResultStr = expectedResultStr.replace(FN_COLON, EMPTY_STRING);
													}
												}

												try {                                					                                				                                					                                					
													byte[] byteArr = expectedResultStr.getBytes(StandardCharsets.UTF_8);
													InputStream inpStream1 = new ByteArrayInputStream(byteArr);
													Document document1 = null;

													try {
														document1 = m_xmlDocumentBuilder.parse(inpStream1);	
														
														Set<String> usedPrefixes = new HashSet<>();														
														
														getUsedXmlNsPrefixes(document1.getDocumentElement(), usedPrefixes);																																										
														removeUnusedXmlNsDeclarations(document1.getDocumentElement(), usedPrefixes);
													}
													catch (Exception ex) {
														expectedResultStr = UNLIKELY_XML_ELEM_START_TAG + expectedResultStr + UNLIKELY_XML_ELEM_END_TAG;
														byteArr = expectedResultStr.getBytes(StandardCharsets.UTF_8);
														inpStream1 = new ByteArrayInputStream(byteArr);

														document1 = m_xmlDocumentBuilder.parse(inpStream1);
														
                                                        Set<String> usedPrefixes = new HashSet<>();
                                                        
                                                        getUsedXmlNsPrefixes(document1.getDocumentElement(), usedPrefixes);														
                                                        removeUnusedXmlNsDeclarations(document1.getDocumentElement(), usedPrefixes);

														resultXmlStr = (UNLIKELY_XML_ELEM_START_TAG + resultXmlStr + UNLIKELY_XML_ELEM_END_TAG); 
													}

													document1.normalizeDocument();
													Node nodeA = document1.getDocumentElement();

													resultXmlStr = resultXmlStr.replaceAll(">\\s*<", "><");

													byte[] byteArr1 = resultXmlStr.getBytes(StandardCharsets.UTF_8);
													InputStream inpStream2 = new ByteArrayInputStream(byteArr1);                                    				
													Document document2 = m_xmlDocumentBuilder.parse(inpStream2);
													
													Set<String> usedPrefixes = new HashSet<>();
													
													getUsedXmlNsPrefixes(document1.getDocumentElement(), usedPrefixes);													
													removeUnusedXmlNsDeclarations(document2.getDocumentElement(), usedPrefixes);
													
													document2.normalizeDocument();                                					                                					

													Node nodeB = document2.getDocumentElement(); 

													if (nodeA.isEqualNode(nodeB)) {
														elemTestResult.setAttribute(STATUS, PASS);
													}
													else {
														elemTestResult.setAttribute(STATUS, FAIL);
													}
												}
												catch (Exception ex) {
													// no op                                					
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
													xpathVarMap.put(new QName(RESULT), xpathResultObj);
													try {
														XPath xpathObj = new XPath(expectedResultStr2, null, xctxt.getNamespaceContext(), XPath.SELECT, null);
														xpathExpectedObj = xpathObj.execute(xctxt, DTM.NULL, xmlNsPrefixResolver);
													}
													finally {
														xpathVarMap.remove(new QName(RESULT));
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
														expectedResultStr2 = getXPathNormalizedStr(expectedResultStr2);
														if ((expectedResultStr2 != null) && !EMPTY_STRING.equals(expectedResultStr2)) {
															boolean isExpResultStrFinal = false;
															if (xpathResultObj instanceof ResultSequence) {									   
																isExpResultStrFinal = true;
																if (!expectedResultStr2.startsWith("(") && !expectedResultStr2.endsWith(")")) {
																   expectedResultStr2 = "(" + expectedResultStr2 + ")";
																}
															}
															else if (xpathResultObj instanceof XPathArray) {
																isExpResultStrFinal = true;
																if (!expectedResultStr2.startsWith("[") && !expectedResultStr2.endsWith("]")) {
																   expectedResultStr2 = "[" + expectedResultStr2 + "]";
																}
                                                            }
															
															if (!isExpResultStrFinal) {
																if (expectedResultStr2.startsWith("\"") && expectedResultStr2.endsWith("\"")) {
																	int size3 = expectedResultStr2.length();
																	expectedResultStr2 = expectedResultStr2.substring(1, size3 - 1);
																	expectedResultStr2 = "'" + expectedResultStr2 + "'"; 
																}
																else if (!expectedResultStr2.startsWith("\'") && !expectedResultStr2.endsWith("\'")) {
																	expectedResultStr2 = "'" + expectedResultStr2 + "'";										
																	expectedResultStrUnquoted = true;
																}
															}

															XPath xpathObj = new XPath(expectedResultStr2, null, xctxt.getNamespaceContext(), XPath.SELECT, null);
															xpathExpectedObj = xpathObj.execute(xctxt, DTM.NULL, xmlNsPrefixResolver);
														}

														FuncDeepEqual funcDeepEqual = new FuncDeepEqual();
														funcDeepEqual.setArg(xpathResultObj, 0);
														
														boolean isCompOk = false;
														if (xpathResultObj instanceof XSNumericType) {
															if ((xpathExpectedObj instanceof XSString) || (xpathExpectedObj instanceof XString)) {
															   String str2 = XslTransformEvaluationHelper.getStrVal(xpathExpectedObj);
															   XSDouble xsDouble2 = null;
															   try {
															      xsDouble2 = new XSDouble(str2);
															      funcDeepEqual.setArg(xsDouble2, 1);												      
															      isCompOk = true;
															   }
															   catch (TransformerException ex) {
																  // no op 
															   }
															}
														}
														
														if (!isCompOk) {
														   funcDeepEqual.setArg(xpathExpectedObj, 1);
														}

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

													if (!expectedResultStr2.startsWith(XS_COLON) && !expectedResultStr2.startsWith(FN_COLON)) {      											                                        	  
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

															java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("[\\-]?([0-9]{0,})(\\.)?([0-9]{0,})");
															if ((pattern.matcher(expectedResultStr2)).matches()) {
																expectedResultStr2 = "xs:decimal('" + expectedResultStr2 + "')";
															}
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

														if (EMPTY_STRING.equals(expectedResultStr2)) {
															expectedResultStr2 = "''";  
														}                                                  
														else if (!(expectedResultStr2.startsWith("'") && expectedResultStr2.endsWith("'"))) {
															expectedResultStr2 = "'" + expectedResultStr2 + "'";  
														}
														else if (!(expectedResultStr2.startsWith("\"") && expectedResultStr2.endsWith("\""))) {
															expectedResultStr2 = "'" + expectedResultStr2 + "'"; 
														}

														XPath xpathObj = new XPath(expectedResultStr2, null, xctxt.getNamespaceContext(), XPath.SELECT, null);
														xpathExpectedObj = xpathObj.execute(xctxt, DTM.NULL, xmlNsPrefixResolver);

														String expectedStr1 = XslTransformEvaluationHelper.getStrVal(xpathExpectedObj);
														//String resultStr1 = (XslTransformEvaluationHelper.getStrVal(xpathResultObj)).trim();
														//String resultStr1 = (XslTransformEvaluationHelper.getStrVal(xpathResultObj));
														String resultStr1 = null;
														if ((xpathResultObj instanceof XSNormalizedString) || (xpathResultObj instanceof XSToken)) {
														   resultStr1 = (XslTransformEvaluationHelper.getStrVal(xpathResultObj));
														}
														else {
														   resultStr1 = (XslTransformEvaluationHelper.getStrVal(xpathResultObj)).trim();
														}
														
														if (!expectedStr1.equals(resultStr1)) {
															isXslTestPass = false;

															break; 
														} 
													} 
												}
												else if (ASSERT_XML.equals(nodeName3)) {
													if (xpathResultObj != null) {
														if (xpathResultObj instanceof ResultSequence) {
															ResultSequence rSeq = (ResultSequence)xpathResultObj;
															if (rSeq.size() == 1) {
																xpathResultObj = rSeq.item(0);   
															}
														}

														StringBuffer strBuff = new StringBuffer();                                		
														boolean isXmlCmpSupported = false;                                		
														if (xpathResultObj instanceof ResultSequence) {
															ResultSequence rSeq = (ResultSequence)xpathResultObj;
															int size3 = rSeq.size();
															for (int idx2 = 0; idx2 < size3; idx2++) {
																XObject xObj = rSeq.item(idx2);
																if (xObj instanceof XMLNodeCursorImpl) {
																	isXmlCmpSupported = true;

																	int nodeHandle = ((XMLNodeCursorImpl)xObj).asNode(xctxt);
																	DTM dtm = xctxt.getDTM(nodeHandle);
																	Node node3 = dtm.getNode(nodeHandle);
																	String xmlStr = XslTransformEvaluationHelper.serializeXmlDomElementNode(node3);
																	int idx1 = xmlStr.indexOf("?>");
																	xmlStr = (xmlStr.substring(idx1 + 2)).trim();
																	strBuff.append(xmlStr);
																}
																else {
																	// xpathResultObj, which is a sequence not having 
																	// all its items as nodes, isn't supported here.                                				  
																	isXmlCmpSupported = false;

																	break; 
																}
															}                                		                                   		   
														}

														if (isXmlCmpSupported) {
															String resultXmlFragStr = strBuff.toString();
															expectedResultStr2 = expectedResultStr2.trim();

															if (!resultXmlFragStr.equals(expectedResultStr2)) {
																isXslTestPass = false;

																break;
															}                                        			  
														}                                        		  
														else if (xpathResultObj instanceof XMLNodeCursorImpl) {																								
															expectedResultStr2 = expectedResultStr2.replaceAll(">\\s*<", "><");
															expectedResultStr2 = expectedResultStr2.trim();

															XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xpathResultObj;
															DTMCursorIterator dtmCursorIterator = xmlNodeCursorImpl.iter();
															StringBuffer strBuff2 = new StringBuffer();
															int nextNode = DTM.NULL;
															while ((nextNode = dtmCursorIterator.nextNode()) != DTM.NULL) {
																DTM dtm = xctxt.getDTM(nextNode);
																short nodeType = dtm.getNodeType(nextNode);
																if ((nodeType == DTM.ELEMENT_NODE) || (nodeType == DTM.PROCESSING_INSTRUCTION_NODE)
																		                           || (nodeType == DTM.COMMENT_NODE)) {
																	Node node3 = dtm.getNode(nextNode);
																	String str1 = XslTransformEvaluationHelper.serializeXmlDomElementNode(node3);
																	str1 = str1.replaceAll(">\\s*<", "><");
																	int idx2 = str1.indexOf("?>");
																	str1 = str1.substring(idx2 + 2);
																	str1 = str1.trim();
																	strBuff2.append(str1);
																}
																else if (nodeType == DTM.TEXT_NODE) {
																	Node node3 = dtm.getNode(nextNode);
																	String str1 = node3.getTextContent();
																	str1 = str1.trim();
																	strBuff2.append(str1);
																}
															}

															String resultXmlStr = strBuff2.toString();                                 																	

															if (m_xslTransformTestSetFilePath.contains("analyze-string.xml")) {
																String ignorePrefixesStr = elNode1.getAttribute(IGNORE_PREFIXES);
																if (TRUE.equals(ignorePrefixesStr)) {
																	expectedResultStr2 = expectedResultStr2.replace(org.apache.xalan.templates.Constants.XMLNS_COLON + "fn", XMLConstants.XMLNS_ATTRIBUTE);
																	expectedResultStr2 = expectedResultStr2.replace(FN_COLON, EMPTY_STRING);
																}
															}

															try {                                					                                				                                					                                					
																byte[] byteArr = expectedResultStr2.getBytes(StandardCharsets.UTF_8);
																InputStream inpStream1 = new ByteArrayInputStream(byteArr);
																Document document1 = null;

																try {
																	document1 = m_xmlDocumentBuilder.parse(inpStream1);
																	
																	Set<String> usedPrefixes = new HashSet<>();														
																	
																	getUsedXmlNsPrefixes(document1.getDocumentElement(), usedPrefixes);																																										
																	removeUnusedXmlNsDeclarations(document1.getDocumentElement(), usedPrefixes);
																}
																catch (Exception ex) {
																	expectedResultStr2 = UNLIKELY_XML_ELEM_START_TAG + expectedResultStr2 + UNLIKELY_XML_ELEM_END_TAG;
																	byteArr = expectedResultStr2.getBytes(StandardCharsets.UTF_8);
																	inpStream1 = new ByteArrayInputStream(byteArr);

																	document1 = m_xmlDocumentBuilder.parse(inpStream1);
																	
																	Set<String> usedPrefixes = new HashSet<>();														
																	
																	getUsedXmlNsPrefixes(document1.getDocumentElement(), usedPrefixes);																																										
																	removeUnusedXmlNsDeclarations(document1.getDocumentElement(), usedPrefixes);

																	resultXmlStr = (UNLIKELY_XML_ELEM_START_TAG + resultXmlStr + UNLIKELY_XML_ELEM_END_TAG); 
																}

																document1.normalizeDocument();
																Node nodeA = document1.getDocumentElement();

																resultXmlStr = resultXmlStr.replaceAll(">\\s*<", "><");

																byte[] byteArr1 = resultXmlStr.getBytes(StandardCharsets.UTF_8);
																InputStream inpStream2 = new ByteArrayInputStream(byteArr1);                                    				
																
																Document document2 = m_xmlDocumentBuilder.parse(inpStream2);
																
																Set<String> usedPrefixes = new HashSet<>();														
																
																getUsedXmlNsPrefixes(document2.getDocumentElement(), usedPrefixes);																																										
																removeUnusedXmlNsDeclarations(document2.getDocumentElement(), usedPrefixes);
																
																document2.normalizeDocument();                                					                                					

																Node nodeB = document2.getDocumentElement(); 

																if (!nodeA.isEqualNode(nodeB)) {
																	isXslTestPass = false;

																	break;
																}																	
															}
															catch (Exception ex) {
																// no op                                					
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
													expectedErrCode = elNode1.getAttribute("code");
													if ((runTimeErrCode != null) && !runTimeErrCode.equals(expectedErrCode)) {
														isXslTestPass = false;

														break;  
													}
												}
												else if (ASSERT_PERMUTATION.equals(nodeName3)) {
													if (xpathResultObj != null) {														
														expectedResultStr2 = "(" + getXPathNormalizedStr(expectedResultStr2) + ")";
														
														XPath xpathObj = new XPath(expectedResultStr2, null, xctxt.getNamespaceContext(), XPath.SELECT, null);
														XObject xpathExpectedObj2 = xpathObj.execute(xctxt, DTM.NULL, xmlNsPrefixResolver);
														
														if (xpathResultObj instanceof ResultSequence) {
															ResultSequence rSeq1 = (ResultSequence)xpathResultObj;
															ResultSequence rSeq2 = (ResultSequence)xpathExpectedObj2;												
															
															int size3 = rSeq1.size();
															int size4 = rSeq2.size();												
															if (size3 == size4) {
															   List<String> list1 = new ArrayList<String>();
															   for (int idx2 = 0; idx2 < size3; idx++) {
																  XObject xObj = rSeq1.item(idx2);
																  String str1 = XslTransformEvaluationHelper.getStrVal(xObj);
																  list1.add(str1);
															   }
															   
															   list1.sort(null);
															   
															   List<String> list2 = new ArrayList<String>();
															   for (int idx3 = 0; idx3 < size4; idx3++) {
																  XObject xObj = rSeq2.item(idx3);
																  String str2 = XslTransformEvaluationHelper.getStrVal(xObj);
																  list2.add(str2);
															   }
															   
															   list2.sort(null);
															   
															   if (!list1.equals(list2)) {
																   isXslTestPass = false;

																   break;   
															   }
															}
															else {
																isXslTestPass = false;

																break;
															}
														}
														else {
															isXslTestPass = false;

															break;
														}				
													}
													else {
														isXslTestPass = false;

														break;
													}
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
													xpathVarMap.put(new QName(RESULT), xpathResultObj);
													try {
														XPath xpathObj = new XPath(expectedResultStr2, null, xctxt.getNamespaceContext(), XPath.SELECT, null);
														xpathExpectedObj = xpathObj.execute(xctxt, DTM.NULL, xmlNsPrefixResolver);
													}
													finally {
														xpathVarMap.remove(new QName(RESULT));
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
														expectedResultStr2 = getXPathNormalizedStr(expectedResultStr2);
														if ((expectedResultStr2 != null) && !EMPTY_STRING.equals(expectedResultStr2)) {
															boolean isExpResultStrFinal = false;
															if (xpathResultObj instanceof ResultSequence) {									   
																isExpResultStrFinal = true;
																if (!expectedResultStr2.startsWith("(") && !expectedResultStr2.endsWith(")")) {
																   expectedResultStr2 = "(" + expectedResultStr2 + ")";
																}
															}
															else if (xpathResultObj instanceof XPathArray) {
																isExpResultStrFinal = true;
																if (!expectedResultStr2.startsWith("[") && !expectedResultStr2.endsWith("]")) {
																   expectedResultStr2 = "[" + expectedResultStr2 + "]";
																}
                                                            }
															
															if (!isExpResultStrFinal) {
																if (expectedResultStr2.startsWith("\"") && expectedResultStr2.endsWith("\"")) {
																	int size3 = expectedResultStr2.length();
																	expectedResultStr2 = expectedResultStr2.substring(1, size3 - 1);
																	expectedResultStr2 = "'" + expectedResultStr2 + "'"; 
																}
																else if (!expectedResultStr2.startsWith("\'") && !expectedResultStr2.endsWith("\'")) {
																	expectedResultStr2 = "'" + expectedResultStr2 + "'";										
																	expectedResultStrUnquoted = true;
																}
															}

															XPath xpathObj = new XPath(expectedResultStr2, null, xctxt.getNamespaceContext(), XPath.SELECT, null);
															xpathExpectedObj = xpathObj.execute(xctxt, DTM.NULL, xmlNsPrefixResolver);
														}

														FuncDeepEqual funcDeepEqual = new FuncDeepEqual();
														funcDeepEqual.setArg(xpathResultObj, 0);
														
														boolean isCompOk = false;
														if (xpathResultObj instanceof XSNumericType) {
															if ((xpathExpectedObj instanceof XSString) || (xpathExpectedObj instanceof XString)) {
															   String str2 = XslTransformEvaluationHelper.getStrVal(xpathExpectedObj);
															   XSDouble xsDouble2 = null;
															   try {
															      xsDouble2 = new XSDouble(str2);
															      funcDeepEqual.setArg(xsDouble2, 1);												      
															      isCompOk = true;
															   }
															   catch (TransformerException ex) {
																  // no op 
															   }
															}
														}
														
														if (!isCompOk) {
														   funcDeepEqual.setArg(xpathExpectedObj, 1);
														}

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

													if (!expectedResultStr2.startsWith(XS_COLON) && !expectedResultStr2.startsWith(FN_COLON)) { 
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

															java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("[\\-]?([0-9]{0,})(\\.)?([0-9]{0,})");
															if ((pattern.matcher(expectedResultStr2)).matches()) {
																expectedResultStr2 = "xs:decimal('" + expectedResultStr2 + "')";
															}
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

														if (EMPTY_STRING.equals(expectedResultStr2)) {
															expectedResultStr2 = "''";  
														}
														else if (!(expectedResultStr2.startsWith("'") && expectedResultStr2.endsWith("'"))) {
															expectedResultStr2 = "'" + expectedResultStr2 + "'";  
														}
														else if (!(expectedResultStr2.startsWith("\"") && expectedResultStr2.endsWith("\""))) {
															expectedResultStr2 = "'" + expectedResultStr2 + "'"; 
														}

														XPath xpathObj = new XPath(expectedResultStr2, null, xctxt.getNamespaceContext(), XPath.SELECT, null);
														xpathExpectedObj = xpathObj.execute(xctxt, DTM.NULL, xmlNsPrefixResolver);

														String expectedStr1 = XslTransformEvaluationHelper.getStrVal(xpathExpectedObj);
														//String resultStr1 = (XslTransformEvaluationHelper.getStrVal(xpathResultObj)).trim();
														//String resultStr1 = (XslTransformEvaluationHelper.getStrVal(xpathResultObj));
														String resultStr1 = null;
														if ((xpathResultObj instanceof XSNormalizedString) || (xpathResultObj instanceof XSToken)) {
														   resultStr1 = (XslTransformEvaluationHelper.getStrVal(xpathResultObj));
														}
														else {
														   resultStr1 = (XslTransformEvaluationHelper.getStrVal(xpathResultObj)).trim();
														}
														
														if (expectedStr1.equals(resultStr1)) {
															isXslTestPass = true;

															break; 
														} 
													} 
												}
												else if (ASSERT_XML.equals(nodeName3)) {
													if (xpathResultObj != null) {
														if (xpathResultObj instanceof ResultSequence) {
															ResultSequence rSeq = (ResultSequence)xpathResultObj;
															if (rSeq.size() == 1) {
																xpathResultObj = rSeq.item(0);   
															}
														}

														StringBuffer strBuff = new StringBuffer();                                		
														boolean isXmlCmpSupported = false;                                		
														if (xpathResultObj instanceof ResultSequence) {
															ResultSequence rSeq = (ResultSequence)xpathResultObj;
															int size3 = rSeq.size();
															for (int idx2 = 0; idx2 < size3; idx2++) {
																XObject xObj = rSeq.item(idx2);
																if (xObj instanceof XMLNodeCursorImpl) {
																	isXmlCmpSupported = true;

																	int nodeHandle = ((XMLNodeCursorImpl)xObj).asNode(xctxt);
																	DTM dtm = xctxt.getDTM(nodeHandle);
																	Node node3 = dtm.getNode(nodeHandle);
																	String xmlStr = XslTransformEvaluationHelper.serializeXmlDomElementNode(node3);
																	int idx1 = xmlStr.indexOf("?>");
																	xmlStr = (xmlStr.substring(idx1 + 2)).trim();
																	strBuff.append(xmlStr);
																}
																else {
																	// xpathResultObj, which is a sequence not having 
																	// all its items as nodes, isn't supported here.                                				  
																	isXmlCmpSupported = false;

																	break; 
																}
															}                                		                                   		   
														}

														if (isXmlCmpSupported) {
															String resultXmlFragStr = strBuff.toString();
															expectedResultStr2 = expectedResultStr2.trim();

															if (resultXmlFragStr.equals(expectedResultStr2)) {
																isXslTestPass = true;

																break;
															}                                        			  
														}                                        		  
														else if (xpathResultObj instanceof XMLNodeCursorImpl) {
															expectedResultStr2 = expectedResultStr2.replaceAll(">\\s*<", "><");
															expectedResultStr2 = expectedResultStr2.trim();

															XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xpathResultObj;
															DTMCursorIterator dtmCursorIterator = xmlNodeCursorImpl.iter();
															StringBuffer strBuff2 = new StringBuffer();
															int nextNode = DTM.NULL;
															while ((nextNode = dtmCursorIterator.nextNode()) != DTM.NULL) {
																DTM dtm = xctxt.getDTM(nextNode);
																short nodeType = dtm.getNodeType(nextNode);
																if ((nodeType == DTM.ELEMENT_NODE) || (nodeType == DTM.PROCESSING_INSTRUCTION_NODE)
																		                           || (nodeType == DTM.COMMENT_NODE)) {
																	Node node3 = dtm.getNode(nextNode);
																	String str1 = XslTransformEvaluationHelper.serializeXmlDomElementNode(node3);
																	str1 = str1.replaceAll(">\\s*<", "><");
																	int idx2 = str1.indexOf("?>");
																	str1 = str1.substring(idx2 + 2);
																	str1 = str1.trim();
																	strBuff2.append(str1);
																}
																else if (nodeType == DTM.TEXT_NODE) {
																	Node node3 = dtm.getNode(nextNode);
																	String str1 = node3.getTextContent();
																	str1 = str1.trim();
																	strBuff2.append(str1);
																}
															}

															String resultXmlStr = strBuff2.toString();                                  																	

															if (m_xslTransformTestSetFilePath.contains("analyze-string.xml")) {
																String ignorePrefixesStr = elNode1.getAttribute(IGNORE_PREFIXES);
																if (TRUE.equals(ignorePrefixesStr)) {
																	expectedResultStr2 = expectedResultStr2.replace(org.apache.xalan.templates.Constants.XMLNS_COLON + "fn", XMLConstants.XMLNS_ATTRIBUTE);
																	expectedResultStr2 = expectedResultStr2.replace(FN_COLON, EMPTY_STRING);
																}
															}

															try {                                					                                				                                					                                					
																byte[] byteArr = expectedResultStr2.getBytes(StandardCharsets.UTF_8);
																InputStream inpStream1 = new ByteArrayInputStream(byteArr);
																Document document1 = null;

																try {
																	document1 = m_xmlDocumentBuilder.parse(inpStream1);

																	Set<String> usedPrefixes = new HashSet<>();														

																	getUsedXmlNsPrefixes(document1.getDocumentElement(), usedPrefixes);																																										
																	removeUnusedXmlNsDeclarations(document1.getDocumentElement(), usedPrefixes);
																}
																catch (Exception ex) {
																	expectedResultStr2 = UNLIKELY_XML_ELEM_START_TAG + expectedResultStr2 + UNLIKELY_XML_ELEM_END_TAG;
																	byteArr = expectedResultStr2.getBytes(StandardCharsets.UTF_8);
																	inpStream1 = new ByteArrayInputStream(byteArr);

																	document1 = m_xmlDocumentBuilder.parse(inpStream1);

																	Set<String> usedPrefixes = new HashSet<>();														

																	getUsedXmlNsPrefixes(document1.getDocumentElement(), usedPrefixes);																																										
																	removeUnusedXmlNsDeclarations(document1.getDocumentElement(), usedPrefixes);

																	resultXmlStr = (UNLIKELY_XML_ELEM_START_TAG + resultXmlStr + UNLIKELY_XML_ELEM_END_TAG); 
																}

																document1.normalizeDocument();
																Node nodeA = document1.getDocumentElement();

																resultXmlStr = resultXmlStr.replaceAll(">\\s*<", "><");

																byte[] byteArr1 = resultXmlStr.getBytes(StandardCharsets.UTF_8);
																InputStream inpStream2 = new ByteArrayInputStream(byteArr1);                                    				

																Document document2 = m_xmlDocumentBuilder.parse(inpStream2);

																Set<String> usedPrefixes = new HashSet<>();														

																getUsedXmlNsPrefixes(document2.getDocumentElement(), usedPrefixes);																																										
																removeUnusedXmlNsDeclarations(document2.getDocumentElement(), usedPrefixes);

																document2.normalizeDocument();                                					                                					

																Node nodeB = document2.getDocumentElement(); 

																if (nodeA.isEqualNode(nodeB)) {
																	isXslTestPass = true;

																	break;
																}																	
															}
															catch (Exception ex) {
																// no op                                					
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
													expectedErrCode = elNode1.getAttribute("code");
													if ((runTimeErrCode != null) && runTimeErrCode.equals(expectedErrCode)) {
														isXslTestPass = true;

														break;  
													}
												}
												else if (ASSERT_PERMUTATION.equals(nodeName3)) {
													if (xpathResultObj != null) {														
														expectedResultStr2 = "(" + getXPathNormalizedStr(expectedResultStr2) + ")";
														
														XPath xpathObj = new XPath(expectedResultStr2, null, xctxt.getNamespaceContext(), XPath.SELECT, null);
														XObject xpathExpectedObj2 = xpathObj.execute(xctxt, DTM.NULL, xmlNsPrefixResolver);
														
														if (xpathResultObj instanceof ResultSequence) {
															ResultSequence rSeq1 = (ResultSequence)xpathResultObj;
															ResultSequence rSeq2 = (ResultSequence)xpathExpectedObj2;												
															
															int size3 = rSeq1.size();
															int size4 = rSeq2.size();												
															if (size3 == size4) {
															   List<String> list1 = new ArrayList<String>();
															   for (int idx2 = 0; idx2 < size3; idx++) {
																  XObject xObj = rSeq1.item(idx2);
																  String str1 = XslTransformEvaluationHelper.getStrVal(xObj);
																  list1.add(str1);
															   }
															   
															   list1.sort(null);
															   
															   List<String> list2 = new ArrayList<String>();
															   for (int idx3 = 0; idx3 < size4; idx3++) {
																  XObject xObj = rSeq2.item(idx3);
																  String str2 = XslTransformEvaluationHelper.getStrVal(xObj);
																  list2.add(str2);
															   }
															   
															   list2.sort(null);
															   
															   if (list1.equals(list2)) {
																   isXslTestPass = true;

																   break;   
															   }
															}															
														}																		
													}													
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
										if (xpathResultObj != null) {
											if (xpathResultObj instanceof ResultSequence) {
												ResultSequence rSeq1 = (ResultSequence)xpathResultObj;
												ResultSequence rSeq2 = (ResultSequence)xpathExpectedObj;												
												
												int size2 = rSeq1.size();
												int size3 = rSeq2.size();												
												if (size2 == size3) {
												   List<String> list1 = new ArrayList<String>();
												   for (int idx = 0; idx < size2; idx++) {
													  XObject xObj = rSeq1.item(idx);
													  String str1 = XslTransformEvaluationHelper.getStrVal(xObj);
													  list1.add(str1);
												   }
												   
												   list1.sort(null);
												   
												   List<String> list2 = new ArrayList<String>();
												   for (int idx = 0; idx < size3; idx++) {
													  XObject xObj = rSeq2.item(idx);
													  String str2 = XslTransformEvaluationHelper.getStrVal(xObj);
													  list2.add(str2);
												   }
												   
												   list2.sort(null);
												   
												   if (list1.equals(list2)) {
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
											else {
												elemTestResult.setAttribute(STATUS, FAIL);
											}				
										}
										else {
											elemTestResult.setAttribute(STATUS, FAIL);
										}
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
						finally {
							xctxt.popCurrentNode();							
							XslTransformData.m_xmlSystemId = null;
						}
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
    	
    	elem.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, org.apache.xalan.templates.Constants.XMLNS_COLON + "fn", XPathStaticContext.XPATH_BUILT_IN_FUNCS_NS_URI);
    	elem.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, org.apache.xalan.templates.Constants.XMLNS_COLON + "math", XPathStaticContext.XPATH_BUILT_IN_MATH_FUNCS_NS_URI);
    	elem.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, org.apache.xalan.templates.Constants.XMLNS_COLON + "map", XPathStaticContext.XPATH_BUILT_IN_MAP_FUNCS_NS_URI);
    	elem.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, org.apache.xalan.templates.Constants.XMLNS_COLON + "array", XPathStaticContext.XPATH_BUILT_IN_ARRAY_FUNCS_NS_URI);
    	elem.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, org.apache.xalan.templates.Constants.XMLNS_COLON + "err", org.apache.xalan.templates.Constants.XSL_ERROR_NAMESACE);
    	elem.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, org.apache.xalan.templates.Constants.XMLNS_COLON + "xs", XMLConstants.W3C_XML_SCHEMA_NS_URI);
    	
    	if (nsMap.size() > 0) {
    	   Set<Entry<String, String>> mapEntrySet1 = nsMap.entrySet();
    	   Iterator<Entry<String, String>> iter1 = mapEntrySet1.iterator();
    	   while (iter1.hasNext()) {
    		  Entry<String, String> mapEntry1 = iter1.next();
    		  String prefix = mapEntry1.getKey();
    		  String uri = mapEntry1.getValue();
    		  
    		  elem.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, org.apache.xalan.templates.Constants.XMLNS_COLON + prefix, uri);
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
    		
    		XslTransformData.m_xmlSystemId = sourceDocUrlStr;

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
		
    	xpathExprStr = xpathExprStr.replace("Q{" + XPathStaticContext.XPATH_BUILT_IN_FUNCS_NS_URI + "}", EMPTY_STRING);
    	xpathExprStr = xpathExprStr.replace("Q{" + XPathStaticContext.XPATH_BUILT_IN_MATH_FUNCS_NS_URI + "}", "math:");
    	xpathExprStr = xpathExprStr.replace("Q{" + XPathStaticContext.XPATH_BUILT_IN_MAP_FUNCS_NS_URI + "}", "map:");
    	xpathExprStr = xpathExprStr.replace("Q{" + XPathStaticContext.XPATH_BUILT_IN_ARRAY_FUNCS_NS_URI + "}", "array:");
    	xpathExprStr = xpathExprStr.replace("Q{" + org.apache.xalan.templates.Constants.XSL_ERROR_NAMESACE + "}", "err:");
    	xpathExprStr = xpathExprStr.replace("Q{" + XMLConstants.W3C_XML_SCHEMA_NS_URI + "}", XS_COLON);
    	
    	result = xpathExprStr; 
		
		return result;
	}
    
    /**
     * Method definition, to remove from the supplied
     * XML DOM node and document tree attached to this node,
     * unused XML namespace declarations.
     * 
     * @param node                         The supplied XML node                       
     * @param usedPrefixes                 java.util.Set object representing
     *                                     XML unused namespace prefixes.
     */
    private void removeUnusedXmlNsDeclarations(Node node, Set<String> usedPrefixes) {
        
    	if (node != null) {
    		if (node.getNodeType() == Node.ELEMENT_NODE) {
    			Element elem = (Element)node;
    			
    			NamedNodeMap attributes = elem.getAttributes();

    			boolean continueLoop = true;
    			
    			while (continueLoop) {
    				int size1 = attributes.getLength();
    				boolean xmlAttrRemove = false;
    				for (int idx = 0; idx < size1; idx++) {
    					Node attr = attributes.item(idx);
    					String name = attr.getNodeName();
    					
    					if (name.startsWith("xmlns:")) {
    						String prefix = name.substring(6);
    						if (!usedPrefixes.contains(prefix)) {
    							elem.removeAttributeNode((Attr)attr);    							    							    							    							    							
    							attributes = elem.getAttributes();    							
    							
    							xmlAttrRemove = true;
    							
    							break;
    						}
    					}
    					else if (name.equals(XMLConstants.XMLNS_ATTRIBUTE) && (elem.getPrefix() != null)) {
    						// no op
    					}
    				}
    				
    				if (!xmlAttrRemove) {
    					break;
    				}
    			}
    		}
    		
    		Node child = node.getFirstChild();
    		
    		while (child != null) {
    			removeUnusedXmlNsDeclarations(child, usedPrefixes);

    			child = child.getNextSibling();
    		}

    		node = node.getNextSibling();
        }
    }
    
    /**
     * Method definition, to get XML unused namespace prefixes
     * within the supplied XML DOM node and the document tree 
     * attached to the node.
     * 
     * @param node                       The supplied XML dom node.
     * @param usedPrefixes               java.util.Set object representing
     *                                   XML unused namespace prefixes.
     */
    private void getUsedXmlNsPrefixes(Node node, Set<String> usedPrefixes) {
        
    	if (node != null) {
    		if (node.getNodeType() == Node.ELEMENT_NODE) {
    			if (node.getPrefix() != null) {
    				usedPrefixes.add(node.getPrefix());
    			}

    			NamedNodeMap attributes = node.getAttributes();
    			int size1 = attributes.getLength();
    			for (int idx = 0; idx < size1; idx++) {
    				Node attr = attributes.item(idx);
    				if (attr.getPrefix() != null && !((attr.getPrefix()).equals(XMLConstants.XMLNS_ATTRIBUTE))) {
    					usedPrefixes.add(attr.getPrefix());
    				}
    			}
    		}

    		Node child = node.getFirstChild();
    		
    		while (child != null) {
    			getUsedXmlNsPrefixes(child, usedPrefixes);

    			child = child.getNextSibling();
    		}

    		node = node.getNextSibling();
    	}
    }

}
