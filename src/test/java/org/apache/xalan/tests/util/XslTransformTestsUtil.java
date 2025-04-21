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
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.xalan.transformer.TransformerImpl;
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
    protected static TransformerFactory xslTransformerFactory = null;
    
    /**
     * Class field representing file path prefix, that is used for test cases
     * related to XSL instructions like xsl:result-document that have 'href' 
     * attribute.
     */
    protected static String xslTransformInpPath = null;
    
    /**
     * Class field representing local file system folder, where W3C XSLT 3.0 
     * Xalan-J test results shall be saved.
     */
    protected static String w3cXslt3TestSuiteXalanResultFolderUri = "file:/d:/xslt30-test-master/tests/";
    
    protected static String xslTransformTestSetFilePath = null;
    
    /**
     * Class field representing, whether XML Schema validation is enabled 
     * for an XSL transformation instance invoked by this test suite.
     */
    private boolean isXmlValidationEnabled = false;
    
    /**
     * Class field representing, whether an XSL xsl:evaluate instruction 
     * processing is enabled for an XSL transformation instance invoked by 
     * this test suite.
     */
    private boolean isXslEvaluateEnabled = false;
    
    private static final String XSL_TRANSFORM_TEST_ALL_OF_TEMPLATE_FILE_PATH = "file:/d:/xslt30-test-master/tests/test_all_of_template.xsl";
    
    protected static String w3cXslt3TestSuiteXalanResultsPathPrefix = "file:/d:/xslt30-test-master/xalan_j_test_results/";
    
    /**
     * Class constructor.
     */
    public XslTransformTestsUtil() {        
        System.setProperty(XSLTestConstants.XSLT_TRANSFORMER_FACTORY_KEY, XSLTestConstants.XSLT_TRANSFORMER_FACTORY_VALUE);
        
        xslTransformerFactory = TransformerFactory.newInstance();
    }
    
    /**
     * This method definition, serves to invoke W3C XSLT 3.0 test suite, on Xalan-J 
     * XSLT3 implementation.
     */
    protected void runW3CXSLTTestSuiteXslTransformAndProduceResult(String testCaseName, DOMSource xmlInpDomSource, 
    		                                                       StreamSource xsltStreamSrc, Element expectedResultElem,  
            													   Element elemTestRun, Document testResultDoc, 
            													   XslTestsErrorHandler xslTransformErrHandler) throws Exception {
    	
    	List<String> trfErrorList = null;
    	List<String> trfFatalErrorList = null;

    	try {
    		Transformer transformer = xslTransformerFactory.newTransformer(xsltStreamSrc);

    		setXslTransformProperties(transformer);

    		if (xslTransformErrHandler != null) {
    			transformer.setErrorListener(xslTransformErrHandler);  
    		}

    		StringWriter resultStrWriter = new StringWriter();

    		transformer.transform(xmlInpDomSource, new StreamResult(resultStrWriter));

    		trfErrorList = xslTransformErrHandler.getTrfErrorList();
    		trfFatalErrorList = xslTransformErrHandler.getTrfFatalErrorList();
    		
    		Element elemTestResult = testResultDoc.createElement("testResult");
    		elemTestResult.setAttribute("testName", testCaseName);
    		
    		Node nodeExpected = (expectedResultElem.getFirstChild()).getNextSibling();
    		String expectedNodeKindName = nodeExpected.getNodeName();
    		if ("error".equals(expectedNodeKindName)) {
    			String expErrCodeName = ((Element)nodeExpected).getAttribute("code");
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
    					// An XSL transformation resulted with dynamic error.
    					// But Xalan-J produced an error trace that doesn't match
    					// with XSL 3 test suite's error definition for this 
    					// test case.
    					elemTestResult.setAttribute("status", "fail");
    					
    					Element resultOutElem = testResultDoc.createElement("outResult");
    					for (int idx = 0; idx < trfErrorList.size(); idx++) {
        					String errTraceInfo = trfErrorList.get(idx);
        					Element errTraceElem = testResultDoc.createElement("errTrace");
        					errTraceElem.setTextContent(errTraceInfo);
        					resultOutElem.appendChild(errTraceElem);
    					}
    					
    					for (int idx = 0; idx < trfFatalErrorList.size(); idx++) {
        					String errTraceInfo = trfFatalErrorList.get(idx);
        					Element errTraceElem = testResultDoc.createElement("errTrace");
        					errTraceElem.setTextContent(errTraceInfo);
        					resultOutElem.appendChild(errTraceElem);
    					}
    					
    					elemTestResult.appendChild(resultOutElem);
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
    		/*else if ("all-of".equals(expectedNodeKindName)) {    			
    			byte[] fileBytes = Files.readAllBytes(Paths.get(new URI(XSL_TRANSFORM_TEST_ALL_OF_TEMPLATE_FILE_PATH)));
    			String xslTemplateStr = new String(fileBytes);
    			NodeList nodeList = nodeExpected.getChildNodes();
    			StringBuffer replacementStrBuff = new StringBuffer();
    			StringBuffer replacementStrBuffOk = new StringBuffer();
    			replacementStrBuffOk.append("<result>");
    			for (int idx = 0; idx < nodeList.getLength(); idx++) {
    				Node node = nodeList.item(idx);
    				if (node.getNodeType() == Node.ELEMENT_NODE) {
    					String assertStr = ((Element)node).getTextContent();
    					String str = "<xpath inp='" + assertStr + "'>";
    					str += "<xsl:value-of select='" + assertStr + "'></xsl:value-of>";
    					str += "</xpath>\n";
    					replacementStrBuff.append(str);
    					
    					replacementStrBuffOk.append("<xpath inp='" + assertStr + "'>true</xpath>");
    				}
    			}    			
    			
    			String xslTemplateReplacedStr = xslTemplateStr.replace("{{XPATH_ASSERT_LIST}}", replacementStrBuff.toString());
    			
    			replacementStrBuffOk.append("</result>");
    			
    			// Do an XSL transformation of XSLT stylesheet document xslTemplateReplacedStr with 
    			// an XML document input resultStrWriter.toString(). The result of this XSL 
    			// transformation should be equal to string value replacementStrBuffOk.toString() for 
    			// this test case to pass.
    			
    			Document xmlInpDoc = xmlDocumentBuilder.parse(new ByteArrayInputStream((resultStrWriter.toString()).getBytes()));
    			
    			transformer = xslTransformerFactory.newTransformer(new StreamSource(new ByteArrayInputStream(xslTemplateReplacedStr.getBytes())));
    			StringWriter strWriter = new StringWriter();
    			transformer.transform(new DOMSource(xmlInpDoc), new StreamResult(strWriter));
    			
    			Document xmlInpDoc1 = xmlDocumentBuilder.parse(new ByteArrayInputStream((strWriter.toString()).getBytes()));
    			String str1 = serializeXmlDomElementNode(xmlInpDoc1);
    			String str2 = serializeXmlDomElementNode(xmlInpDoc);
    			
    			if (str1.equals(str2)) {
    				elemTestResult.setAttribute("status", "pass");
    			}
    			else {
    				elemTestResult.setAttribute("status", "fail");
    			}
    		}*/
    		else if ("all-of".equals(expectedNodeKindName)) {
    			// This is currently not supported. We need to fix
    			// a bug within commented code block above.
    			elemTestResult.setAttribute("status", "fail");
    		}
            else if ("assert-xml".equals(expectedNodeKindName)) {
            	Element elemNode = (Element)nodeExpected;
            	String fileName = elemNode.getAttribute("file");
            	String expectedResultStr = null;
            	if (!"".equals(fileName)) {
            		URI uri = new URI(xslTransformTestSetFilePath);
            		uri = uri.resolve(fileName);
            		expectedResultStr = getStringContentFromUrl(uri.toURL());
            	}
            	else {
            	   expectedResultStr = elemNode.getTextContent();            		
            	}
            	
            	Document xmlInpDoc1 = xmlDocumentBuilder.parse(new ByteArrayInputStream((resultStrWriter.toString()).getBytes()));
    			String str1 = serializeXmlDomElementNode(xmlInpDoc1);
    			Document xmlInpDoc2 = xmlDocumentBuilder.parse(new ByteArrayInputStream((expectedResultStr).getBytes()));
    			String str2 = serializeXmlDomElementNode(xmlInpDoc2);
    			if (str1.equals(str2)) {
    				elemTestResult.setAttribute("status", "pass");
    			}
    			else {
    				elemTestResult.setAttribute("status", "fail");
    			}
    		}
    		
    		elemTestRun.appendChild(elemTestResult);
    	}
    	finally {
    		trfErrorList.clear();
    		trfFatalErrorList.clear();
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
                                                               String xslGoldFilePath, 
                                                               XslTestsErrorHandler xslTransformErrHandler) {
        try {
           String xmlDocumentUriStr = ((new File(xmlFilePath)).toURI()).toString();
           String xslDocumentUriStr = ((new File(xslFilePath)).toURI()).toString();
           
           Node xmlDomSource = xmlDocumentBuilder.parse(new InputSource(xmlDocumentUriStr));
       
           Transformer transformer = xslTransformerFactory.newTransformer(new StreamSource(xslDocumentUriStr));
           
           setXslTransformProperties(transformer);
           
           if (xslTransformErrHandler != null) {
               transformer.setErrorListener(xslTransformErrHandler);  
           }
           
           StringWriter resultStrWriter = new StringWriter();
           
           DOMSource xmlDomSrc = new DOMSource(xmlDomSource, xmlDocumentUriStr);
           
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
              
              if ((XSLTestConstants.XML).equals(fileComparisonType)) {            	              	  
            	  if (!isXMLFileContentsEqual(new String(goldFileBytes), resultStrWriter.toString())) {
            		  Assert.fail(); 
            	  }
              }
              else if ((XSLTestConstants.JSON).equals(fileComparisonType)) {
  				  if (!isJsonFileContentsEqual(new String(goldFileBytes), resultStrWriter.toString())) {
  				     Assert.fail();
  				  } 
              }
              else if ((XSLTestConstants.TEXT).equals(fileComparisonType) || (XSLTestConstants.HTML).equals(fileComparisonType)) {
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
     protected void runXslTransformAndAssertOutput(String xmlFilePath, String xslFilePath, 
    		                                           String[] goldFileStrArr, String[] goldFileNameArr, 
    		                                           XslTestsErrorHandler xslTransformErrHandler) {    	     	 
 		
    	 String fileProducedName1 = null;
    	 String fileProducedName2 = null;
    	 
    	 try {
    		String xmlDocumentUriStr = ((new File(xmlFilePath)).toURI()).toString();
    		String xslDocumentUriStr = ((new File(xslFilePath)).toURI()).toString();

    		Node xmlDomSource = xmlDocumentBuilder.parse(new InputSource(xmlDocumentUriStr));

    		Transformer transformer = xslTransformerFactory.newTransformer(new StreamSource(xslDocumentUriStr));

    		setXslTransformProperties(transformer);

    		if (xslTransformErrHandler != null) {
    			transformer.setErrorListener(xslTransformErrHandler);  
    		}

    		StringWriter resultStrWriter = new StringWriter();

    		DOMSource xmlDomSrc = new DOMSource(xmlDomSource, xmlDocumentUriStr);

    		transformer.transform(xmlDomSrc, new StreamResult(resultStrWriter));
    		
    		if (goldFileNameArr.length == 2) {
    			fileProducedName1 = (xslTransformInpPath != null) ? (xslTransformInpPath + 
    					                                                          goldFileNameArr[0]) : goldFileNameArr[0];
    			fileProducedName2 = (xslTransformInpPath != null) ? (xslTransformInpPath + 
    					                                                          goldFileNameArr[1]) : goldFileNameArr[1];

    			String fileProducedStr1 = getFileContentAsString(fileProducedName1);
    			String fileProducedStr2 = getFileContentAsString(fileProducedName2);
    			
    			if ((XSLTestConstants.XML).equals(fileComparisonType)) {    				
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
    			else if ((XSLTestConstants.JSON).equals(fileComparisonType)) {
    				boolean fileComparisonResult1 = isJsonFileContentsEqual(goldFileStrArr[0], fileProducedStr1);
    				boolean fileComparisonResult2 = isJsonFileContentsEqual(goldFileStrArr[1], fileProducedStr2);
    				if (!(fileComparisonResult1 && fileComparisonResult2)) {
    				   Assert.fail();
    				}
    			}
    			else if ((XSLTestConstants.TEXT).equals(fileComparisonType) || (XSLTestConstants.HTML).equals(fileComparisonType)) {
    				boolean fileComparisonResult1 = (goldFileStrArr[0]).equals(fileProducedStr1); 
    				boolean fileComparisonResult2 = (goldFileStrArr[1]).equals(fileProducedStr2);
    				if (!(fileComparisonResult1 && fileComparisonResult2)) {
     				   Assert.fail();
     				}
                }
    		}
    		else {
    			// Only one file is there to compare it's contents
    			
    			fileProducedName1 = (xslTransformInpPath != null) ? (xslTransformInpPath + 
                                                                                  goldFileNameArr[0]) : goldFileNameArr[0];

    			String fileProducedStr1 = getFileContentAsString(fileProducedName1);

    			if ((XSLTestConstants.XML).equals(fileComparisonType)) { 
    				if (!isXMLFileContentsEqual(goldFileStrArr[0], fileProducedStr1)) {
     				   Assert.fail();	
     				}
    			}
    			else if ((XSLTestConstants.JSON).equals(fileComparisonType)) {
    				if (!isJsonFileContentsEqual(goldFileStrArr[0], fileProducedStr1)) {
    				   Assert.fail();	
    				}
    			}
    			else if ((XSLTestConstants.TEXT).equals(fileComparisonType) || (XSLTestConstants.HTML).equals(fileComparisonType)) {
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

    		Node xmlDomSource = xmlDocumentBuilder.parse(new InputSource(xmlDocumentUriStr));

    		Transformer transformer = xslTransformerFactory.newTransformer(new StreamSource(xslDocumentUriStr));

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
    			
    			if ((XSLTestConstants.XML).equals(fileComparisonType)) {
    				if (!isXMLFileContentsEqual(new String(goldFileBytes), resultStrWriter.toString())) {
    					Assert.fail();	
    				}	
    			}
    			else if ((XSLTestConstants.JSON).equals(fileComparisonType)) {
    				if (!isJsonFileContentsEqual(new String(goldFileBytes), resultStrWriter.toString())) {
    					Assert.fail();	
    				}
    			}
    			else if ((XSLTestConstants.TEXT).equals(fileComparisonType) || (XSLTestConstants.HTML).equals(fileComparisonType)) {
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
           
           Node xmlDomSource = xmlDocumentBuilder.parse(xmlDocumentUriStr);
       
           Transformer xslTransformer = xslTransformerFactory.newTransformer(new 
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

        	   if ((XSLTestConstants.XML).equals(fileComparisonType)) {
        		   if (!isXMLFileContentsEqual(new String(goldFileBytes), resultStrWriter.toString())) {
        			   Assert.fail();	
        		   }	
        	   }
        	   else if ((XSLTestConstants.JSON).equals(fileComparisonType)) {
        		   if (!isJsonFileContentsEqual(new String(goldFileBytes), resultStrWriter.toString())) {
        			   Assert.fail();	
        		   }
        	   }
        	   else if ((XSLTestConstants.TEXT).equals(fileComparisonType) || (XSLTestConstants.HTML).equals(fileComparisonType)) {
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
    	isXmlValidationEnabled = isEnableValidation;
    }
    
    /**
     * Set an XSL transformation, xsl:evaluate instruction evaluation property.
     */
    protected void setXslEvaluateProperty(boolean isXslEvaluateEnable) {
    	isXslEvaluateEnabled = isXslEvaluateEnable;
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
     * Set XML Schema validation and xsl:evaluate XSL transformation properties, on 
     * Xalan-J's TransformerImpl object. 
     */
    private void setXslTransformProperties(Transformer transformer) throws TransformerException {
    	if (isXmlValidationEnabled) {
    		TransformerImpl transformerImpl = (TransformerImpl)transformer;
    		transformerImpl.setProperty(TransformerImpl.XML_VALIDATION_PROPERTY, Boolean.TRUE);
    	}

    	if (isXslEvaluateEnabled) {
    		TransformerImpl transformerImpl = (TransformerImpl)transformer;
    		transformerImpl.setProperty(TransformerImpl.XSL_EVALUATE_PROPERTY, Boolean.TRUE); 
    	}
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
