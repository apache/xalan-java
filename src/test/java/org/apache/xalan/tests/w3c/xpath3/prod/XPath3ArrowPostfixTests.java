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

import javax.xml.transform.TransformerException;

import org.apache.xalan.tests.w3c.xpath3.W3CXPath3TestsUtil;
import org.apache.xml.dtm.DTM;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.FuncDeepEqual;
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
 * for XPath 3.1 operator arrow postfix.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPath3ArrowPostfixTests extends W3CXPath3TestsUtil { 

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {    	    	
    	m_xslTransformTestSetFilePath = W3C_XPATH3_TESTS_META_DATA_DIR_HOME + "prod/ArrowPostfix.xml";
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    	m_xmlDocumentBuilderFactory = null;
        m_xmlDocumentBuilder = null;
    }

    @Test
    public void runXslArrowPostfixTests() {
    	
    	Document document = null;    	
    	try {
    		document = m_xmlDocumentBuilder.parse(m_xslTransformTestSetFilePath);
    	} 
    	catch (Exception ex) {
            // no op
    	}
    	
		Element elem1 = document.getDocumentElement();
		
        String testCaseNameStr = null;
    	
    	int textCaseCount = 0;
    	int testcasePass = 0;
		
		Node node = elem1.getFirstChild();		
		while (node != null) {			
			try {    		
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element testCaseElem = (Element)node;
					String nodeName = testCaseElem.getNodeName();
					String expectedErrCode = null;
					String runTimeErrCode = null;
					if ("test-case".equals(nodeName)) {
						textCaseCount++;    					
						testCaseNameStr = testCaseElem.getAttribute("name");     					    					
						NodeList nodeList = testCaseElem.getElementsByTagName("dependency");
						int size1 = nodeList.getLength();
						XObject xpathResultObj = null;

						XPathContext xctxt = new XPathContext();
						PrefixResolver xmlNsPrefixResolver = getXMLNsPrefixResolver();
						xctxt.setNamespaceContext(xmlNsPrefixResolver);

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
								Element elemNode1 = (Element)((testCaseElem.getElementsByTagName("test")).item(0));    							
								String xpathExprStr = elemNode1.getTextContent();

								xpathExprStr = replaceExpandedNsDecl(xpathExprStr);

								// Within XPath string, converting all instances of string 
								// values of form "..." to '...'.
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

								try {
									XPath xpathObj = new XPath(xpathExprStr, null, xctxt.getNamespaceContext(), XPath.SELECT, null);
									xpathResultObj = xpathObj.execute(xctxt, DTM.NULL, xctxt.getNamespaceContext());
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
								if ((expectedResultStr != null) && !"".equals(expectedResultStr)) {
									if (expectedResultStr.startsWith("\"") && expectedResultStr.endsWith("\"")) {
										int size2 = expectedResultStr.length();
										expectedResultStr = expectedResultStr.substring(1, size2 - 1);
										expectedResultStr = "'" + expectedResultStr + "'"; 
									}                            	                              	  

									XPath xpathObj = new XPath(expectedResultStr, null, xctxt.getNamespaceContext(), XPath.SELECT, null);
									xpathExpectedObj = xpathObj.execute(xctxt, DTM.NULL, xctxt.getNamespaceContext());
								}
								else if ("assert-true".equals(nodeName2)) {
									XPath xpathObj = new XPath("true()", null, xctxt.getNamespaceContext(), XPath.SELECT, null);
									xpathExpectedObj = xpathObj.execute(xctxt, DTM.NULL, xctxt.getNamespaceContext()); 
								}
								else if ("assert-false".equals(nodeName2)) {
									XPath xpathObj = new XPath("false()", null, xctxt.getNamespaceContext(), XPath.SELECT, null);
									xpathExpectedObj = xpathObj.execute(xctxt, DTM.NULL, xctxt.getNamespaceContext());
								}

								if ("assert-deep-eq".equals(nodeName2)) {
									FuncDeepEqual funcDeepEqual = new FuncDeepEqual();
									funcDeepEqual.setArg(xpathResultObj, 0);
									funcDeepEqual.setArg(xpathExpectedObj, 1);

									XObject xObj = funcDeepEqual.execute(xctxt);
									if (xObj.bool()) {
										testcasePass++;
									}
									else {

									}
								}
								else if ("assert-true".equals(nodeName2)) {
									if (xpathResultObj.bool()) {
										testcasePass++;
									}
									else {

									}
								}
								else if ("assert-false".equals(nodeName2)) {
									if (!xpathResultObj.bool()) {
										testcasePass++;
									}
									else {

									} 
								}
								else if ("assert-eq".equals(nodeName2)) {
									if (xpathResultObj.vcEquals(xpathExpectedObj, null, null, true)) {
										testcasePass++;
									}
									else {

									}
								}
								else if ("assert-permutation".equals(nodeName2)) {                                    
                                    // to do
								}
								else if ("error".equals(nodeName2)) {
									expectedErrCode = resultElem1.getAttribute("code");
									if ((runTimeErrCode != null) && runTimeErrCode.equals(expectedErrCode)) {
										testcasePass++;  
									}
								}
							}

							child = child.getNextSibling();
						}
					}    				
				}

				node = node.getNextSibling();
			}
			catch (Exception ex) {
				node = node.getNextSibling();
			}
        }
		
    }

}
