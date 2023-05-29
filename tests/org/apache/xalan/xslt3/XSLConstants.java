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
package org.apache.xalan.xslt3;

/**
 * The constant definitions, used by this XSLT and XPath test suite.
 *  
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XSLConstants {
    
    public static final String XERCES_DOCUMENT_BUILDER_FACTORY_KEY = "javax.xml.parsers.DocumentBuilderFactory";
    
    public static final String XERCES_DOCUMENT_BUILDER_FACTORY_VALUE = "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl";
    
    public static final String XSLT_TRANSFORMER_FACTORY_KEY = "javax.xml.transform.TransformerFactory";
    
    public static final String XSLT_TRANSFORMER_FACTORY_VALUE = "org.apache.xalan.processor.TransformerFactoryImpl";
    
    // the values of following, two variables are host specific where this test suite shall be run. the values of
    // following two variables may be modified, accordingly.
    
    public static final String XSL_TRANSFORM_INPUT_DIR_PATH_PREFIX = "file:///d:/eclipseWorkspaces/xalanj/xalan-java/tests/";
    
    public static final String XSL_TRANSFORM_GOLD_DIR_PATH_PREFIX = "d:/eclipseWorkspaces/xalanj/xalan-java/tests/";

}
