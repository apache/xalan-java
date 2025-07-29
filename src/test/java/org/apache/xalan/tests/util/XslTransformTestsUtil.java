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
package org.apache.xalan.tests.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.transformer.XalanProperties;
import org.apache.xpath.functions.XSL3FunctionService;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;

import junit.framework.Assert;

/**
 * A class providing, various common method definitions for Xalan-J's 
 * XSL 3 test suite.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * @author Vladimir Sitnikov <sitnikov.vladimir@gmail.com>
 * 
 * @xsl.usage advanced
 */
public class XslTransformTestsUtil extends FileComparisonUtil {
    
    /**
     * Class field representing an XSL TransformerFactory object instance 
     * needed by this test suite.
     */
    protected static TransformerFactory m_xslTransformerFactory = null;
    
    protected static String m_encoding = null;
    
    protected static String m_initTemplateName = null;
    
    /**
     * Class field representing file path prefix, that is used for test cases
     * related to XSL instructions like xsl:result-document that have 'href' 
     * attribute.
     */
    protected static String m_xslTransformInpPath = null;
    
    /**
     * Class field representing, whether XML Schema validation is enabled 
     * for an XSL transformation instance invoked by this test suite.
     */
    private boolean m_isXmlValidationEnabled = false;
    
    /**
     * Class field representing, whether an XSL xsl:evaluate instruction 
     * processing is enabled for an XSL transformation instance invoked by 
     * this test suite.
     */
    private boolean m_isXslEvaluateEnabled = false;
    
    /**
     * Class constructor.
     */
    public XslTransformTestsUtil() {        
        System.setProperty(XSLTestConstants.XSLT_TRANSFORMER_FACTORY_KEY, XSLTestConstants.XSLT_TRANSFORMER_FACTORY_VALUE);
        
        m_xslTransformerFactory = TransformerFactory.newInstance();
    }

    /**
     * This function is invoked by many of the Xalan-J XSL 3 test 
     * cases within this test suite.
     * 
     * This function does an XSLT transformation via JAXP XSL transformation API, and
     * compares the XSLT transformation's output with the corresponding expected output.
     */
    protected void runXslTransformAndAssertOutput(String xmlFilePath, String xslFilePath, 
                                                               String xslGoldFilePath, 
                                                               XslTestsErrorHandler xslTransformErrHandler) {
        try {
           
           
           String xslDocumentUriStr = ((new File(xslFilePath)).toURI()).toString();
           
           if (m_initTemplateName != null) {
        	  m_xslTransformerFactory.setAttribute(XalanProperties.INIT_TEMPLATE, m_initTemplateName); 
           }
           
           StreamSource xsltStreamSrc = new StreamSource(xslDocumentUriStr);
       
           Transformer transformer = m_xslTransformerFactory.newTransformer(xsltStreamSrc);
           
           setXslTransformProperties(transformer);
           
           if (xslTransformErrHandler != null) {
               transformer.setErrorListener(xslTransformErrHandler);  
           }
           
           StringWriter resultStrWriter = new StringWriter();
           
           DOMSource xmlDomSrc = null;
           
           if (xmlFilePath != null) {
              String xmlDocumentUriStr = ((new File(xmlFilePath)).toURI()).toString();
              InputSource inpSrc = new InputSource(xmlDocumentUriStr);
              if (m_encoding != null) {
            	 inpSrc.setEncoding(m_encoding); 
              }
              Node node = m_xmlDocumentBuilder.parse(inpSrc);
              xmlDomSrc = new DOMSource(node, xmlDocumentUriStr);
           }
           
           Source xmlInpSrc = null;
           if ((m_initTemplateName != null) && (xmlFilePath == null)) {
        	   xmlInpSrc = xsltStreamSrc;
        	   ((TransformerImpl)transformer).setXMLSourceAbsent(true);
           }
           else {
        	   xmlInpSrc = xmlDomSrc; 
           }
           
           transformer.transform(xmlInpSrc, new StreamResult(resultStrWriter));
           
           if (xslTransformErrHandler != null) {
               List<String> trfErrorList = xslTransformErrHandler.getTrfErrorList();
               List<String> trfFatalErrorList = xslTransformErrHandler.getTrfFatalErrorList();
               if (trfErrorList.size() > 0 || trfFatalErrorList.size() > 0) {
                   // The test has passed
                   return;
               }
               else {
                   // The test has failed
                   Assert.fail();  
               }
           }
           else {
              byte[] goldFileBytes = Files.readAllBytes(Paths.get(xslGoldFilePath));
              
              if ((XSLTestConstants.XML).equals(m_fileComparisonType)) {            	              	  
            	  if (!isXMLFileContentsEqual(new String(goldFileBytes), resultStrWriter.toString())) {
            		  Assert.fail(); 
            	  }
              }
              else if ((XSLTestConstants.JSON).equals(m_fileComparisonType)) {
  				  if (!isJsonFileContentsEqual(new String(goldFileBytes), resultStrWriter.toString())) {
  				     Assert.fail();
  				  } 
              }
              else if ((XSLTestConstants.TEXT).equals(m_fileComparisonType) || (XSLTestConstants.HTML).equals(m_fileComparisonType)) {
            	  Assert.assertEquals(new String(goldFileBytes), resultStrWriter.toString());
              }              
           }
        }
        catch (Exception ex) {
            Assert.fail();    
        }
        finally {
        	m_fileComparisonType = XSLTestConstants.XML;         	
        	if (m_initTemplateName != null) {
        		m_xslTransformerFactory.setAttribute(XalanProperties.INIT_TEMPLATE, null);
        		m_initTemplateName = null;
        	}
        }
     }
    
