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

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;

/**
 * This class definition, provides implementation of XPath general 
 * comparison operators (i.e, =, !=, <, <=, >, >=) for XPath ExprSingle 
 * expression (one of the XPath expressions 'for', 'let', 'some', 
 * 'every', 'if') as LHS operand and another appropriate XPath operand 
 * as RHS.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPath3ExprSingleComparison extends Expression {

	private static final long serialVersionUID = 5684685034365152001L;
	
	/**
	 * An XPath expression string for general comparison operator's 
	 * LHS operand.
	 */
    private String m_xpathExprLhs = null;
	
	/**
	 * An XPath expression list for general comparison operator's 
	 * RHS operand.
	 */
	private List<String> m_seqArrConstructorXPathRhs = new ArrayList<String>();
	
	/**
     * Xalan-J's XPath general comparison operator's op code.
     */
	private int m_comparisonOpCode;
    
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
        
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
        int contextNode = xctxt.getContextNode();
        
        ElemTemplateElement elemTemplateElement = (ElemTemplateElement)xctxt.getNamespaceContext();
        List<XMLNSDecl> prefixTable = null;
        if (elemTemplateElement != null) {
            prefixTable = (List<XMLNSDecl>)elemTemplateElement.getPrefixTable();
        }
        
        if (prefixTable != null) {
        	m_xpathExprLhs = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(m_xpathExprLhs, prefixTable);
        }

        XPath xpathObj = new XPath(m_xpathExprLhs, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
        if (m_vars != null) {
        	xpathObj.fixupVariables(m_vars, m_globals_size);
        }

        XObject xObjLhs = xpathObj.execute(xctxt, contextNode, xctxt.getNamespaceContext());
        ResultSequence resultSeqLhs = new ResultSequence();
        if (xObjLhs instanceof ResultSequence) {
           resultSeqLhs = (ResultSequence)xObjLhs;
        }
        else {
           resultSeqLhs.add(xObjLhs);
        }
        
        ResultSequence resultSeqRhs = new ResultSequence();
        for (int idx = 0; idx < m_seqArrConstructorXPathRhs.size(); idx++) {
        	String xpathExprStr = m_seqArrConstructorXPathRhs.get(idx);        	
        	if (prefixTable != null) {
        		xpathExprStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(xpathExprStr, prefixTable);
        	}

        	XPath xpathObj2 = new XPath(xpathExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
        	if (m_vars != null) {
        		xpathObj2.fixupVariables(m_vars, m_globals_size);
        	}

        	XObject xObj = xpathObj2.execute(xctxt, contextNode, xctxt.getNamespaceContext());
        	resultSeqRhs.add(xObj);
        }
        
        result = compareSequence(resultSeqLhs, resultSeqRhs, m_comparisonOpCode);
        
        return result;
    }

    @Override
    public void fixupVariables(Vector vars, int globalsSize) {
        m_vars = (Vector)(vars.clone());
        m_globals_size = globalsSize;
    }
    
    @Override
    public void callVisitors(ExpressionOwner owner, XPathVisitor visitor) {
        // no op
    }

    @Override
    public boolean deepEquals(Expression expr) {
        return false;
    }

	public String getXPathExprLhs() {
		return m_xpathExprLhs;
	}

	public void setXPathExprLhs(String xpathExprLhs) {
		this.m_xpathExprLhs = xpathExprLhs;
	}

	public int getComparisonOpCode() {
		return m_comparisonOpCode;
	}

	public void setComparisonOpCode(int comparisonOpCode) {
		this.m_comparisonOpCode = comparisonOpCode;
	}

	public List<String> getSeqArrConstructorXPathRhs() {
		return m_seqArrConstructorXPathRhs;
	}

	public void setSeqArrConstructorXPathRhs(List<String> seqArrConstructorXPathRhs) {
		this.m_seqArrConstructorXPathRhs = seqArrConstructorXPathRhs;
	}

}
