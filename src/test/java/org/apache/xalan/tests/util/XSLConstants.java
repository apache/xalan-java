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
 * The constant definitions, used by this XSLT and XPath test suite.
 *  
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XSLConstants {        
    
    public static final String XSLT_TRANSFORMER_FACTORY_KEY = "javax.xml.transform.TransformerFactory";
    
    public static final String XSLT_TRANSFORMER_FACTORY_VALUE = "org.apache.xalan.processor.TransformerFactoryImpl";
    
    public static final String XSL_TRANSFORM_INPUT_DIRPATH_PREFIX = "./src/test/resources/";
    
    public static final String XSL_TRANSFORM_GOLD_DIRPATH_PREFIX = "./src/test/resources/";
    
    
    /**
     * Few XSL tests within this test suite, require the workstation where these tests 
     * are running to be connected to web. To enable proper working of these tests, the 
     * value of this class field needs to be set to boolean 'true'. 
     */
    public static final boolean IS_TESTS_USING_ONLINE_URIS_ENABLED = true;
    
    /**
     * Few XSL tests within this test suite, require specifying absolute URI of local files 
     * with file: scheme. The following two class field variables are used for these XSL tests 
     * within this test suite.
     */    
    public static final String LOCAL_BASE_URI_PREFIX_OF_TESTS1 = "file:/D:/eclipseWorkspaces/xalanj/xalan-j_xslt3.0_mvn/src/test/resources/";
    
    public static final String LOCAL_BASE_URI_PREFIX_OF_TESTS2 = "file:/D:///eclipseWorkspaces/xalanj/xalan-j_xslt3.0_mvn/src/test/resources/";

}