    /**
     * This function is invoked by many of the Xalan-J XSL 3 test 
     * cases within this test suite.
     * 
     * This function does an XSLT transformation via JAXP XSL transformation API, and
     * compares the XSLT transformation's output with the corresponding expected output.
     */
     protected void runXslTransformAndAssertOutput(String xmlFilePath, String xslFilePath, 
    		                                           String[] goldFileStrArr, String[] goldFileNameArr, 
    		                                           XslTestsErrorHandler xslTransformErrHandler) {    	     	 
 		
    	 String fileProducedName1 = null;
    	 String fileProducedName2 = null;
    	 
    	 try {
    		String xmlDocumentUriStr = ((new File(xmlFilePath)).toURI()).toString();
    		String xslDocumentUriStr = ((new File(xslFilePath)).toURI()).toString();

    		Node xmlDomSource = m_xmlDocumentBuilder.parse(new InputSource(xmlDocumentUriStr));

    		Transformer transformer = m_xslTransformerFactory.newTransformer(new StreamSource(xslDocumentUriStr));

    		setXslTransformProperties(transformer);

    		if (xslTransformErrHandler != null) {
    			transformer.setErrorListener(xslTransformErrHandler);  
    		}

    		StringWriter resultStrWriter = new StringWriter();

    		DOMSource xmlDomSrc = new DOMSource(xmlDomSource, xmlDocumentUriStr);

    		transformer.transform(xmlDomSrc, new StreamResult(resultStrWriter));
    		
    		if (goldFileNameArr.length == 2) {
    			fileProducedName1 = (m_xslTransformInpPath != null) ? (m_xslTransformInpPath + 
    					                                                          goldFileNameArr[0]) : goldFileNameArr[0];
    			fileProducedName2 = (m_xslTransformInpPath != null) ? (m_xslTransformInpPath + 
    					                                                          goldFileNameArr[1]) : goldFileNameArr[1];

    			String fileProducedStr1 = getFileContentAsString(fileProducedName1);
    			String fileProducedStr2 = getFileContentAsString(fileProducedName2);
    			
    			if ((XSLTestConstants.XML).equals(m_fileComparisonType)) {    				
    				int idx1 = fileProducedStr1.lastIndexOf('>');
    				fileProducedStr1 = fileProducedStr1.substring(0, idx1 + 1);
    				int idx2 = fileProducedStr2.lastIndexOf('>');
    				fileProducedStr2 = fileProducedStr2.substring(0, idx2 + 1);
    				boolean fileComparisonResult1 = isXMLFileContentsEqual(goldFileStrArr[0], fileProducedStr1);
    				boolean fileComparisonResult2 = isXMLFileContentsEqual(goldFileStrArr[1], fileProducedStr2);
    				if (!(fileComparisonResult1 && fileComparisonResult2)) {
    				   Assert.fail();
    				}
    			}
    			else if ((XSLTestConstants.JSON).equals(m_fileComparisonType)) {
    				boolean fileComparisonResult1 = isJsonFileContentsEqual(goldFileStrArr[0], fileProducedStr1);
    				boolean fileComparisonResult2 = isJsonFileContentsEqual(goldFileStrArr[1], fileProducedStr2);
    				if (!(fileComparisonResult1 && fileComparisonResult2)) {
    				   Assert.fail();
    				}
    			}
    			else if ((XSLTestConstants.TEXT).equals(m_fileComparisonType) || (XSLTestConstants.HTML).equals(m_fileComparisonType)) {
    				boolean fileComparisonResult1 = (goldFileStrArr[0]).equals(fileProducedStr1); 
    				boolean fileComparisonResult2 = (goldFileStrArr[1]).equals(fileProducedStr2);
    				if (!(fileComparisonResult1 && fileComparisonResult2)) {
     				   Assert.fail();
     				}
                }
    		}
    		else {
    			// Only one file is there to compare it's contents
    			
    			fileProducedName1 = (m_xslTransformInpPath != null) ? (m_xslTransformInpPath + 
                                                                                  goldFileNameArr[0]) : goldFileNameArr[0];

    			String fileProducedStr1 = getFileContentAsString(fileProducedName1);

    			if ((XSLTestConstants.XML).equals(m_fileComparisonType)) { 
    				if (!isXMLFileContentsEqual(goldFileStrArr[0], fileProducedStr1)) {
     				   Assert.fail();	
     				}
    			}
    			else if ((XSLTestConstants.JSON).equals(m_fileComparisonType)) {
    				if (!isJsonFileContentsEqual(goldFileStrArr[0], fileProducedStr1)) {
    				   Assert.fail();	
    				}
    			}
    			else if ((XSLTestConstants.TEXT).equals(m_fileComparisonType) || (XSLTestConstants.HTML).equals(m_fileComparisonType)) {
    				if (!(goldFileStrArr[0].equals(fileProducedStr1))) {
    				   Assert.fail();
    				}
    			}
    		}
    	}
    	catch (Exception ex) {
    		Assert.fail();    
    	}
    	finally {    		    		
    		// Delete the temporary files produced while running 
    		// a test case using this method.
    		(new File(fileProducedName1)).delete();
    		if (fileProducedName2 != null) {
    		   (new File(fileProducedName2)).delete();
    		}
    	}
    }
    
