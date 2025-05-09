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
package org.apache.xpath.composite;

import java.util.List;
import java.util.Vector;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;

/**
 * An XPath parser, constructs an object instance of this class
 * to represent an XPath path expression with a function call 
 * suffix with form /temp/abc/func().
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPathExprFunctionCallSuffix extends Expression {

	private static final long serialVersionUID = -4971046034028352044L;
	
	/**
	 * An XPath path expression string.
	 */
	private String m_xpathExprStr = null;
	
	/**
	 * This class field is used during, XPath.fixupVariables(..) action 
	 * as performed within object of this class.  
	 */    
	private Vector m_vars;
	
	/**
	 * This class field is used during, XPath.fixupVariables(..) action 
	 * as performed within object of this class.  
	 */
	private int m_globals_size;
	
	
	@Override
	public XObject execute(XPathContext xctxt) throws TransformerException {
		XObject result = null;
		
        final int contextNode = xctxt.getCurrentNode();
    	
    	SourceLocator srcLocator = xctxt.getSAXLocator();
    	
    	ElemTemplateElement elemTemplateElement = (ElemTemplateElement)xctxt.getNamespaceContext();
    	
    	List<XMLNSDecl> prefixTable = null;
        if (elemTemplateElement != null) {
            prefixTable = (List<XMLNSDecl>)elemTemplateElement.getPrefixTable();
        }
        
        if (prefixTable != null) {
        	m_xpathExprStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(m_xpathExprStr, 
                                                                                                prefixTable);
        }
        
        /**
         * Split an XPath path expression string of form /temp/abc/func() into
         * two strings /temp/abc and func(). Evaluate the XPath path expression
         * /temp/abc as Xalan-J iterator, and for each node accessed during this
         * traversal as context node evaluate the function func(). For each
         * evaluation of function func(), add the result of this evaluation to a 
         * sequence which is the final desired result for XPath expression 
         * evaluation /temp/abc/func().  
         */
        
        int idx = m_xpathExprStr.lastIndexOf('/');
        String xpathLhsStr = m_xpathExprStr.substring(0, idx);
        String xpathRhsStr = m_xpathExprStr.substring(idx + 1);
    	
    	XPath xpathLhsObj = new XPath(xpathLhsStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
    	
    	if (m_vars != null) {
    		xpathLhsObj.fixupVariables(m_vars, m_globals_size);
        }
    	
    	XObject xObj = xpathLhsObj.execute(xctxt, contextNode, xctxt.getNamespaceContext());
    	
    	XPath xpathRhsObj = new XPath(xpathRhsStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
    	
    	if (xObj instanceof XMLNodeCursorImpl) {
    	   ResultSequence resultSeq = new ResultSequence();
    	   XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xObj;
    	   DTMCursorIterator dtmCursorIter = xmlNodeCursorImpl.iter();
    	   int nextNode;
    	   while ((nextNode = dtmCursorIter.nextNode()) != DTM.NULL) {
    		   XObject xObj2 = xpathRhsObj.execute(xctxt, nextNode, xctxt.getNamespaceContext());
    		   resultSeq.add(xObj2);
    	   }
    	   
    	   result = resultSeq; 
    	}
		
		return result;
	}

	@Override
	public void fixupVariables(Vector vars, int globalsSize) {
		m_vars = (Vector)(vars.clone());
        m_globals_size = globalsSize;
	}
	
	@Override
	public void callVisitors(ExpressionOwner owner, XPathVisitor visitor) {
		// NO OP
	}

	@Override
	public boolean deepEquals(Expression expr) {
		// NO OP
		return false;
	}

	public String getXPathExprStr() {
		return m_xpathExprStr;
	}

	public void setXPathExprStr(String xpathExprStr) {
		this.m_xpathExprStr = xpathExprStr;
	}

}
