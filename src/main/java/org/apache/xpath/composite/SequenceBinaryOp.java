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
import org.apache.xpath.operations.Div;
import org.apache.xpath.operations.IDiv;
import org.apache.xpath.operations.Minus;
import org.apache.xpath.operations.Mod;
import org.apache.xpath.operations.Mult;
import org.apache.xpath.operations.Plus;
import org.apache.xpath.operations.VcEquals;
import org.apache.xpath.operations.VcGe;
import org.apache.xpath.operations.VcGt;
import org.apache.xpath.operations.VcLe;
import org.apache.xpath.operations.VcLt;
import org.apache.xpath.operations.VcNotEquals;

/**
 * A class definition, to implement XPath 3.1 binary operator
 * expressions like (if ...) + (if ...) , 2 + (if ...)
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class SequenceBinaryOp extends Expression {

	private static final long serialVersionUID = -4920578903670425812L;
	
    private String m_leftStr = null;
	
	private String m_rightStr = null;
	
	private String m_opStr = null;
	
    private Vector m_vars;
    
    private int m_globals_size;

	@Override
	public void callVisitors(ExpressionOwner owner, XPathVisitor visitor) {
		// no op
	}

	@Override
	public XObject execute(XPathContext xctxt) throws TransformerException {
		
		XObject result = null;

		SourceLocator srcLocator = xctxt.getSAXLocator();

		final int currentNode = xctxt.getContextNode();

		List<XMLNSDecl> prefixTable = XslTransformEvaluationHelper.getXSLNsPrefixTable(xctxt);

		if (prefixTable != null) {
			m_leftStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(m_leftStr, prefixTable);
			
			m_rightStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(m_rightStr, prefixTable);
		}

		XPath lXPath = new XPath(m_leftStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
		if (m_vars != null) {
			lXPath.fixupVariables(m_vars, m_globals_size);
		}

		XObject lObj = lXPath.execute(xctxt, currentNode, xctxt.getNamespaceContext());
		
		XPath rXPath = new XPath(m_rightStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
		if (m_vars != null) {
			rXPath.fixupVariables(m_vars, m_globals_size);
		}

		XObject rObj = rXPath.execute(xctxt, currentNode, xctxt.getNamespaceContext());

		if ("+".equals(m_opStr)) {
		   Plus plus = new Plus();
		   
		   result = plus.operate(lObj, rObj);
		}
		else if ("-".equals(m_opStr)) {
		   Minus minus = new Minus();
			   
		   result = minus.operate(lObj, rObj);
		}
		else if ("*".equals(m_opStr)) {
			Mult mult = new Mult();

			result = mult.operate(lObj, rObj);
		}
		else if ("idiv".equals(m_opStr)) {
			IDiv idiv = new IDiv();

			result = idiv.operate(lObj, rObj);
		}		
		else if ("div".equals(m_opStr)) {
			Div div = new Div();

			result = div.operate(lObj, rObj);
		}
		else if ("mod".equals(m_opStr)) {
			Mod mod = new Mod();

			result = mod.operate(lObj, rObj);
		}		
		else if ("eq".equals(m_opStr)) {
			VcEquals vcEquals = new VcEquals();

			result = vcEquals.operate(lObj, rObj);
		}
		else if ("ne".equals(m_opStr)) {
			VcNotEquals vcNotEquals = new VcNotEquals();

			result = vcNotEquals.operate(lObj, rObj);
		}
		else if ("lt".equals(m_opStr)) {
			VcLt vcLt = new VcLt();

			result = vcLt.operate(lObj, rObj);
		}
		else if ("gt".equals(m_opStr)) {
			VcGt vcGt = new VcGt();

			result = vcGt.operate(lObj, rObj);
		}
		else if ("le".equals(m_opStr)) {
			VcLe vcLe = new VcLe();

			result = vcLe.operate(lObj, rObj);
		}
		else if ("ge".equals(m_opStr)) {
			VcGe vcGe = new VcGe();

			result = vcGe.operate(lObj, rObj);
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

}
