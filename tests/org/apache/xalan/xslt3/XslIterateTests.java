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

import org.apache.xalan.util.XslTransformTestsUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * XSLT 3.0 test cases for the xsl:iterate instruction.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XslIterateTests extends XslTransformTestsUtil {
    
    private static final String XSL_TRANSFORM_INPUT_DIRPATH = XSLConstants.XSL_TRANSFORM_INPUT_DIRPATH_PREFIX + 
                                                                                                 "w3c_xslt30_testsuite/insn/iterate/";
    
    private static final String XSL_TRANSFORM_GOLD_DIRPATH = XSLConstants.XSL_TRANSFORM_GOLD_DIRPATH_PREFIX + 
                                                                                                 "w3c_xslt30_testsuite/insn/iterate/gold/";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        // no op
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        xmlDocumentBuilderFactory = null;
        xmlDocumentBuilder = null;
        xslTransformerFactory = null;
    }

    @Test
    public void xslIterateTest1() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "iterate001.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "iterate-001.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "iterate-001.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }

}
