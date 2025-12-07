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
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.objects.XObject;

/**
 * This class definition, evaluates particular types of XPath 3.1 
 * function calls, for e.g an XPath effective function call 
 * resulting from XPath expression like (1,2,3,4,5) => tail().
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced 
 */
public class XPathFunctionCall2 extends Expression {

	private static final long serialVersionUID = -6325019736212325986L;
	
	/**
	 * Class field, representing an XPath expression string
	 * for function call.
	 */
	private String m_func_call_expr = null;
	
    private Vector m_vars;
    
    private int m_globals_size;

	@Override
	public XObject execute(XPathContext xctxt) throws TransformerException {				
		
		XObject result = null;

		SourceLocator srcLocator = xctxt.getSAXLocator();
		
		final int contextNode = xctxt.getCurrentNode();

		ElemTemplateElement elemTemplateElement = (ElemTemplateElement)xctxt.getNamespaceContext();
		List<XMLNSDecl> prefixTable = null;
		if (elemTemplateElement != null) {
			prefixTable = (List<XMLNSDecl>)elemTemplateElement.getPrefixTable();
		}

		if (prefixTable != null) {
			m_func_call_expr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(m_func_call_expr, prefixTable);
		}

		XPath xpathObj = new XPath(m_func_call_expr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);

		if (m_vars != null) {
			xpathObj.fixupVariables(m_vars, m_globals_size);
		}
		
		result = xpathObj.execute(xctxt, contextNode, xctxt.getNamespaceContext());

		return result;
	}
	
	@Override
	public void callVisitors(ExpressionOwner owner, XPathVisitor visitor) {
		// NO OP
	}

	@Override
	public void fixupVariables(Vector vars, int globalsSize) {
		m_vars = (Vector)(vars.clone());
    	m_globals_size = globalsSize; 
	}

	@Override
	public boolean deepEquals(Expression expr) {
		// NO OP
		
		return false;
	}

	public String getFuncCallExpr() {
		return m_func_call_expr;
	}

	public void setFuncCallExpr(String func_call_expr) {
		this.m_func_call_expr = func_call_expr;
	}

}
