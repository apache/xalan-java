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

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformData;
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
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.types.XslForEachGroupCompositeGroupingKey;

import xml.xpath31.processor.types.XSInteger;
import xml.xpath31.processor.types.XSNumericType;

/**
 * Class definition, representing an XPath 3.1 compiled expression
 * for, XPath expression that is an XPath function argument of type
 * funcCall(...)[..].
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 */
public class XPathFuncArgFuncCallWithPredicate extends Expression {

	private static final long serialVersionUID = 2034075289598534654L;
	
	/**
	 * An XPath expression string, representing an XPath function call.
	 */
	private String m_func_call_str = null;
	
	/**
	 * An XPath expression string, representing an XPath predicate 
	 * suffix after a function call.
	 */
	private String m_predicate_expr_str = null;
	
	private Vector m_vars;
	
	private int m_globals_size;
	
	private static final String CURRENT_GROUP = "current-group";
	
	private static final String CURRENT_GROUPING_KEY = "current-grouping-key";
	

	@Override
	public XObject execute(XPathContext xctxt) throws TransformerException {
		
		XObject result = null;
		
		SourceLocator srcLocator = null;
		
		final int sourceNode = xctxt.getCurrentNode(); 
		
		List<XMLNSDecl> prefixTable = XslTransformEvaluationHelper.getXSLNsPrefixTable(xctxt);

		if (prefixTable != null) {
			m_func_call_str = XslTransformEvaluationHelper.replaceXmlNsUrisWithPrefixesXPathStr(m_func_call_str, prefixTable);
		}
		
		XObject xObj1 = null;
		
		String funcCallStr1 = m_func_call_str.replaceAll("\\s*", "");
		
		if (!((CURRENT_GROUP + "()").equals(funcCallStr1) || (CURRENT_GROUPING_KEY + "()").equals(funcCallStr1))) {
			XPath funcCallStrXPath = new XPath(m_func_call_str, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);

			if (m_vars != null) {
				funcCallStrXPath.fixupVariables(m_vars, m_globals_size);
			}

			xObj1 = funcCallStrXPath.execute(xctxt, sourceNode, xctxt.getNamespaceContext());
		}
		else if ((CURRENT_GROUP + "()").equals(funcCallStr1)) {
			StylesheetRoot stylesheetRoot = XslTransformData.m_stylesheetRoot;
			TransformerImpl transformerImpl = stylesheetRoot.getTransformerImpl();
			
			ElemTemplateElement elemTemplateElement = transformerImpl.getCurrentElement();
			
			ResultSequence rSeq = elemTemplateElement.getCurrentGroup();
			List<Integer> nodeHandleList = elemTemplateElement.getGroupNodesDtmHandles();
			
			while ((rSeq == null) && (nodeHandleList == null)) {
			   elemTemplateElement = elemTemplateElement.getParentElem();
			   rSeq = elemTemplateElement.getCurrentGroup();
			   nodeHandleList = elemTemplateElement.getGroupNodesDtmHandles();
			}
			
			if (rSeq != null) {
			   nodeHandleList = new ArrayList<Integer>();
			   int size1 = rSeq.size();
			   
			   for (int idx = 0; idx < size1; idx++) {
				  XSInteger xsInteger = (XSInteger)rSeq.item(idx);
				  nodeHandleList.add(Integer.valueOf(xsInteger.stringValue())); 
			   }
			}
			
			if (nodeHandleList != null) {
			   xObj1 = new XMLNodeCursorImpl(nodeHandleList, xctxt);
			}
			else {
			   xObj1 = new ResultSequence();
			}
		}
		else if ((CURRENT_GROUPING_KEY + "()").equals(funcCallStr1)) {
			StylesheetRoot stylesheetRoot = XslTransformData.m_stylesheetRoot;
			TransformerImpl transformerImpl = stylesheetRoot.getTransformerImpl();
			
			ElemTemplateElement elemTemplateElement = transformerImpl.getCurrentElement();
			
			Object currentGrpKey1 = elemTemplateElement.getGroupingKey();
			
			while (currentGrpKey1 == null) {
			   elemTemplateElement = elemTemplateElement.getParentElem();
			   currentGrpKey1 = elemTemplateElement.getGroupingKey();
			}
			
			if (currentGrpKey1 != null) {
				if (!(currentGrpKey1 instanceof XslForEachGroupCompositeGroupingKey)) {					
					xObj1 = (XObject)currentGrpKey1;
				}
				else {
					XslForEachGroupCompositeGroupingKey groupingKeyObj = (XslForEachGroupCompositeGroupingKey)currentGrpKey1;
					xObj1 = groupingKeyObj.getValue();
				}
			}			
		}
		
		if (prefixTable != null) {
			m_predicate_expr_str = XslTransformEvaluationHelper.replaceXmlNsUrisWithPrefixesXPathStr(m_predicate_expr_str, prefixTable);
		}
		
        XPath predicateXPath = new XPath(m_predicate_expr_str, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
		
		if (m_vars != null) {
		   predicateXPath.fixupVariables(m_vars, m_globals_size);
		}
		
		XObject xObj2 = null;
		
		if (xObj1 instanceof ResultSequence) {			
		   ResultSequence rSeq = (ResultSequence)xObj1;
		   
		   XObject prevXPathCtxtItem = xctxt.getXPath3ContextItem();
		   int prevXPathCtxtPos = xctxt.getXPath3ContextPosition();
		   int prevXPathCtxtSize = xctxt.getXPath3ContextSize();
		  
		   xctxt.setXPath3ContextSize(rSeq.size());
		   
		   try {
		      xObj2 = predicateXPath.execute(xctxt, sourceNode, xctxt.getNamespaceContext());
		   }
		   finally {
			  xctxt.setXPath3ContextItem(prevXPathCtxtItem);
			  xctxt.setXPath3ContextPosition(prevXPathCtxtPos);
			  xctxt.setXPath3ContextSize(prevXPathCtxtSize); 
		   }
		}
		else if (xObj1 instanceof XMLNodeCursorImpl) {
		   xObj2 = predicateXPath.execute(xctxt, sourceNode, xctxt.getNamespaceContext());
		}
		
		int idx2 = Integer.MAX_VALUE;
		
		if (xObj2 instanceof XNumber) {
			xObj2 = XslTransformEvaluationHelper.getXNumberNormalizedValue((XNumber)xObj2);  
		}

		String str1 = null;

		if (xObj2 instanceof XSNumericType) {
			XSNumericType xsNumericType = (XSNumericType)xObj2;
			str1 = xsNumericType.stringValue(); 
		}
		else if (xObj2 instanceof XNumber) {
			double num1 = ((XNumber)xObj2).num();
			str1 = (num1 + "");
		}

		if (str1 != null) {		   
			try {
				idx2 = Integer.valueOf(str1);
			}
			catch (NumberFormatException ex) {
				// No op 
			}
		}
		
		if ((xObj1 instanceof ResultSequence) && (idx2 != Integer.MAX_VALUE)) {
		   ResultSequence rSeq = (ResultSequence)xObj1;
		   int size1 = rSeq.size();
		   
		   if ((idx2 >= 1) && (idx2 <= size1)) {
			  result = rSeq.item(idx2 - 1);  
		   }
		   else {
			  result = new ResultSequence(); 
		   }
		   
		   return result;
		}
		
		if ((xObj1 instanceof XMLNodeCursorImpl) && (idx2 != Integer.MAX_VALUE)) {
			XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xObj1;
			
			DTMCursorIterator dtmCursorIter = xmlNodeCursorImpl.iter();
			int nextNode = DTM.NULL;
			
			int count = 0;
			
			while ((nextNode = dtmCursorIter.nextNode()) != DTM.NULL) {
				count++;
				
				if (count == idx2) {
				   XMLNodeCursorImpl xmlNodeCursorImpl2 = new XMLNodeCursorImpl(nextNode, xctxt);
				   
				   return xmlNodeCursorImpl2; 
				}
			}
			
			result = new ResultSequence();

			return result;
		}
		
		if (xObj2.bool()) {
		   result = xObj1; 
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

	public String getFuncCallStr() {
		return m_func_call_str;
	}

	public void setFuncCallStr(String funcCallStr) {
		this.m_func_call_str = funcCallStr;
	}

	public String getPredicateExprStr() {
		return m_predicate_expr_str;
	}

	public void setPredicateExprStr(String predicateExprStr) {
		this.m_predicate_expr_str = predicateExprStr;
	}

}