     /**
      * This function is invoked by many of the Xalan-J XSL 3 test 
      * cases within this test suite.
      * 
      * This function does an XSLT transformation via JAXP XSL transformation API, and
      * compares the XSLT transformation's output with the corresponding expected output.
      */
     protected void runXslTransformAndAssertOutputWithXslParamBaseUri(String xmlFilePath, String xslFilePath, 
			    		                                              String xslGoldFilePath, XslTestsErrorHandler 
			    		                                              xslTransformErrHandler, String localBaseUriPrefix) {
    	try {
    		String xmlDocumentUriStr = ((new File(xmlFilePath)).toURI()).toString();
    		String xslDocumentUriStr = ((new File(xslFilePath)).toURI()).toString();

    		Node xmlDomSource = m_xmlDocumentBuilder.parse(new InputSource(xmlDocumentUriStr));

    		Transformer transformer = m_xslTransformerFactory.newTransformer(new StreamSource(xslDocumentUriStr));

    		setXslTransformProperties(transformer);

    		if (xslTransformErrHandler != null) {
    			transformer.setErrorListener(xslTransformErrHandler);  
    		}

    		StringWriter resultStrWriter = new StringWriter();

    		DOMSource xmlDomSrc = new DOMSource(xmlDomSource, xmlDocumentUriStr);

    		transformer.setParameter("localBaseUriPrefix", localBaseUriPrefix);    		
    		transformer.transform(xmlDomSrc, new StreamResult(resultStrWriter));

    		if (xslTransformErrHandler != null) {
    			List<String> trfErrorList = xslTransformErrHandler.getTrfErrorList();
    			List<String> trfFatalErrorList = xslTransformErrHandler.getTrfFatalErrorList();
    			if (trfErrorList.size() > 0 || trfFatalErrorList.size() > 0) {
    				// The test has passed
    				return;
    			}
    			else {
    				// The test has failed
    				Assert.fail();  
    			}
    		}
    		else {
    			byte[] goldFileBytes = Files.readAllBytes(Paths.get(xslGoldFilePath));
    			
    			if ((XSLTestConstants.XML).equals(m_fileComparisonType)) {
    				if (!isXMLFileContentsEqual(new String(goldFileBytes), resultStrWriter.toString())) {
    					Assert.fail();	
    				}	
    			}
    			else if ((XSLTestConstants.JSON).equals(m_fileComparisonType)) {
    				if (!isJsonFileContentsEqual(new String(goldFileBytes), resultStrWriter.toString())) {
    					Assert.fail();	
    				}
    			}
    			else if ((XSLTestConstants.TEXT).equals(m_fileComparisonType) || (XSLTestConstants.HTML).equals(m_fileComparisonType)) {
    			    Assert.assertEquals(new String(goldFileBytes), resultStrWriter.toString());
                }    			
    		}
    	}
    	catch (Exception ex) {
    		Assert.fail();    
    	}
    }
    
