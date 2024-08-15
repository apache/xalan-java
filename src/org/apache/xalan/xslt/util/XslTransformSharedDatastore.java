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
package org.apache.xalan.xslt.util;

import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.objects.XPathMap;

/**
 * This class, has few XSL stylesheet transformation-wide 
 * variables that're used during evaluation of specific XSLT 
 * instructions and XPath expressions.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XslTransformSharedDatastore {
    
	/**    
     * This class field represents, an XSL stylesheet 
     * document's uri, if available.
     */
    public static String xslSystemId;
    
    /**
     * This class field represents, function items that're 
     * returned from XSL stylesheet xsl:function calls.
     */
    public static XPathInlineFunction xpathInlineFunction;
    
    /**
     * This class field represents, XPath array values
     * returned from evaluation of xsl:evaluate 
     * instruction.
     */
    public static XPathArray xpathArray;
    
    /**
     * This class field represents, XPath map values
     * returned from evaluation of xsl:evaluate 
     * instruction.
     */
    public static XPathMap xpathMap;
    
    /**
     * This class field represents, compiled representation 
     * of an XSL stylesheet.
     */
    public static StylesheetRoot stylesheetRoot;

}
