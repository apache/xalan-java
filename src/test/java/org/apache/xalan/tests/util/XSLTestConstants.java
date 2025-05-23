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

/**
 * A class defining constants, used by Xalan-J's 
 * XSL 3 test suite.
 *  
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XSLTestConstants {        
    
    public static final String XSLT_TRANSFORMER_FACTORY_KEY = "javax.xml.transform.TransformerFactory";
    
    public static final String XSLT_TRANSFORMER_FACTORY_VALUE = "org.apache.xalan.processor.XSL3TransformerFactoryImpl";
    
    public static final String XSL_TRANSFORM_INPUT_DIRPATH_PREFIX = "./src/test/resources/";
    
    public static final String XSL_TRANSFORM_GOLD_DIRPATH_PREFIX = "./src/test/resources/";
    
    public static final String XML = "xml";
    
    public static final String JSON = "json";
    
    public static final String TEXT = "text";
    
    public static final String HTML = "html";
    
    
    /**
     * Few XSL tests within this test suite, require the workstation where these tests 
     * are running to be connected to web. To enable proper working of these tests, the 
     * value of this class field needs to be set to boolean 'true'. 
     */
    public static final boolean IS_TESTS_USING_ONLINE_URIS_ENABLED = true;
    
    /**
     * Few XSL tests within this test suite, require specifying absolute URI of local files 
     * with file: scheme. The following two class field variables are used for these XSL tests 
     * within this test suite. Both the file local absolute URI prefixes file:/d:/ and file:/d:/// are 
     * appropriate examples for Windows. For Linux, there are similar URI naming conventions. User running
     * this test suite on Windows needs to change values of following two class field variables as 
     * per Xalan-J's XSLT 3.0 implementation src code's local folder. For Linux, an appropriate local 
     * absolute URI prefix needs to be specified. 
     */
    
    public static final String LOCAL_BASE_URI_PREFIX_FOR_TESTS_1 = "file:/d:/eclipseWorkspaces/xalanj/xalan-j_xslt3.0_mvn/src/test/resources/";
    
    public static final String LOCAL_BASE_URI_PREFIX_FOR_TESTS_2 = "file:/d:///eclipseWorkspaces/xalanj/xalan-j_xslt3.0_mvn/src/test/resources/";

}
