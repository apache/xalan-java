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
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.xalan.tests.util.XslTestsErrorHandler;
import org.apache.xalan.tests.util.XslTransformTestsUtil;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.transformer.XalanProperties;
import org.apache.xpath.regex.Matcher;
import org.apache.xpath.regex.Pattern;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class implementation, contains common code for Xalan-J's 
 * implementation of W3C XSL 3.0 transformations test suite.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class W3CXslTransformTestsUtil extends XslTransformTestsUtil {	    
    
    protected static final String W3C_XSLT3_TESTS_META_DATA_DIR_HOME = "file:/d:/xslt30-test-master/tests/";
    
    private static final String XSL_TRANSFORM_TEST_ALL_OF_TEMPLATE_FILE_PATH =  W3C_XSLT3_TESTS_META_DATA_DIR_HOME + "variant_all_of_test_template_assert.xsl";
    
    protected static String m_xslTransformTestSetFilePath = null;
    
    protected static final String W3C_XSLT3_TESTS_RESULT_DIR_HOME = "file:/d:/eclipseWorkspaces/xalanj/xalan-j_xslt3.0_mvn/src/test/java/org/apache/xalan/tests/w3c/xslt3/result/";
    
    protected static String m_resultSubFolderName = null;
    
    protected static String m_testResultFileName = null;
    
    private static final String EXPECTED_NODE_KIND_ERROR = "error";
    
    private static final String EXPECTED_NODE_KIND_ASSERT_ALL_OF = "all-of";
    
    private static final String EXPECTED_NODE_KIND_ASSERT_XML = "assert-xml";
	
	/**
	 * Method definition to run all XSL transformation tests from 
	 * a test set.
	 */
    public void runXslTestSet() {    	
    	
    	Document xslTestSetDoc = null;
    	
    	FileOutputStream testResultFos = null;
    	
    	Document testResultDoc = null;    	
    	Element elemTestRun = null;
    	
    	try {
    	   xslTestSetDoc = m_xmlDocumentBuilder.parse(m_xslTransformTestSetFilePath);
    	   Element docElem = xslTestSetDoc.getDocumentElement();
    	   
    	   // Construct XSL tests result XML DOM tree header, to which
		   // individual test results will be appended.
		   String testSetName = docElem.getAttribute("name");
		   testResultDoc = m_xmlDocumentBuilder.newDocument();
    	   elemTestRun = testResultDoc.createElement("testrun");
    	   String testRunDateStrValue = getDateISOString(new Date());
    	   elemTestRun.setAttribute("name", testSetName);
    	   elemTestRun.setAttribute("dateTime", testRunDateStrValue);
    	   testResultDoc.appendChild(elemTestRun);    	   
    	   
    	   NodeList nodeList = xslTestSetDoc.getElementsByTagNameNS("http://www.w3.org/2012/10/xslt-test-catalog", "test-case");
    	   for (int idx = 0; idx < nodeList.getLength(); idx++) {
    		   Node node = nodeList.item(idx);
    		   
    		   if (isXslt2OnlyTestCase(node)) {
    			  // We skip running XSLT 2.0 only test cases
    			  Element elemTestResult = testResultDoc.createElement("testResult");
    			  elemTestResult.setAttribute("status", "skipped");
    			  elemTestResult.setAttribute("xsltVersion", "2.0 only");
    			  elemTestRun.appendChild(elemTestResult);
    			  
    			  continue; 
    		   }
    		   else if (isStreamingFeatureTestCase(node)) {
     			  // We skip running streaming feature test cases
       			  Element elemTestResult = testResultDoc.createElement("testResult");
       			  elemTestResult.setAttribute("status", "skipped");
       			  elemTestResult.setAttribute("feature", "streaming");
       			  elemTestRun.appendChild(elemTestResult);
       			  
       			  continue; 
     		   }
    		   
    		   String testCaseName = ((Element)node).getAttribute("name");    		   
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
    						   String envName = elemNode.getAttribute("name");
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
    			  String envFileName = envSrcElem.getAttribute("file");
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
    				   if ("test".equals(elemNode.getLocalName())) {    					   
    					   NodeList nodeList3 = elemNode.getElementsByTagName("stylesheet");
    					   Element elemNode2 = (Element)(nodeList3.item(0));    					   
    					   String fileName = elemNode2.getAttribute("file");
    					   URI uri = new URI(m_xslTransformTestSetFilePath);
   						   uri = uri.resolve(fileName);
   						   xslStylesheetUriStr = uri.toString();
   						   
   						   NodeList nodeList4 = elemNode.getElementsByTagName("initial-template");
   						   if (nodeList4.getLength() == 1) {
   							   Element elemNode3 = (Element)(nodeList4.item(0));
   							   m_initTemplateName = elemNode3.getAttribute("name");
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
    		   
    		   StreamSource xsltStreamSrc = new StreamSource(xslStylesheetUriStr);
    		   
    		   try {
    		      runW3CXSLTTestSuiteXslTransformAndProduceResult(testCaseName, xmlInpDomSource, xsltStreamSrc, 
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
        	   
        	   int totalTestsRun = (testsPassCount + testsfailCount + testsSkippedCount + testStatusUnknownCount);    
        	   
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
     * This method definition, serves to invoke W3C XSLT 3.0 test suite, on Xalan-J 
     * XSLT3 implementation.
     */
    private void runW3CXSLTTestSuiteXslTransformAndProduceResult(String testCaseName, DOMSource xmlInpDomSource, 
    		                                                     StreamSource xsltStreamSrc, Element expectedResultElem,  
            													 Element elemTestRun, Document testResultDoc) throws Exception {    	    	

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
    		
    		Transformer transformer = m_xslTransformerFactory.newTransformer(xsltStreamSrc);
    		
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
        		}
    		    else {
    		    	elemTestResult.setAttribute("status", "fail");
    				
    				Element resultOutElem = testResultDoc.createElement("outResult");
    				resultOutElem.setTextContent(resultStrWriter.toString());
    				elemTestResult.appendChild(resultOutElem);	
    		    }
    		    
    			return;
    		}
    		
    		Source xmlInpSrc = null;
    		if ((m_initTemplateName != null) && (xmlInpDomSource == null)) {
    			xmlInpSrc = xsltStreamSrc;
    			((TransformerImpl)transformer).setXMLSourceAbsent(true);
    		}
    		else {
    			xmlInpSrc = xmlInpDomSource; 
    		}
    		
    		transformer.transform(xmlInpSrc, new StreamResult(resultStrWriter));
    		
    		if (EXPECTED_NODE_KIND_ERROR.equals(expectedNodeKindName)) {
    			handleExpectedXslTransformationError(testResultDoc, elemTestResult, trfErrorList, 
    					                             trfFatalErrorList, expErrCodeName, resultStrWriter);
    		}
    		else if (EXPECTED_NODE_KIND_ASSERT_ALL_OF.equals(expectedNodeKindName)) {    			
    			NodeList nodeList1 = ((Element)nodeExpected).getElementsByTagName("serialization-matches");
    			int nodeListLength = nodeList1.getLength();    			
    			if (nodeListLength > 0) {
    			   List<XslSerializationMatchesMetaData> serMatchesMetaDataList = new ArrayList<XslSerializationMatchesMetaData>();
    			   for (int idx = 0; idx < nodeListLength; idx++) {
    				  Element elemNode = (Element)(nodeList1.item(idx));
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
    			   
    			   StringWriter strWriter = new StringWriter();
    			   
   				   Document documentToBeTransformed = m_xmlDocumentBuilder.parse(new ByteArrayInputStream((resultStrWriter.toString()).getBytes()));   				
   				   
   				   transformer.transform(new DOMSource(documentToBeTransformed), new StreamResult(strWriter));
   				   
   				   Document xmlResultDoc = m_xmlDocumentBuilder.parse(new ByteArrayInputStream((strWriter.toString()).getBytes()));
   				   
   				   List<Element> elemNodeList = new ArrayList<Element>();
   				   elemNodeList.add(xmlResultDoc.getDocumentElement());
   				   
   				   // Get list of all XML element nodes within a DOM object
   				   getXmlDomElemNodes(xmlResultDoc.getDocumentElement(), elemNodeList);
   				   
   				   boolean isTestCasePass = false;
   				   
   				   for (int idx = 0; idx < elemNodeList.size(); idx++) {
   					  Element elemNode = elemNodeList.get(idx);
   					  String elemNodeStrValue = null;   					  
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
   					  }
   					  
   					  List<Boolean> boolList = new ArrayList<Boolean>();
   					  for (int idx2 = 0; idx2 < serMatchesMetaDataList.size(); idx2++) {
   						  XslSerializationMatchesMetaData xslSerializationMatchesMetaData = serMatchesMetaDataList.get(idx2);
   						  String[] strArray = xslSerializationMatchesMetaData.getStrArr();
   						  String strValue = xslSerializationMatchesMetaData.getStrValue();
   						  if (strValue != null) {
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
    			else {
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

    				transformer = m_xslTransformerFactory.newTransformer(new DOMSource(verificationXslDoc));

    				StringWriter strWriter = new StringWriter();
    				
    				Document documentToBeTransformed = m_xmlDocumentBuilder.parse(new ByteArrayInputStream((resultStrWriter.toString()).getBytes()));    				
    				
    				transformer.transform(new DOMSource(documentToBeTransformed), new StreamResult(strWriter));

    				Document xmlInpDoc1 = m_xmlDocumentBuilder.parse(new ByteArrayInputStream((strWriter.toString()).getBytes()));
    				String str1 = serializeXmlDomElementNode(xmlInpDoc1);
    				String str2 = serializeXmlDomElementNode(expectedResultDoc);

    				if (isTwoXmlHtmlStrEqual(str1, str2)) {
    					elemTestResult.setAttribute("status", "pass");
    				}
    				else {
    					elemTestResult.setAttribute("status", "fail");
    				}
    		    }
    		}
            else if (EXPECTED_NODE_KIND_ASSERT_XML.equals(expectedNodeKindName)) {
            	Element elemNode = (Element)nodeExpected;
            	String fileName = elemNode.getAttribute("file");
            	String expectedResultStr = null;
            	if (!"".equals(fileName)) {
            		URI uri = new URI(m_xslTransformTestSetFilePath);
            		uri = uri.resolve(fileName);
            		expectedResultStr = getStringContentFromUrl(uri.toURL());
            	}
            	else {
            		expectedResultStr = elemNode.getTextContent();            		
            	}

            	Document xmlInpDoc1 = m_xmlDocumentBuilder.parse(new ByteArrayInputStream((resultStrWriter.toString()).getBytes()));
            	String str1 = serializeXmlDomElementNode(xmlInpDoc1);

            	Document xmlInpDoc2 = m_xmlDocumentBuilder.parse(new ByteArrayInputStream((expectedResultStr).getBytes()));

            	String str2 = serializeXmlDomElementNode(xmlInpDoc2);

            	if (isTwoXmlHtmlStrEqual(str1, str2)) {            		
            		elemTestResult.setAttribute("status", "pass");
            	}
            	else {
            		elemTestResult.setAttribute("status", "fail");
            	}
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
     * Method definition to get a list of all XML element nodes within 
     * the supplied XML element node, including the supplied node as well.
     * 
     * @param elemNode			   The supplied element node
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
    			    String fileName = elemNode.getAttribute("file");    			       			    
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
     * Method definition to get an ISO formatted date string for the supplied 
     * java.util.Date value.
     *  
     * @param date				The supplied date object value
     * @return					The formatted date string
     */
    private String getDateISOString(Date dateValue) {
    	String result = null;
    	
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        
        result = sdf.format(dateValue); 
        
        return result;
    }
    
    /**
     * Method definition to handle an expected XSL transformation error.
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
	 * An object of this class, contains information for XSL test case's expected 
	 * run-time result, when the test case specifies expected XSL transformation 
	 * result using an XML meta-data tag "serialization-matches".
	 */
	public class XslSerializationMatchesMetaData {
		
		// One of the variable m_strArr or m_strValue will be null 
		// while the other will be non-null.
		
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
