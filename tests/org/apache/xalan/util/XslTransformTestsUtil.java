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
 * A class providing, common services to this JUnit test suite.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * @author Vladimir Sitnikov <sitnikov.vladimir@gmail.com>
 * 
 * @xsl.usage advanced
 */
public class XslTransformTestsUtil {        
    
    protected static DocumentBuilderFactory xmlDocumentBuilderFactory = null;
    
    protected static DocumentBuilder xmlDocumentBuilder = null;
    
    protected static TransformerFactory xslTransformerFactory = null;
    
    /*
     * Class constructor.
     */
    public XslTransformTestsUtil() {
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

    protected void runXslTransformAndAssertOutput(String xmlFilePath, String xslFilePath, 
                                                            String goldFilePath, 
                                                            XslTestsErrorHandler errHandler) {
        try {
           Node xmlDomSource = xmlDocumentBuilder.parse(new InputSource(xmlFilePath));
       
           Transformer xslTransformer = xslTransformerFactory.newTransformer(new StreamSource(xslFilePath));
           if (errHandler != null) {
               xslTransformer.setErrorListener(errHandler);  
           }
           
           StringWriter resultStrWriter = new StringWriter();
           
           xslTransformer.transform(new DOMSource(xmlDomSource), new StreamResult(resultStrWriter));
           
           if (errHandler != null) {
               List<String> trfErrorList = errHandler.getTrfErrorList();
               List<String> trfFatalErrorList = errHandler.getTrfFatalErrorList();
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
              byte[] goldFileBytes = Files.readAllBytes(Paths.get(goldFilePath));
           
              Assert.assertEquals(new String(goldFileBytes), resultStrWriter.toString());
           }
        }
        catch (Exception ex) {
            Assert.fail();    
        }
     }
    
     public static Date getCurrentDate() {
         Date currentDate = new Date();
       
         return currentDate;
     }
    
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
