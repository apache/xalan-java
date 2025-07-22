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
import org.apache.xpath.compiler.OpCodes;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.operations.Equals;
import org.apache.xpath.operations.Lt;
import org.apache.xpath.operations.NotEquals;
import org.apache.xpath.operations.VcGt;

/**
 * This class definition, provides implementation of general 
 * comparison operators for XPath array as LHS operand and 
 * another operand on RHS.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPathArrayComparison extends Expression {

	private static final long serialVersionUID = -5811334732284992044L;

	private List<String> m_arrayConstructorXPathLhs = new ArrayList<String>();
	
	private List<String> m_seqArrConstructorXPathRhs = new ArrayList<String>();
	
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
        
        ResultSequence resultSeqLhs = new ResultSequence(); 
        for (int idx = 0; idx < m_arrayConstructorXPathLhs.size(); idx++) {
        	String xpathExprStr = m_arrayConstructorXPathLhs.get(idx);        	
        	if (prefixTable != null) {
        	   xpathExprStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(xpathExprStr, prefixTable);
    		}
        	
        	XPath xpathObj = new XPath(xpathExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
			if (m_vars != null) {
			   xpathObj.fixupVariables(m_vars, m_globals_size);
			}
			
			XObject xObj = xpathObj.execute(xctxt, contextNode, xctxt.getNamespaceContext());
			resultSeqLhs.add(xObj);
        }
        
        ResultSequence resultSeqRhs = new ResultSequence();
        for (int idx = 0; idx < m_seqArrConstructorXPathRhs.size(); idx++) {
        	String xpathExprStr = m_seqArrConstructorXPathRhs.get(idx);        	
        	if (prefixTable != null) {
        	   xpathExprStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(xpathExprStr, prefixTable);
    		}
        	
        	XPath xpathObj = new XPath(xpathExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
			if (m_vars != null) {
			   xpathObj.fixupVariables(m_vars, m_globals_size);
			}
			
			XObject xObj = xpathObj.execute(xctxt, contextNode, xctxt.getNamespaceContext());
			resultSeqRhs.add(xObj);
        }
        
        if (m_comparisonOpCode == OpCodes.OP_EQUALS) {
            Equals equals = new Equals();            
            result = equals.operate(resultSeqLhs, resultSeqRhs);
        }        
        else if (m_comparisonOpCode == OpCodes.OP_NOTEQUALS) {
        	NotEquals notEquals = new NotEquals();                                    
            result = notEquals.operate(resultSeqLhs, resultSeqRhs);
        }
        else if (m_comparisonOpCode == OpCodes.OP_LT) {
        	Lt lessThan = new Lt();                                    
            result = lessThan.operate(resultSeqLhs, resultSeqRhs);
        }
        else if (m_comparisonOpCode == OpCodes.OP_LTE) {
        	Lt lessThan = new Lt();
        	Equals equals = new Equals();
            XObject result1 = lessThan.operate(resultSeqLhs, resultSeqRhs);
            XObject result2 = result = equals.operate(resultSeqLhs, resultSeqRhs);
            boolean boolResult = (result1.bool() || result2.bool());
            result = (boolResult ? XBoolean.S_TRUE : XBoolean.S_FALSE);
        }
        else if (m_comparisonOpCode == OpCodes.OP_GTE) {
        	VcGt vcGt = new VcGt();
        	Equals equals = new Equals();
        	
        	boolean isGt = false;
        	for (int i = 0; i < resultSeqLhs.size(); i++) {
        		XObject xObj1 = resultSeqLhs.item(i); 
        		for (int j = 0; j < resultSeqRhs.size(); j++) {
        			XObject xObj2 = resultSeqRhs.item(j);
        			XObject xObj1Bool = vcGt.operate(xObj1, xObj2);
        			if (xObj1Bool.bool()) {
        				isGt = true;
        				break;
        			}
        		}

        		if (isGt) {
        			break; 
        		}
        	}
        	
        	XObject eqResult = equals.operate(resultSeqLhs, resultSeqRhs);
        	boolean boolResult = (isGt || eqResult.bool());
        	
        	result = (boolResult ? XBoolean.S_TRUE : XBoolean.S_FALSE);
        }
        else if (m_comparisonOpCode == OpCodes.OP_GT) {
        	VcGt vcGt = new VcGt();
        	
        	boolean isGt = false;
        	for (int i = 0; i < resultSeqLhs.size(); i++) {
        		XObject xObj1 = resultSeqLhs.item(i); 
        		for (int j = 0; j < resultSeqRhs.size(); j++) {
        			XObject xObj2 = resultSeqRhs.item(j);
        			XObject xObj1Bool = vcGt.operate(xObj1, xObj2);
        			if (xObj1Bool.bool()) {
        				isGt = true;
        				break;
        			}
        		}

        		if (isGt) {
        			break; 
        		}
        	}
        	
        	result = (isGt ? XBoolean.S_TRUE : XBoolean.S_FALSE);
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
        // no op
    }

    @Override
    public boolean deepEquals(Expression expr) {
        return false;
    }

	public List<String> getArrayConstructorXPathLhs() {
		return m_arrayConstructorXPathLhs;
	}

	public void setArrayConstructorXPathLhs(List<String> arrayConstructorXPathLhs) {
		this.m_arrayConstructorXPathLhs = arrayConstructorXPathLhs;
	}

	public List<String> getSeqArrConstructorXPathRhs() {
		return m_seqArrConstructorXPathRhs;
	}

	public void setSeqArrConstructorXPathRhs(List<String> seqArrConstructorXPathRhs) {
		this.m_seqArrConstructorXPathRhs = seqArrConstructorXPathRhs;
	}

	public int getComparisonOpCode() {
		return m_comparisonOpCode;
	}

	public void setComparisonOpCode(int comparisonOpCode) {
		this.m_comparisonOpCode = comparisonOpCode;
	}

}
