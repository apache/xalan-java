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
    
    // Currently, one test each within files FnBaseUriTests.java and FnDocumentUriTests.java, require
    // user workstation running these tests to be connected to web. Specify boolean value of following
    // class variable to false to disable running these tests.
    public static final boolean IS_TESTS_USING_ONLINE_URIS_ENABLED = true;

}
