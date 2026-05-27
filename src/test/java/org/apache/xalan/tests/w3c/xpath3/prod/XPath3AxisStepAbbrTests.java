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
package org.apache.xalan.tests.w3c.xpath3.prod;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.Date;

import javax.xml.transform.TransformerException;

import org.apache.xalan.tests.w3c.xpath3.W3CXPath3TestsUtil;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Xalan-J XSL 3 test driver, to run W3C XPath 3.1 test cases
 * for XPath abbreviated axes.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPath3AxisStepAbbrTests extends W3CXPath3TestsUtil { 

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {    	    	
    	m_xslTransformTestSetFilePath = W3C_XPATH3_TESTS_META_DATA_DIR_HOME + "prod/AxisStep.abbr.xml";
    	
        m_resultSubFolderName = "prod";
    	
    	m_testResultFileName = "axis_step_abbr_result.xml";
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    	m_xmlDocumentBuilderFactory = null;
        m_xmlDocumentBuilder = null;
        
        m_skipped_tests_list.clear();
    }

    @Test
    public void runXslAbbrAxisTests() {
    	
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
		
		Node node = elem1.getFirstChild();		
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
						
						NodeList nodeList = testCaseElem.getElementsByTagName("dependency");
						int size1 = nodeList.getLength();
						XObject xpathResultObj = null;

						XPathContext xctxt = new XPathContext(false);
						xctxt.setIncremental(false);
						(xctxt.getDTMManager()).setIncremental(false);
						xctxt.setSource_location(false);
						(xctxt.getDTMManager()).setSource_location(false);
						
						PrefixResolver xmlNsPrefixResolver = getXMLNsPrefixResolver();
						xctxt.setNamespaceContext(xmlNsPrefixResolver);
						
						if (envNodeList.getLength() > 0) {
							Element elem = (Element)(envNodeList.item(0));
							String envName = elem.getAttribute("ref");
							if ((envName != null) && !"".equals(envName)) {
								Node child = catalogDocElem1.getFirstChild();
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

						boolean unRecoverableException = false;
						boolean isXslTestXPathAndXQuery = false;
						if (size1 == 0) {
							isXslTestXPathAndXQuery = true;
							size1 = 1;
						}
						
						for (int idx = 0; idx < size1; idx++) {
							Element elem3 = null;
							String depType = null;							
							if (!isXslTestXPathAndXQuery) {
							   elem3 = (Element)(nodeList.item(idx));
							   depType = elem3.getAttribute("type");
							}
							
							if (isXslTestXPathAndXQuery || ("spec".equals(depType) && ((elem3.getAttribute("value")).contains("XP31+") ||
									                                                   (elem3.getAttribute("value")).contains("XP30+") ||
													                                   (elem3.getAttribute("value")).contains("XP20+")))) {
								elemTestResult = testResultDoc.createElement("testResult");																																		 
								elemTestResult.setAttribute("testName", testCaseNameStr);
								
								Element elemNode1 = (Element)((testCaseElem.getElementsByTagName("test")).item(0));    							
								String xpathExprStr = elemNode1.getTextContent();

								xpathExprStr = getXPathNormalizedStr(xpathExprStr);

								try {
									final int sourceNode = xctxt.getCurrentNode();
									XPath xpathObj = new XPath(xpathExprStr, null, xctxt.getNamespaceContext(), XPath.SELECT, null);
									xpathResultObj = xpathObj.execute(xctxt, sourceNode, xctxt.getNamespaceContext());
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
									node = node.getNextSibling();

									break;
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
								if (!"error".equals(nodeName2) && (expectedResultStr != null) && !"".equals(expectedResultStr)) {
									if (expectedResultStr.startsWith("\"") && expectedResultStr.endsWith("\"")) {
										int size2 = expectedResultStr.length();
										expectedResultStr = expectedResultStr.substring(1, size2 - 1);
										expectedResultStr = "'" + expectedResultStr + "'"; 
									} 
									else {
										expectedResultStr = "'" + expectedResultStr + "'"; 
									}

									XPath xpathObj = new XPath(expectedResultStr, null, xctxt.getNamespaceContext(), XPath.SELECT, null);
									xpathExpectedObj = xpathObj.execute(xctxt, DTM.NULL, xctxt.getNamespaceContext());
								}								
								
								if ("assert-string-value".equals(nodeName2)) {																		
									if (xpathResultObj != null) {                                	   
										String expectedStr1 = XslTransformEvaluationHelper.getStrVal(xpathExpectedObj);
										String resultStr1 = XslTransformEvaluationHelper.getStrVal(xpathResultObj);
										
										String normalizeSpace = resultElem1.getAttribute("normalize-space");
										if ("true".equals(normalizeSpace)) {
											XPath xpathObj = new XPath("normalize-space('" + resultStr1 + "')", null, xctxt.getNamespaceContext(), XPath.SELECT, null);
											XObject xObj1 = xpathObj.execute(xctxt, DTM.NULL, xctxt.getNamespaceContext());
											resultStr1 = XslTransformEvaluationHelper.getStrVal(xObj1);
										}
										
										if (expectedStr1.equals(resultStr1)) {
											elemTestResult.setAttribute("status", "pass"); 
										}
										else {
											elemTestResult.setAttribute("status", "fail");
										}
									}
									else {

									}
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
