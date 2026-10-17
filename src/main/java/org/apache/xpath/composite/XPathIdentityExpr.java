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
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.objects.XObject;

public class XPathIdentityExpr extends Expression {	

	private static final long serialVersionUID = -7777603348399435402L;
	
	private String m_xpathExprStr = null;
	
    private Vector m_vars;
    
    private int m_globals_size;

	@Override
	public XObject execute(XPathContext xctxt) throws TransformerException {
		
		XObject result = null;
		
		SourceLocator srcLocator = xctxt.getSAXLocator(); 
		
		final int sourceNode = xctxt.getCurrentNode();
		
		List<XMLNSDecl> prefixTable = XslTransformEvaluationHelper.getXSLNsPrefixTable(xctxt);
		
		if (prefixTable != null) {
		   m_xpathExprStr = XslTransformEvaluationHelper.replaceXmlNsUrisWithPrefixesXPathStr(m_xpathExprStr, prefixTable); 
		}
		
		XPath xpathObj = new XPath(m_xpathExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
		
		if (m_vars != null) {
			xpathObj.fixupVariables(m_vars, m_globals_size);
		}
		
		result = xpathObj.execute(xctxt, sourceNode, xctxt.getNamespaceContext()); 
		
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

	public String getXpathExprStr() {
		return m_xpathExprStr;
	}

	public void setXpathExprStr(String xpathExprStr) {
		this.m_xpathExprStr = xpathExprStr;
	}

}
