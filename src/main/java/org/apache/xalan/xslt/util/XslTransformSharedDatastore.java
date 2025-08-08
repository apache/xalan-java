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
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.objects.XPathMap;

/**
 * A class definition, that has few XSL stylesheet transformation 
 * wide variables that are used while evaluating specific XSL 
 * stylesheet instructions and XPath expressions.
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
    public static String m_xslSystemId;
    
    /**
     * This class field represents, xdm function items that 
     * are returned from XSL stylesheet xsl:function calls.
     */
    public static XPathInlineFunction m_xpathInlineFunction;
    
    /**
     * This class field represents, xdm array values returned from 
     * evaluation of xsl:sequence & xsl:evaluate instruction.
     */
    public static XPathArray m_xpathArray;
    
    /**
     * This class field represents, xdm map values
     * returned from evaluation of xsl:evaluate 
     * instruction.
     */
    public static XPathMap m_xpathMap;
    
    /**
     * This class field represents, compiled representation 
     * of an XSL stylesheet root object.
     */
    public static StylesheetRoot m_stylesheetRoot;
    
    /**
     * This class field represents, a string buffer value which is
     * RHS of an XPath expression like '* except (a,b)' i.e (a,b) and when used 
     * with a node combining operator like union (and equivalently '|'), 
     * intersect or except.
     */
    public static StringBuffer m_xpathNodeCombiningExprRhsStrBuff;
    
    /**
     * This class field represents, XPath predicate suffix for XSL 
     * template definition like xsl:template match=".[..]".
     */
    public static String m_templateMatchDotPatternPredicateStr;
    
    /**
     * This class field represents, an org.apache.xpath.compiler.OpCodes 
     * value, when evaluating an XPath 'idiv' operation. This class variable
     * is initialized to a reasonably safest integer value for which, a 
     * functional org.apache.xpath.compiler.OpCodes value will not exist. 
     */
    public static int m_xpathCallingOpCode = Integer.MIN_VALUE;
    
    /**
     * This class field is used to return XPath 'named function reference' 
     * compiled information from an XSL stylesheet function call.
     */
    public static ResultSequence m_xpathNamedFunctionRefSequence = new ResultSequence();
    
    /**
     * This class field is used to refer to a document node, that an
     * xsl:document instruction's evaluation has produced.
     */
    public static XMLNodeCursorImpl m_xslDocumentEvaluationResult;
    
    /**
     * Class field, to support Xalan-J's test driver for W3C XSLT 3.0 test suite. 
     */
    public static boolean m_is_xsl_test_invocation = false;
    
    /**
	 * Method definition to reset the variable values specified 
	 * within this class.
	 */
	public static void reset() {
		m_xslSystemId = null;
		m_xpathInlineFunction = null;
		m_xpathArray = null;
		m_xpathMap = null;
		m_stylesheetRoot = null;
		m_xpathNodeCombiningExprRhsStrBuff = null;
		m_templateMatchDotPatternPredicateStr = null;
		m_xpathCallingOpCode = Integer.MIN_VALUE;
		m_xpathNamedFunctionRefSequence.clear();
		m_xslDocumentEvaluationResult = null;
		m_is_xsl_test_invocation = false;
	}		

}
