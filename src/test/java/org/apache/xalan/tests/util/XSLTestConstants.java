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
 * A class defining few constants, used by Xalan-J's XSL 3 test suite.
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
     * are running to be connected to web to access few test related files saved on Apache 
     * Xalan-J's server host. To enable proper working of these tests, the value of this 
     * class field needs to be set to boolean 'true'. If this variable is set to 'false',
     * these Xalan-J tests won't run and the tests build shall pass with a reduced total 
     * tests count. 
     */
    public static final boolean IS_TESTS_USING_ONLINE_URIS_ENABLED = true;

}