     /**
      * This function is invoked by many of the Xalan-J XSL 3 test 
      * cases within this test suite.
      * 
      * This function does an XSLT transformation via JAXP XSL transformation API, and
      * compares the XSLT transformation's output with the corresponding expected output.
      */
    protected void runXslUriTransformAndAssertOutput(String xmlDocumentUri, String xslFilePath, 
                                                               String xslGoldFilePath, 
                                                               XslTestsErrorHandler xslTransformErrHandler) {
        try {
           String xmlDocumentUriStr = xmlDocumentUri;
           String xslDocumentUriStr = ((new File(xslFilePath)).toURI()).toString();
           
           Node xmlDomSource = m_xmlDocumentBuilder.parse(xmlDocumentUriStr);
       
           Transformer xslTransformer = m_xslTransformerFactory.newTransformer(new 
        		                                                          StreamSource(xslDocumentUriStr));
           if (xslTransformErrHandler != null) {
               xslTransformer.setErrorListener(xslTransformErrHandler);  
           }
           
           StringWriter resultStrWriter = new StringWriter();
           
           xslTransformer.transform(new DOMSource(xmlDomSource), new StreamResult(resultStrWriter));
           
           if (xslTransformErrHandler != null) {
               List<String> trfErrorList = xslTransformErrHandler.getTrfErrorList();
               List<String> trfFatalErrorList = xslTransformErrHandler.getTrfFatalErrorList();
               if (trfErrorList.size() > 0 || trfFatalErrorList.size() > 0) {
                   // The test has passed
                   return;
               }
               else {
                   // The test has failed
                   Assert.fail();  
               }
           }
           else {
        	   byte[] goldFileBytes = Files.readAllBytes(Paths.get(xslGoldFilePath));

        	   if ((XSLTestConstants.XML).equals(m_fileComparisonType)) {
        		   if (!isXMLFileContentsEqual(new String(goldFileBytes), resultStrWriter.toString())) {
        			   Assert.fail();	
        		   }	
        	   }
        	   else if ((XSLTestConstants.JSON).equals(m_fileComparisonType)) {
        		   if (!isJsonFileContentsEqual(new String(goldFileBytes), resultStrWriter.toString())) {
        			   Assert.fail();	
        		   }
        	   }
        	   else if ((XSLTestConstants.TEXT).equals(m_fileComparisonType) || (XSLTestConstants.HTML).equals(m_fileComparisonType)) {
        		   Assert.assertEquals(new String(goldFileBytes), resultStrWriter.toString());
        	   } 
           }
        }
        catch (Exception ex) {
            Assert.fail();    
        }
    }
    
    /**
     * Set an XSL transformation, XML Schema validation property.
     */
    protected void setXmlValidationProperty(boolean isEnableValidation) {
    	m_isXmlValidationEnabled = isEnableValidation;
    }
    
    /**
     * Set an XSL transformation, xsl:evaluate instruction evaluation property.
     */
    protected void setXslEvaluateProperty(boolean isXslEvaluateEnable) {
    	m_isXslEvaluateEnabled = isXslEvaluateEnable;
    }
    
