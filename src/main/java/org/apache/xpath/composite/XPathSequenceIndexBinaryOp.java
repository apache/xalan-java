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
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.operations.CastAs;
import org.apache.xpath.operations.CastableAs;
import org.apache.xpath.operations.InstanceOf;
import org.apache.xpath.operations.TreatAs;

import xml.xpath31.processor.types.XSNumericType;

/**
 * A class definition, to implement XPath 3.1 binary operator
 * expressions like (1,2,3)[1] ,
 *                  (1, 2, 3)[1] treat as xs:integer
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPathSequenceIndexBinaryOp extends Expression {

	private static final long serialVersionUID = 2875746406047406844L;

	private String m_leftStr = null;
	
	/**
	 * This class field may be null, within this class's
	 * populated object instance.
	 */
	private String m_rightStr = null;
	
	// This class field is not null at run-time
	private String m_opStr = null;
	
	/**
	 * Class field, with form [..]. This shall always be
	 * non-null for this object instance.
	 */
	private String m_predicateExpr = null;
	
    private Vector m_vars;
    
    private int m_globals_size;

	@Override
	public XObject execute(XPathContext xctxt) throws TransformerException {
		
		XObject result = null;

		SourceLocator srcLocator = xctxt.getSAXLocator();

		final int currentNode = xctxt.getContextNode();

		List<XMLNSDecl> prefixTable = XslTransformEvaluationHelper.getXSLNsPrefixTable(xctxt);

		if (prefixTable != null) {
			m_leftStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(m_leftStr, prefixTable);
			
			if (m_rightStr != null) {
			   m_rightStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(m_rightStr, prefixTable);
			}
		}

		XPath lXPath = new XPath(m_leftStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
		if (m_vars != null) {
			lXPath.fixupVariables(m_vars, m_globals_size);
		}

		XObject lObj = lXPath.execute(xctxt, currentNode, xctxt.getNamespaceContext());
		
		String xPathPredStr = m_predicateExpr.substring(1, m_predicateExpr.length() - 1);
		XPath predXPath = new XPath(xPathPredStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
		if (m_vars != null) {
			predXPath.fixupVariables(m_vars, m_globals_size);
		}
		
		if (lObj instanceof ResultSequence) {
			ResultSequence rSeq =(ResultSequence)lObj;
			XObject predXObj = predXPath.execute(xctxt, currentNode, xctxt.getNamespaceContext());
			String str1 = XslTransformEvaluationHelper.getStrVal(predXObj);
			if ((predXObj instanceof XNumber) || (predXObj instanceof XSNumericType)) {				
				try { 
					int predIndex = Integer.valueOf(str1);
					if ((predIndex >= 1) && (predIndex <= rSeq.size())) {
					   lObj = rSeq.item(predIndex - 1); 
					}
					else {
					   lObj = new ResultSequence();
					}
				}
				catch (NumberFormatException ex) {
					throw new TransformerException("XPDY0050 : An XPath predicate value " + str1 + " is not an integer.", srcLocator);
				}
			}
			else {
				throw new TransformerException("XPDY0050 : An XPath predicate value " + str1 + " is not numeric.", srcLocator);	
			}
		}
		
		if (m_rightStr != null) {
			if ("cast".equals(m_opStr) || "castable".equals(m_opStr) || "instance".equals(m_opStr) 
					                                                                             || "treat".equals(m_opStr)) {
				XPath seqTypeXPath = new XPath(m_rightStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null, true);            
				XObject seqTypeExpressionEvalResult = seqTypeXPath.execute(xctxt, DTM.NULL, xctxt.getNamespaceContext());            
				XPathSequenceTypeData seqExpectedTypeData = (XPathSequenceTypeData)seqTypeExpressionEvalResult;				
				
				if ("cast".equals(m_opStr)) {
					CastAs castAs = new CastAs();
					result = castAs.operate(lObj, seqExpectedTypeData);
				}
				else if ("castable".equals(m_opStr)) {
					CastableAs castableAs = new CastableAs();
					result = castableAs.operate(lObj, seqExpectedTypeData);
				}
				else if ("instance".equals(m_opStr)) {
					InstanceOf instanceOf = new InstanceOf();
					result = instanceOf.operate(lObj, seqExpectedTypeData);
				}
				else if ("treat".equals(m_opStr)) {
					TreatAs treatAs = new TreatAs();
					result = treatAs.operate(lObj, seqExpectedTypeData);
				}
			}
		}
		else {
			result = lObj;
		}


		return result;
	}

	@Override
	public void fixupVariables(Vector vars, int globalsSize) {
		m_vars = (Vector)(vars.clone());
        m_globals_size = globalsSize; 
	}

	@Override
	public boolean deepEquals(Expression expr) {
		return false;
	}

	public String getLeft() {
		return m_leftStr;
	}

	public void setLeft(String left) {
		this.m_leftStr = left;
	}

	public String getRight() {
		return m_rightStr;
	}

	public void setRight(String right) {
		this.m_rightStr = right;
	}

	public String getOpStr() {
		return m_opStr;
	}

	public void setOpStr(String opStr) {
		this.m_opStr = opStr;
	}
	
	public String getPredicateExprStr() {
		return m_predicateExpr;
	}

	public void setPredicateExprStr(String predicateExprStr) {
		this.m_predicateExpr = predicateExprStr;
	}
	
	@Override
	public void callVisitors(ExpressionOwner owner, XPathVisitor visitor) {
		// no op
	}

}
