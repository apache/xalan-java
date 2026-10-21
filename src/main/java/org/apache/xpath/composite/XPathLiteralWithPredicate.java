/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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

import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSDecimal;
import xml.xpath31.processor.types.XSString;

/**
 * Class definition, representing an XPath 3.1 compiled expression
 * for, XPath expression like literal[predicate].
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 */
public class XPathLiteralWithPredicate extends Expression {

	private static final long serialVersionUID = -5849863112943832262L;
	
	/**
	 * An XPath expression literal string value.
	 */
	private String m_literal_str = null;
	
	/**
	 * An XPath expression representing a predicate string value,
	 * that filters whether the literal should be returned or not 
	 * by an XPath expression.
	 */
	private String m_xpath_predicate_str = null;
	
    private Vector m_vars;
	
	private int m_globals_size;

	@Override
	public XObject execute(XPathContext xctxt) throws TransformerException {
		
		XObject result = null;
		
        SourceLocator srcLocator = null;
        
        final int sourceNode = xctxt.getCurrentNode(); 
		
		List<XMLNSDecl> prefixTable = XslTransformEvaluationHelper.getXSLNsPrefixTable(xctxt);
		
		if (prefixTable != null) {
		   m_xpath_predicate_str = XslTransformEvaluationHelper.replaceXmlNsUrisWithPrefixesXPathStr(m_xpath_predicate_str, prefixTable);
		}
		
		XPath xpathPredicateObj = new XPath(m_xpath_predicate_str, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
		
		if (m_vars != null) {
			xpathPredicateObj.fixupVariables(m_vars, m_globals_size);
		}
		
		XObject xpathCtxtItem = null;
		
		try {
		   xpathCtxtItem = new XSDecimal(m_literal_str);
		}
		catch (NumberFormatException ex) {
		   xpathCtxtItem = new XSString(m_literal_str);
		}
		
		XObject xObj1 = null;
		
		XObject prevXPathCtxtItem = xctxt.getXPath3ContextItem(); 
		
		xctxt.setXPath3ContextItem(xpathCtxtItem);
		
		try {
		   xObj1 = xpathPredicateObj.execute(xctxt, sourceNode, xctxt.getNamespaceContext());
		}
		finally {
		   xctxt.setXPath3ContextItem(prevXPathCtxtItem);
		}
		
		if (xObj1.bool()) {
		   XPath literalXPathObj = new XPath(m_literal_str, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
		   
		   result = literalXPathObj.execute(xctxt, DTM.NULL, xctxt.getNamespaceContext());; 
		}
		else {
		   result = new ResultSequence();
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
		// No op
	}

	@Override
	public boolean deepEquals(Expression expr) {
		// No op
		return false;
	}

	public String getLiteralStr() {
		return m_literal_str;
	}

	public void setLiteralStr(String literal) {
		this.m_literal_str = literal;
	}

	public String getPredicateStr() {
		return m_xpath_predicate_str;
	}

	public void setPredicateStr(String predicateStr) {
		this.m_xpath_predicate_str = predicateStr;
	}

}