    /**
     * Get the file contents as String, given the file system 
     * path of the file as an argument to this method.
     */
    protected String getFileContentAsString(String filePathStr) {
    	
    	String fileContentStr = null;
    	
    	try {    		
    		byte[] fileByteArr = Files.readAllBytes(Paths.get(filePathStr));
    		fileContentStr = new String(fileByteArr, "UTF-8");
    	}
    	catch (Exception ex) {
    		ex.printStackTrace();
    	}
    	
    	return fileContentStr;
    }
    
    /**
     * Get the string contents from an URL.
     */
    protected String getStringContentFromUrl(URL url) throws IOException {
        StringBuilder strBuilder = new StringBuilder();
        
        InputStream inpStream = url.openStream();        
        try {                    
            BufferedReader buffReader = new BufferedReader(new InputStreamReader(inpStream));
            int c;
            while ((c = buffReader.read()) != -1) {
               strBuilder.append((char)c);
            }
        } 
        finally {
            inpStream.close();
        }
     
        return strBuilder.toString();
    }
    
    /*
     * Serialize an XML DOM element node, to XML string value.
     */
    protected String serializeXmlDomElementNode(Node node) throws Exception {
    	String resultStr = null;

    	DOMImplementationLS domImplLS = (DOMImplementationLS)((DOMImplementationRegistry.
    																				 newInstance()).getDOMImplementation("LS"));
    	LSSerializer lsSerializer = domImplLS.createLSSerializer();
    	DOMConfiguration domConfig = lsSerializer.getDomConfig();
    	domConfig.setParameter(XSL3FunctionService.XML_DOM_FORMAT_PRETTY_PRINT, Boolean.TRUE);
    	resultStr = lsSerializer.writeToString(node);
    	resultStr = resultStr.replaceFirst(XSL3FunctionService.UTF_16, XSL3FunctionService.UTF_8);

    	return resultStr;
    }
    
    /**
     * When running W3C XSLT 3.0 test suite, check whether the test case 
     * is specified for XSLT 2.0 version only.
     *  
     * @param testCaseNode
     * @return						Boolean value true or false						
     */
    protected boolean isXslt2OnlyTestCase(Node testCaseNode) {
    	
        boolean result = false;
        
        NodeList nodeList = testCaseNode.getChildNodes();
        for (int idx = 0; idx < nodeList.getLength(); idx++) {
     	  Node node = nodeList.item(idx);
     	  if (node.getNodeType() == Node.ELEMENT_NODE) {
     		 Element elemNode = (Element)node;
     		 String elemName = elemNode.getLocalName();
     		 if ("dependencies".equals(elemName)) {
     			 NodeList nodeList1 = elemNode.getElementsByTagName("spec");
     			 if (nodeList1.getLength() == 1) {
     				 Element elem1 = (Element)(nodeList1.item(0));
     				 String xsltSpecVersion = elem1.getAttribute("value");
     				 if ("XSLT20".equals(xsltSpecVersion)) {
     					result = true;
     					
     					break; 
     				 }
     			 }
     		 }
     	  }
        }
        
        return result;
    }
    
    /**
     * When running W3C XSLT 3.0 test suite, check whether the test case 
     * is written for streaming feature.
     *  
     * @param testCaseNode
     * @return						Boolean value true or false						
     */
    protected boolean isStreamingFeatureTestCase(Node testCaseNode) {
    	
        boolean result = false;
        
        NodeList nodeList = testCaseNode.getChildNodes();
        for (int idx = 0; idx < nodeList.getLength(); idx++) {
     	  Node node = nodeList.item(idx);
     	  if (node.getNodeType() == Node.ELEMENT_NODE) {
     		 Element elemNode = (Element)node;
     		 String elemName = elemNode.getLocalName();
     		 if ("dependencies".equals(elemName)) {
     			 NodeList nodeList1 = elemNode.getElementsByTagName("feature");
     			 if (nodeList1.getLength() == 1) {
     				 Element elem1 = (Element)(nodeList1.item(0));
     				 String streaming = elem1.getAttribute("value");
     				 if ("streaming".equals(streaming)) {
     					result = true;
     					break; 
     				 }
     			 }
     		 }
     	  }
        }
        
        return result;
    }
    
