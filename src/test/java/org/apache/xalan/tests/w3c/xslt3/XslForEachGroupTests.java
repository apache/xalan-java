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
import java.net.URI;
import java.nio.charset.StandardCharsets;

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.xalan.tests.util.XslTransformTestsUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XslForEachGroupTests extends XslTransformTestsUtil {     
    
	private static String m_testResultFileName = null;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    	m_xslTransformTestSetFilePath = "file:/d:/xslt30-test-master/tests/insn/for-each-group/_for-each-group-test-set.xml";
    	m_testResultFileName = "xsl_for_each_group_test_results.xml";
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        m_xmlDocumentBuilderFactory = null;
        m_xmlDocumentBuilder = null;
        m_xslTransformerFactory = null;
    }

    @Test
    public void xslForEachGroupTest() {    	
    	
    	Document xslTestSetDoc = null;
    	
    	FileOutputStream testResultFos = null;
    	
    	Document testResultDoc = null;
    	
    	try {
    	   xslTestSetDoc = m_xmlDocumentBuilder.parse(m_xslTransformTestSetFilePath);
    	   Element docElem = xslTestSetDoc.getDocumentElement();
    	   
    	   // Create XSL tests result XML DOM tree header, to which
		   // individual test results will be appended.
		   String testSetName = docElem.getAttribute("name");
		   testResultDoc = m_xmlDocumentBuilder.newDocument();
    	   Element elemTestRun = testResultDoc.createElement("testrun");
    	   elemTestRun.setAttribute("name", testSetName);
    	   testResultDoc.appendChild(elemTestRun);
    	   
    	   NodeList nodeList = xslTestSetDoc.getElementsByTagNameNS("http://www.w3.org/2012/10/xslt-test-catalog", "test-case");
    	   for (int idx = 0; idx < nodeList.getLength(); idx++) {
    		   Node node = nodeList.item(idx);
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
    		   
    		   if (xmlDocInpStr == null) {    			   
    			   /** 
    			    * An XML tag test-case/environment is not present.
    			    * 
    			    * An example of this kind of test case specification, is following:
    			    * 
    			    * <test>
    			    *   <stylesheet file="...xsl"/>
    			    *   <initial-template name="main"/>
    			    * </test>
    			    * 
    			    * Any XML document input shall work here.			  
    			    */
    			   xmlDocInpStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><temp/>"; 
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
    		   
    		   byte[] byteArr = xmlDocInpStr.getBytes(StandardCharsets.UTF_8);
    		   InputStream inpStream = new ByteArrayInputStream(byteArr);    		   
    		   
    		   DOMSource xmlInpDomSource = new DOMSource(m_xmlDocumentBuilder.parse(inpStream));    		   
    		   StreamSource xsltStreamSrc = new StreamSource(xslStylesheetUriStr);
    		       		   
    		   runW3CXSLTTestSuiteXslTransformAndProduceResult(testCaseName, xmlInpDomSource, xsltStreamSrc, expectedResultElem, 
    				                                                            elemTestRun, testResultDoc);
       		   
    	   }  	   
    	}
    	catch (Exception ex) {
    	   ex.printStackTrace();
    	}
    	finally {
    	   try {
    		   // Serialize testResultDoc to file
    		   String xslTestResultStr = serializeXmlDomElementNode(testResultDoc);
        	   
        	   File xslAnalyzeStringTestResultFile = new File(new URI(m_w3cXslt3TestSuiteXalanResultsPathPrefix + m_testResultFileName));
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

}
