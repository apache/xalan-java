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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.utils.Constants;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import junit.framework.Assert;

/**
 * A class providing, various common services for Xalan-J's 
 * XSL 3 test suite.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * @author Vladimir Sitnikov <sitnikov.vladimir@gmail.com>
 * 
 * @xsl.usage advanced
 */
public class XSLTransformTestsUtil {        
    
	/**
	 * Class field representing an XML DocumentBuilderFactory object instance 
	 * needed by this test suite. 
	 */
    protected static DocumentBuilderFactory xmlDocumentBuilderFactory = null;
    
    /**
     * Class field representing an XML DocumentBuilder object instance 
     * needed by this test suite.
     */
    protected static DocumentBuilder xmlDocumentBuilder = null;
    
    /**
     * Class field representing an XSL TransformerFactory object instance 
     * needed by this test suite.
     */
    protected static TransformerFactory xslTransformerFactory = null;
    
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
    
    /**
     * Class constructor.
     */
    public XSLTransformTestsUtil() {
        System.setProperty(Constants.XML_DOCUMENT_BUILDER_FACTORY_KEY, Constants.XML_DOCUMENT_BUILDER_FACTORY_VALUE);
        System.setProperty(XSLConstants.XSLT_TRANSFORMER_FACTORY_KEY, XSLConstants.XSLT_TRANSFORMER_FACTORY_VALUE);                
        
        xmlDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
        xmlDocumentBuilderFactory.setNamespaceAware(true);
        
        try {
            xmlDocumentBuilder = xmlDocumentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {            
            ex.printStackTrace();
        }
        
        xslTransformerFactory = TransformerFactory.newInstance();
    }
    
    /**
     * This method is used by, Xalan-J Java extension functions used
     * within few .xsl test files.
     */
    public static Date getCurrentDate() {
        Date currentDate = new Date();
      
        return currentDate;
    }
   
    /**
     * This method is used by, Xalan-J Java extension functions used
     * within few .xsl test files.
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
           
              Assert.assertEquals(new String(goldFileBytes), resultStrWriter.toString());
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
    	 
    	 String fileProducedName1 = goldFileNameArr[0];
 		 String fileProducedName2 = goldFileNameArr[1];
 		
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
    		
        	String fileProducedStr1 = getFileContentAsString(fileProducedName1);
        	String fileProducedStr2 = getFileContentAsString(fileProducedName2);
        	
        	if (!(goldFileStrArr[0].equals(fileProducedStr1) && goldFileStrArr[1].equals(fileProducedStr2))) {
        	   Assert.fail();
        	}
    	}
    	catch (Exception ex) {
    		Assert.fail();    
    	}
    	finally {    		    		
    		// These statements delete the temporary files 
    		// produced while running a test case using this method.
    		(new File(fileProducedName1)).delete();
    		(new File(fileProducedName2)).delete();
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

    			Assert.assertEquals(new String(goldFileBytes), resultStrWriter.toString());
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
           
              Assert.assertEquals(new String(goldFileBytes), resultStrWriter.toString());
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
     * Get the file contents as String, given the 
     * name of the file as an argument to this method.
     */
    protected String getFileContentAsString(String fileName) {
    	
    	String fileContentStr = null;
    	
    	FileInputStream fileInputStream = null;
    	
    	try {
    		fileInputStream = new FileInputStream(fileName);
    		StringBuffer strBuff = new StringBuffer();
    		byte[] byteArr = new byte[512];
    		int noOfCharsRead = -1;
    		while ((noOfCharsRead = fileInputStream.read(byteArr)) != -1) {
    			String strRead = new String(byteArr);
    			strBuff.append(strRead);
    		}
    		
    		fileContentStr = strBuff.toString();

    		fileInputStream.close();
    	}
    	catch (Exception ex) {
    		ex.printStackTrace();
    	}
    	finally {
    		if (fileInputStream != null) {
    			try {
    				fileInputStream.close();
    			} catch (IOException ex) {
    				ex.printStackTrace();
    			}
    		}
    	}
    	
    	return fileContentStr;
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
    
}