    /**
     * Set XML Schema validation and xsl:evaluate XSL transformation properties, on 
     * Xalan-J's TransformerImpl object. 
     */
    protected void setXslTransformProperties(Transformer transformer) throws TransformerException {
    	if (m_isXmlValidationEnabled) {
    		TransformerImpl transformerImpl = (TransformerImpl)transformer;
    		transformerImpl.setProperty(TransformerImpl.XML_VALIDATION_PROPERTY, Boolean.TRUE);
    	}

    	if (m_isXslEvaluateEnabled) {
    		TransformerImpl transformerImpl = (TransformerImpl)transformer;
    		transformerImpl.setProperty(TransformerImpl.XSL_EVALUATE_PROPERTY, Boolean.TRUE); 
    	}
    }
    
    /**
     * Method definition to handle test case failure exception.
     */
	protected void handleTestCaseFailException(Document testResultDoc, List<String> trfErrorList,
											   List<String> trfFatalErrorList, Element elemTestResult, 
											   String expErrCodeName) {		
		
		Element resultOutElem = testResultDoc.createElement("outResult");
		for (int idx = 0; idx < trfErrorList.size(); idx++) {
			String errTraceInfo = trfErrorList.get(idx);
			if ((expErrCodeName != null) && errTraceInfo.contains(expErrCodeName)) {
			   elemTestResult.setAttribute("status", "pass");
			   
			   return;
			}
			Element errTraceElem = testResultDoc.createElement("errTrace");
			errTraceElem.setTextContent(errTraceInfo);
			resultOutElem.appendChild(errTraceElem);
		}
		
		for (int idx = 0; idx < trfFatalErrorList.size(); idx++) {
			String errTraceInfo = trfFatalErrorList.get(idx);
			if ((expErrCodeName != null) && errTraceInfo.contains(expErrCodeName)) {
				elemTestResult.setAttribute("status", "pass");

				return;
		    }
			Element errTraceElem = testResultDoc.createElement("errTrace");
			errTraceElem.setTextContent(errTraceInfo);
			resultOutElem.appendChild(errTraceElem);
		}
		
		elemTestResult.setAttribute("status", "fail");
		
		elemTestResult.appendChild(resultOutElem);
	}
	
	/**
     * Method definition, to check whether two XML or HTML
     * string values represent equal document content.
     */
    protected boolean isTwoXmlHtmlStrEqual(String str1, String str2) {
        
    	boolean result = true;
        
        String[] strArr1 = str1.split("\r?\n");
        String[] strArr2 = str2.split("\r?\n");
        if (strArr1.length != strArr2.length) {
           result = false;	
        }
        else {
           for (int idx = 0; idx < strArr1.length; idx++) {
        	   for (int idx2 = 0; idx2 < strArr2.length; idx2++) {
        		  if (idx == idx2) {
        			  String strTrim1 = (strArr1[idx]).trim();
        			  String strTrim2 = (strArr2[idx2]).trim();
        			  if (!strTrim1.equals(strTrim2)) {
        				  result = false;
        				  break;
        			  }
        		  }
        	   }
        	   
        	   if (!result) {
        		  break; 
        	   }
           }
        }
        
        return result;
    }
    
    /**
     * This method is used by, Xalan-J XSL 3 java extension function calls
     * used within few .xsl test files.
     * 
     * @return  current date value
     */
    public static Date getCurrentDate() {
        Date currentDate = new Date();
      
        return currentDate;
    }
   
    /**
     * This method is used by, Xalan-J XSL 3 java extension function calls
     * used within few .xsl test files.
     * 
     * @return  default time zone offset string
     */
    public static String getDefaultTimezoneOffsetStr() {
        String timeZoneoffsetStr = null;
       
        String dateStr = (OffsetDateTime.now()).toString();
        if (dateStr.endsWith("Z")) {
            timeZoneoffsetStr = "Z";   
        }
        else {
            int dateStrLength = dateStr.length();
            timeZoneoffsetStr = dateStr.substring(dateStrLength - 6, dateStrLength); 
        }
       
        return timeZoneoffsetStr;
    }
    
}
