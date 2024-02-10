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
package org.apache.xalan.util;

import java.io.File;
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
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.xalan.xslt3.XSLConstants;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import junit.framework.Assert;

/**
 * A class providing, various common services to this JUnit test 
 * suite.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * @author Vladimir Sitnikov <sitnikov.vladimir@gmail.com>
 * 
 * @xsl.usage advanced
 */
public class XSLTransformTestsUtil {        
    
    protected static DocumentBuilderFactory xmlDocumentBuilderFactory = null;
    
    protected static DocumentBuilder xmlDocumentBuilder = null;
    
    protected static TransformerFactory xslTransformerFactory = null;
    
    /**
     * Class constructor.
     */
    public XSLTransformTestsUtil() {
        System.setProperty(XSLConstants.XERCES_DOCUMENT_BUILDER_FACTORY_KEY, XSLConstants.XERCES_DOCUMENT_BUILDER_FACTORY_VALUE);
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
     * This function is the primary function, that is invoked by all the XalanJ XSL3 test 
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
     * This function is similar to the function 'runXslTransformAndAssertOutput' except
     * that, this function accepts a document URI representing an XML document to
     * be transformed via an XSL stylesheet.
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
      * This function is used by, few of XalanJ XSL3 Java extension functions used
      * within the .xsl test files.
      */
     public static Date getCurrentDate() {
         Date currentDate = new Date();
       
         return currentDate;
     }
    
     /**
      * This function is used by, few of XalanJ XSL3 Java extension functions used
      * within the .xsl test files.
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
