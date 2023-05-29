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
package org.apache.xalan.xpath3;

import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.xalan.xslt3.XSLConstants;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import junit.framework.Assert;

/**
 * XPath function fn:string-join test cases.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FnStringJoinTests {
    
    private static final String xslTransformInputDirPath = XSLConstants.XSL_TRANSFORM_INPUT_DIR_PATH_PREFIX + "fn_string_join/";
    
    private static final String xslTransformGoldDirPath = XSLConstants.XSL_TRANSFORM_GOLD_DIR_PATH_PREFIX + "fn_string_join/gold/";
    
    private static TransformerFactory tfactory = null;
    
    private static DocumentBuilderFactory dfactory = null;
    
    private static DocumentBuilder docBuilder = null;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        System.setProperty(XSLConstants.XERCES_DOCUMENT_BUILDER_FACTORY_KEY, XSLConstants.XERCES_DOCUMENT_BUILDER_FACTORY_VALUE);
        System.setProperty(XSLConstants.XSLT_TRANSFORMER_FACTORY_KEY, XSLConstants.XSLT_TRANSFORMER_FACTORY_VALUE);
        
        tfactory = TransformerFactory.newInstance();
        
        dfactory = DocumentBuilderFactory.newInstance();
        docBuilder = dfactory.newDocumentBuilder();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        tfactory = null;
        dfactory = null;
        docBuilder = null;
    }

    @Test
    public void test1() {
        String xmlFilePath = xslTransformInputDirPath + "test1.xsl"; 
        String xslFilePath = xslTransformInputDirPath + "test1.xsl";
        
        String goldFilePath = xslTransformGoldDirPath + "test1.out";                
        
        try {
           Node xmlDomSource = docBuilder.parse(new InputSource(xmlFilePath));
        
           Transformer xslTransformer = tfactory.newTransformer(new StreamSource(xslFilePath));
           StringWriter resultStrWriter = new StringWriter();
           xslTransformer.transform(new DOMSource(xmlDomSource), new StreamResult(resultStrWriter));
           
           byte[] goldFileBytes = Files.readAllBytes(Paths.get(goldFilePath));
           
           Assert.assertEquals(new String(goldFileBytes), resultStrWriter.toString());           
        }
        catch (Exception ex) {
            Assert.fail();    
        }
    }

}
