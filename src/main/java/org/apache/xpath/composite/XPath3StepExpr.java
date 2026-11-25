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
 * Class definition, to implement an XPath 3.1 expression like, 
 *    1) /a/b/(for ...)
 *    2) /a/b/(let ...) etc     
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPath3StepExpr extends Expression {

	private static final long serialVersionUID = -2120248292624857704L;
	
	/**
	 * Class field that represents following:
	 * For an XPath expression /a/b/(for ...) , an XPath expression prefix /a/b. 
	 */
	private String m_str1 = null;
	
	/**
	 * Class field that represents following:
	 * For an XPath expression /a/b/(for ...) , an XPath expression suffix for ... 
	 */
	private String m_str2 = null;
	
    private Vector m_vars;
    
    private int m_globals_size;

	@Override
	public XObject execute(XPathContext xctxt) throws TransformerException {
		
		XObject result = null;

		SourceLocator srcLocator = xctxt.getSAXLocator(); 

		final int sourceNode = xctxt.getCurrentNode();

		List<XMLNSDecl> prefixTable = XslTransformEvaluationHelper.getXSLNsPrefixTable(xctxt);

		if (prefixTable != null) {
			m_str1 = XslTransformEvaluationHelper.replaceXmlNsUrisWithPrefixesXPathStr(m_str1, prefixTable);
			m_str2 = XslTransformEvaluationHelper.replaceXmlNsUrisWithPrefixesXPathStr(m_str2, prefixTable); 
		}
		
		XPath xpathObj1 = new XPath(m_str1, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
		
		if (m_vars != null) {
		   xpathObj1.fixupVariables(m_vars, m_globals_size);
		}
		
		XObject xObj = xpathObj1.execute(xctxt, sourceNode, xctxt.getNamespaceContext());
		
		XPath xpathObj2 = new XPath(m_str2, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
		
		if (m_vars != null) {
			xpathObj2.fixupVariables(m_vars, m_globals_size);
		}
		
		ResultSequence rSeq = new ResultSequence(); 
		
		if (xObj instanceof XMLNodeCursorImpl) {
		   XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xObj;
		   DTMCursorIterator dtmCursorIter = xmlNodeCursorImpl.iter();
		   
		   int nextNode = DTM.NULL;
		   
		   while ((nextNode = dtmCursorIter.nextNode()) != DTM.NULL) {
			  xctxt.pushCurrentNode(nextNode);
			  
			  DTM dtm = xctxt.getDTM(nextNode);
			  String nodeName = dtm.getNodeName(nextNode);
			  
			  try {
				  XObject xObj2 = xpathObj2.execute(xctxt, nextNode, xctxt.getNamespaceContext());
				  
				  rSeq.add(xObj2);
			  }
			  finally {
				  xctxt.popCurrentNode();
			  }
		   }
		   
		   ResultSequence rSeq1 = new ResultSequence(); 		   
		   XslTransformEvaluationHelper.expandResultSequence(rSeq, rSeq1);
		   
		   rSeq = rSeq1; 
		}
		else {
		   // Revisit	
		}
		
		result = rSeq; 

		return result;
	}

	@Override
	public void fixupVariables(Vector vars, int globalsSize) {
		m_vars = (Vector)(vars.clone());
        m_globals_size = globalsSize;
	}

	@Override
	public boolean deepEquals(Expression expr) {
		// No op
		
		return false;
	}
	
	@Override
	public void callVisitors(ExpressionOwner owner, XPathVisitor visitor) {
		// No op
	}

	public String getStr1() {
		return m_str1;
	}

	public void setStr1(String str1) {
		this.m_str1 = str1;
	}

	public String getStr2() {
		return m_str2;
	}

	public void setStr2(String str2) {
		this.m_str2 = str2;
	}

}
