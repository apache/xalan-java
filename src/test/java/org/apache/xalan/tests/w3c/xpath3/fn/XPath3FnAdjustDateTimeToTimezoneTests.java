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
package org.apache.xalan.tests.w3c.xpath3.fn;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.Date;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.apache.xalan.tests.w3c.xpath3.W3CXPath3TestsUtil;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.utils.PrefixResolver;
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
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSString;

/**
 * Xalan-J XSL 3 test driver, to run W3C XPath 3.1 test cases
 * for XPath 3.1 function fn:adjust-dateTime-to-timezone.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 * 
 */
public class XPath3FnAdjustDateTimeToTimezoneTests extends W3CXPath3TestsUtil { 

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {    	    	
    	m_xslTransformTestSetFilePath = W3C_XPATH3_TESTS_META_DATA_DIR_HOME + "fn/adjust-dateTime-to-timezone.xml";
        m_resultSubFolderName = "fn";
    	
    	m_testResultFileName = "adjust-dateTime-to-timezone_result.xml";
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    	m_xmlDocumentBuilderFactory = null;
        m_xmlDocumentBuilder = null;
        
        m_skipped_tests_list.clear();
    }

    @Test
    public void runXslFnAdjustDateTimeToTimezoneTests() {
    	
    	Document document = null;
    	Document xslTestCatalogDocument = null;
    	
    	try {
    		document = m_xmlDocumentBuilder.parse(m_xslTransformTestSetFilePath);
    		
    		xslTestCatalogDocument = m_xmlDocumentBuilder.parse(W3C_XPATH3_TESTS_CATALOG_FILE_PATH);
    	} 
    	catch (Exception ex) {
            // no op
    	}
    	
		Element elem1 = document.getDocumentElement();
		
		Element catalogDocElem1 = xslTestCatalogDocument.getDocumentElement();    	
    	String testSetName = elem1.getAttribute("name");
    	
    	Document testResultDoc = m_xmlDocumentBuilder.newDocument();
    	
		Element elemTestRun = testResultDoc.createElement("testrun");
		String testRunDateStrValue = getDateISOString(new Date());
		elemTestRun.setAttribute("name", testSetName);
		elemTestRun.setAttribute("dateTime", testRunDateStrValue);
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
					if ("test-case".equals(nodeName)) {						    					
						String testCaseNameStr = testCaseElem.getAttribute("name");												
						NodeList envNodeList = testCaseElem.getElementsByTagName("environment");
						
						XPathContext xctxt = new XPathContext(false);
						xctxt.setIncremental(false);
					    (xctxt.getDTMManager()).setIncremental(false);
						xctxt.setSource_location(false);
						(xctxt.getDTMManager()).setSource_location(false);
						
						PrefixResolver xmlNsPrefixResolver = getXMLNsPrefixResolver();
						xctxt.setNamespaceContext(xmlNsPrefixResolver);
						
						String envName = null;
						
						if (envNodeList.getLength() > 0) {
							Element elem = (Element)(envNodeList.item(0));
							envName = elem.getAttribute("ref");
							if ((envName != null) && !"".equals(envName) && !"empty".equals(envName)) {																	
								Node child = docElem1.getFirstChild();
								boolean isEnvNodeResolved = false;
								while (child != null) {
									String envName2 = null;
									Element elem2 = null;
									if (child.getNodeType() == Node.ELEMENT_NODE) {
										elem2 = (Element)child;
										String nodeName2 = elem2.getNodeName();
										if ("environment".equals(nodeName2)) {
											envName2 = elem2.getAttribute("name"); 
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
										Element elem3 = (Element)((elem2.getElementsByTagName("source")).item(0));
										String srcFileName = elem3.getAttribute("file");									 

										constructXalanDtmFromXMLFile(srcFileName, xctxt, false);
										
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
											if ("environment".equals(nodeName2)) {
												envName2 = elem2.getAttribute("name"); 
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
											Element elem3 = (Element)((elem2.getElementsByTagName("source")).item(0));
											String srcFileName = elem3.getAttribute("file");									 
	
											constructXalanDtmFromXMLFile(srcFileName, xctxt, true);
	
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
						
						NodeList depNodeList = testCaseElem.getElementsByTagName("dependency");
						int size1 = depNodeList.getLength();
						XObject xpathResultObj = null;												

						boolean unRecoverableException = false;
						boolean isXslTestXPathAndXQuery = false;
						if (size1 == 0) {
							isXslTestXPathAndXQuery = true;
							size1 = 1;
						}
						
						String xpathExprStr = null;
						
						for (int idx = 0; idx < size1; idx++) {
							Element elem3 = null;
							String depType = null;							
							if (!isXslTestXPathAndXQuery) {
							   elem3 = (Element)(depNodeList.item(idx));
							   depType = elem3.getAttribute("type");
							}							
														
							if (isXslTestXPathAndXQuery || ("spec".equals(depType) && ((elem3.getAttribute("value")).contains("XP31+") ||
									                                                   (elem3.getAttribute("value")).contains("XP30+") ||
									                                                   (elem3.getAttribute("value")).contains("XP20+")))) {
								elemTestResult = testResultDoc.createElement("testResult");																																		 
								elemTestResult.setAttribute("testName", testCaseNameStr);
								
								Element elemNode1 = (Element)((testCaseElem.getElementsByTagName("test")).item(0));    							
								xpathExprStr = elemNode1.getTextContent();

								xpathExprStr = getXPathNormalizedStr(xpathExprStr);

								try {
									int sourceNode = DTM.NULL;
									if ((envName != null) && !"empty".equals(envName)) {
										sourceNode = xctxt.getCurrentNode();
									}
                              	  
									XPath xpathObj = new XPath(xpathExprStr, null, xmlNsPrefixResolver, XPath.SELECT, null);
									xpathResultObj = xpathObj.execute(xctxt, sourceNode, xmlNsPrefixResolver);
								}
								catch (TransformerException ex) {
									String errMeg = ex.getMessage();
									String[] errMesgParts = errMeg.split(":");
									if (errMesgParts.length > 2) {
										runTimeErrCode = (errMesgParts[1]).trim();
									}
									else if (errMesgParts.length > 1) {
										runTimeErrCode = (errMesgParts[0]).trim();
									}
								}    							
								catch (Exception ex) {
									unRecoverableException = true;									
									elemTestResult.setAttribute("status", "fail");
									
									elemTestRun.appendChild(elemTestResult);
									
									node = node.getNextSibling();

									break;
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
								String expectedResultStr = resultElem1.getTextContent();

								XObject xpathExpectedObj = null;
								if ((xpathResultObj != null) && "assert".equals(nodeName2)) {
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
								else if (!("assert-true".equals(nodeName2) || "assert-false".equals(nodeName2) || 
										                                      "assert-type".equals(nodeName2) || 
										                                      "all-of".equals(nodeName2) || 
										                                      "any-of".equals(nodeName2)) && (expectedResultStr != null) 
										                                                                                    && !"".equals(expectedResultStr)) {
									if (expectedResultStr.startsWith("\"") && expectedResultStr.endsWith("\"")) {
										int size2 = expectedResultStr.length();
										expectedResultStr = expectedResultStr.substring(1, size2 - 1);
										expectedResultStr = "'" + expectedResultStr + "'"; 
									}
									else if (!expectedResultStr.startsWith("\'") && !expectedResultStr.endsWith("\'")) {
										expectedResultStr = "'" + expectedResultStr + "'";
									}

									XPath xpathObj = new XPath(expectedResultStr, null, xctxt.getNamespaceContext(), XPath.SELECT, null);
									xpathExpectedObj = xpathObj.execute(xctxt, DTM.NULL, xmlNsPrefixResolver);
								}

								if ("assert-deep-eq".equals(nodeName2)) {
									if (xpathResultObj != null) {
										FuncDeepEqual funcDeepEqual = new FuncDeepEqual();
										funcDeepEqual.setArg(xpathResultObj, 0);
										funcDeepEqual.setArg(xpathExpectedObj, 1);

										XObject xObj = funcDeepEqual.execute(xctxt);
										if (xObj.bool()) {
										   elemTestResult.setAttribute("status", "pass");
										}
										else {
										   elemTestResult.setAttribute("status", "fail");
										}
									}
									else {
										elemTestResult.setAttribute("status", "fail");
									}
								}
								else if ("assert-true".equals(nodeName2)) {
									if ((xpathResultObj != null) && xpathResultObj.bool()) {
									   elemTestResult.setAttribute("status", "pass");
									}
									else {
									   elemTestResult.setAttribute("status", "fail");
									}
								}
								else if ("assert-false".equals(nodeName2)) {
									if ((xpathResultObj != null) && !xpathResultObj.bool()) {
									   elemTestResult.setAttribute("status", "pass");
									}
									else {
									   elemTestResult.setAttribute("status", "fail");
									} 
								}
								else if ("assert-eq".equals(nodeName2)) {
									boolean isStatusFinal = false;
									if ((xpathResultObj instanceof XNumber) || (xpathResultObj instanceof XSNumericType)) {
										if (xpathExpectedObj instanceof XSString || xpathExpectedObj instanceof XString) {
											try {
												String strExpected1 = XslTransformEvaluationHelper.getStrVal(xpathExpectedObj);										   
												String strResult1 = XslTransformEvaluationHelper.getStrVal(xpathResultObj);
												double dbl1 = Double.valueOf(strExpected1);
												double dbl2 = Double.valueOf(strResult1);
												if (dbl1 == dbl2) {
													elemTestResult.setAttribute("status", "pass");
												}
												else {
													elemTestResult.setAttribute("status", "fail");
												}
											}
											catch (NumberFormatException ex) {
												elemTestResult.setAttribute("status", "fail");
											}

											isStatusFinal = true;
										}
									}

									if (!isStatusFinal) {
										if ((xpathResultObj != null) && xpathResultObj.vcEquals(xpathExpectedObj, null, null, true)) {
											elemTestResult.setAttribute("status", "pass");
										}
										else {
											elemTestResult.setAttribute("status", "fail");
										}
									}
								}
								else if ("assert-count".equals(nodeName2)) {
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
										   elemTestResult.setAttribute("status", "pass");
										}
										else {
										   elemTestResult.setAttribute("status", "fail");
										}
									}
									else {
										elemTestResult.setAttribute("status", "fail");
									}
								}
                                else if ("assert-string-value".equals(nodeName2)) {
                                	if (xpathResultObj != null) {                                	   
                                	   String expectedStr1 = XslTransformEvaluationHelper.getStrVal(xpathExpectedObj);
                                	   String resultStr1 = XslTransformEvaluationHelper.getStrVal(xpathResultObj);
                                	   if (expectedStr1.equals(resultStr1)) {
                                		  elemTestResult.setAttribute("status", "pass");
                                	   }
                                	   else {
                                		  elemTestResult.setAttribute("status", "fail");
                                	   }
                                	}
                                	else {
                                	   elemTestResult.setAttribute("status", "fail");
                                	}
								}                                
                                else if ("assert".equals(nodeName2)) {
                                	if ((xpathResultObj != null) && xpathExpectedObj.bool()) {
                                	   elemTestResult.setAttribute("status", "pass");
                                	}
                                	else {
                                	   elemTestResult.setAttribute("status", "fail");
                                	}
								}
                                else if ("assert-type".equals(nodeName2)) {
                                	int sourceNode = DTM.NULL;
                                	if ((envName != null) && !"empty".equals(envName)) {
                                		sourceNode = xctxt.getCurrentNode();
                                	}
                              	  
                                	XPath xpathObj = new XPath("(" + xpathExprStr + ") instance of " + expectedResultStr, null, xctxt.getNamespaceContext(), 
                                																												XPath.SELECT, null);
                                	XObject xObj = xpathObj.execute(xctxt, sourceNode, xmlNsPrefixResolver);
                                	if (xObj.bool()) {
                                	   elemTestResult.setAttribute("status", "pass");
                                	}
                                	else {
                                	   elemTestResult.setAttribute("status", "fail");
                                	}
								}
                                else if ("assert-empty".equals(nodeName2)) {
                                	if (xpathResultObj != null) { 
                                	   FuncEmpty funcEmpty = new FuncEmpty();
                                	   funcEmpty.setArg0(xpathResultObj);
                                	   
                                	   XObject xObj = funcEmpty.execute(xctxt);
                                	   if (xObj.bool()) {
                                		  elemTestResult.setAttribute("status", "pass"); 
                                	   }
                                	   else {
                                		  elemTestResult.setAttribute("status", "fail"); 
                                	   }
                                	}
                                	else {
                                	   elemTestResult.setAttribute("status", "fail");
                                	}
								}
                                else if ("all-of".equals(nodeName2)) {
                                	NodeList nodeList = resultElem1.getChildNodes();
                                	int size2 = nodeList.getLength();
                                	
                                	boolean isXslTestPass = true;
                                	
                                	for (int idx = 0; idx < size2; idx++) {
                                	   Node node2 = nodeList.item(idx);
                                	   if (node2.getNodeType() == Node.ELEMENT_NODE) {
                                		  Element elNode1 = (Element)node2;
                                		  String nodeName3 = elNode1.getNodeName();
                                		  String expectedResultStr2 = elNode1.getTextContent();                                 		  
                                		  if ((xpathResultObj != null) && "assert".equals(nodeName3)) {
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
                                		  else if ("assert-true".equals(nodeName3)) {          									  
          									  if ((xpathResultObj != null) && !xpathResultObj.bool()) {
          										 isXslTestPass = false;
          										 
          										 break;
          									  }
                                		  }
                                          else if ("assert-false".equals(nodeName3)) {          									  
          									  if ((xpathResultObj != null) && xpathResultObj.bool()) {
          										 isXslTestPass = false;
          										 
          										 break;
          									  }
                                		  }
                                          else if ("assert-type".equals(nodeName3)) {
                                        	  int sourceNode = DTM.NULL;
                                        	  if ((envName != null) && !"empty".equals(envName)) {
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
                                          else if ("assert-deep-eq".equals(nodeName3)) {
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
                                          else if ("assert-eq".equals(nodeName3)) {
                                        	  if (xpathResultObj != null) {
                                        		  XPath xpathObj = new XPath(expectedResultStr2, null, xctxt.getNamespaceContext(), XPath.SELECT, null);
                                        		  xpathExpectedObj = xpathObj.execute(xctxt, DTM.NULL, xmlNsPrefixResolver);

                                        		  if (!xpathResultObj.vcEquals(xpathExpectedObj, null, null, true)) {
                                        			  isXslTestPass = false;

                                        			  break;
                                        		  }
                                        	  }
                                		  }
                                          else if ("assert-count".equals(nodeName3)) {
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
                                          else if ("assert-string-value".equals(nodeName3)) {
                                        	  if (xpathResultObj != null) {
                                        		  XPath xpathObj = new XPath(expectedResultStr2, null, xctxt.getNamespaceContext(), XPath.SELECT, null);
                                        		  xpathExpectedObj = xpathObj.execute(xctxt, DTM.NULL, xmlNsPrefixResolver);
                                        		  
                                        		  String expectedStr1 = XslTransformEvaluationHelper.getStrVal(xpathExpectedObj);
                                        		  String resultStr1 = XslTransformEvaluationHelper.getStrVal(xpathResultObj);
                                        		  if (!expectedStr1.equals(resultStr1)) {
                                        			  isXslTestPass = false;

                                        			  break; 
                                        		  }
                                        	  } 
                                		  }
                                          else if ("assert-empty".equals(nodeName3)) {
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
                                          else if ("error".equals(nodeName3)) {
                                        	  expectedErrCode = resultElem1.getAttribute("code");
                                        	  if ((runTimeErrCode != null) && !runTimeErrCode.equals(expectedErrCode)) {
                                        		  isXslTestPass = false;

                                    			  break;  
          									  }
                                          }
                                          else if ("assert-permutation".equals(nodeName3)) {
                                        	  // Skipping these XSL test cases, for now 									
                                              m_skipped_tests_list.add(testCaseNameStr);
                                              
                                              elemTestResult.setAttribute("status", "skipped");
                                              
                                              isXslTestPass = false;

                                			  break;
                                          }                                		                                  		  
                                	   }
                                	   
                                	   node2 = node2.getNextSibling();
                                	}
                                	
                                	if (isXslTestPass) {
                                	   elemTestResult.setAttribute("status", "pass");
                                	}
                                	else {
                                	   elemTestResult.setAttribute("status", "fail");
                                	}
								}
                                else if ("any-of".equals(nodeName2)) {
                                	NodeList nodeList = resultElem1.getChildNodes();
                                	int size2 = nodeList.getLength();
                                	
                                	boolean isXslTestPass = false;
                                	
                                	for (int idx = 0; idx < size2; idx++) {
                                	   Node node2 = nodeList.item(idx);
                                	   if (node2.getNodeType() == Node.ELEMENT_NODE) {
                                		  Element elNode1 = (Element)node2;
                                		  String nodeName3 = elNode1.getNodeName();
                                		  String expectedResultStr2 = elNode1.getTextContent();                                 		  
                                		  if ((xpathResultObj != null) && "assert".equals(nodeName3)) {
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
                                		  else if ("assert-true".equals(nodeName3)) {          									  
          									  if ((xpathResultObj != null) && xpathResultObj.bool()) {
          										 isXslTestPass = true;
          										 
          										 break;
          									  }
                                		  }
                                          else if ("assert-false".equals(nodeName3)) {          									  
          									  if ((xpathResultObj != null) && !xpathResultObj.bool()) {
          										 isXslTestPass = true;
          										 
          										 break;
          									  }
                                		  }
                                          else if ("assert-type".equals(nodeName3)) {
                                        	  int sourceNode = DTM.NULL;
                                        	  if ((envName != null) && !"empty".equals(envName)) {
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
                                          else if ("assert-deep-eq".equals(nodeName3)) {
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
                                          else if ("assert-eq".equals(nodeName3)) {
                                        	  if (xpathResultObj != null) {
                                        		  XPath xpathObj = new XPath(expectedResultStr2, null, xctxt.getNamespaceContext(), XPath.SELECT, null);
                                        		  xpathExpectedObj = xpathObj.execute(xctxt, DTM.NULL, xmlNsPrefixResolver);

                                        		  if (xpathResultObj.vcEquals(xpathExpectedObj, null, null, true)) {
                                        			  isXslTestPass = true;

                                        			  break;
                                        		  }
                                        	  }
                                		  }
                                          else if ("assert-count".equals(nodeName3)) {
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
                                          else if ("assert-string-value".equals(nodeName3)) {
                                        	  if (xpathResultObj != null) {
                                        		  XPath xpathObj = new XPath(expectedResultStr2, null, xctxt.getNamespaceContext(), XPath.SELECT, null);
                                        		  xpathExpectedObj = xpathObj.execute(xctxt, DTM.NULL, xmlNsPrefixResolver);
                                        		  
                                        		  String expectedStr1 = XslTransformEvaluationHelper.getStrVal(xpathExpectedObj);
                                        		  String resultStr1 = XslTransformEvaluationHelper.getStrVal(xpathResultObj);
                                        		  if (expectedStr1.equals(resultStr1)) {
                                        			  isXslTestPass = true;

                                        			  break; 
                                        		  }
                                        	  } 
                                		  }
                                          else if ("assert-empty".equals(nodeName3)) {
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
                                          else if ("error".equals(nodeName3)) {
                                        	  expectedErrCode = resultElem1.getAttribute("code");
                                        	  if ((runTimeErrCode != null) && runTimeErrCode.equals(expectedErrCode)) {
                                        		  isXslTestPass = true;

                                    			  break;  
          									  }
                                          }
                                          else if ("assert-permutation".equals(nodeName3)) {
                                        	  // Skipping these XSL test cases, for now 									
                                              m_skipped_tests_list.add(testCaseNameStr);
                                              
                                              elemTestResult.setAttribute("status", "skipped");

                                			  break;
                                          }                                		                                  		  
                                	   }
                                	   
                                	   node2 = node2.getNextSibling();
                                	}
                                	
                                	if (isXslTestPass) {
                                	   elemTestResult.setAttribute("status", "pass");
                                	}
                                	else {
                                	   elemTestResult.setAttribute("status", "fail");
                                	}
								}
								else if ("assert-permutation".equals(nodeName2)) {
									// Skipping these XSL test cases, for now 									
                                    m_skipped_tests_list.add(testCaseNameStr);
                                    
                                    elemTestResult.setAttribute("status", "skipped");
								}
								else if ("error".equals(nodeName2)) {
									expectedErrCode = resultElem1.getAttribute("code");
									if ((runTimeErrCode != null) && runTimeErrCode.equals(expectedErrCode)) {
									   elemTestResult.setAttribute("status", "pass"); 
									}
									else {
									   elemTestResult.setAttribute("status", "fail");
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
					elemTestResult.setAttribute("status", "fail");
				}
			}
        }
		
		NodeList nodeList = testResultDoc.getElementsByTagName("testResult");

		int testsPassCount = 0;
		int testsfailCount = 0;
		int testsSkippedCount = 0;
		int testStatusUnknownCount = 0;

		int length1 = nodeList.getLength();
		for (int idx = 0; idx < length1; idx++) {
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
		elemTestRun.setAttribute("run", String.valueOf(totalTestsRun));

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

}
