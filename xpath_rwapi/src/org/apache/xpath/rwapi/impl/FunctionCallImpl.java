/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xalan" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xpath.rwapi.impl;

import org.apache.xpath.rwapi.expression.Expr;
import org.apache.xpath.rwapi.expression.FunctionCall;
import org.apache.xpath.rwapi.impl.parser.Node;
import org.apache.xpath.rwapi.impl.parser.QName;
import org.apache.xpath.rwapi.impl.parser.SimpleNode;
import org.apache.xpath.rwapi.impl.parser.XPath;
import org.apache.xpath.rwapi.impl.parser.XPathTreeConstants;

/**
 *
 */
public class FunctionCallImpl extends OperatorImpl implements FunctionCall {

	String m_localPart;
	String m_prefix;

	/**
	 * Constructor for FunctionCallImpl.
	 * @param i
	 */
	public FunctionCallImpl(int i) {
		super(i);
	}

	/**
	 * Constructor for FunctionCallImpl.
	 * @param p
	 * @param i
	 */
	public FunctionCallImpl(XPath p, int i) {
		super(p, i);
	}

	/**
	 * @see org.apache.xpath.rwapi.expression.FunctionCall#getFunctionName()
	 */
	public String getFunctionName() {
		return m_localPart;
	}

	/**
	 * @see org.apache.xpath.rwapi.expression.FunctionCall#getFunctionPrefix()
	 */
	public String getFunctionPrefix() {
		return m_prefix;
	}

	/**
	 * @see org.apache.xpath.rwapi.impl.parser.Node#jjtAddChild(Node, int)
	 */
	final public void jjtAddChild(Node n, int i) {
		
		if (n.getId() == XPathTreeConstants.JJTQNAMELPAR) {
				m_localPart = ((QName) n).getLocalPart();
				m_prefix = ((QName) n).getPrefix();
			} else {
				// parameter
				// -1 because of QNAMELPAR 
				if (((SimpleNode) n).canBeReduced()) {
					super.jjtAddChild(n.jjtGetChild(0), i - 1);
				} else {
					super.jjtAddChild(n, i - 1);
				}
			}

	}
	/**
	 * @see org.apache.xpath.rwapi.impl.ExprImpl#getString(StringBuffer, boolean)
	 */
	protected void getString(StringBuffer expr, boolean abbreviate) {
		if (m_prefix != null) {
			expr.append(m_prefix).append(":").append(m_localPart);
		} else {
			expr.append(m_localPart);
		}
		expr.append('(');
		int size = getOperandCount();
		for (int i = 0; i < size; i++) {
			((ExprImpl) getOperand(i)).getString(expr, abbreviate);
			if (i < size - 1) {
				expr.append(',');
			}
		}
		expr.append(')');

	}

	/**
	 * @see org.apache.xpath.rwapi.expression.Expr#getExprType()
	 */
	public short getExprType() {
		return FUNCTION_CALL_EXPR;
	}

	/**
	 * @see org.apache.xpath.rwapi.expression.OperatorExpr#getOperand(int)
	 */
	public Expr getOperand(int i) {
		return (Expr) children[i];
	}

	/**
	 * @see org.apache.xpath.rwapi.expression.OperatorExpr#getOperandCount()
	 */
	public int getOperandCount() {
		return (children == null) ? 0 : children.length;
	}

	/**
	 * @see org.apache.xpath.rwapi.impl.OperatorImpl#getOperatorChar()
	 */
	protected String getOperatorChar() {
		return ",";
	}

	/**
	 * @see org.apache.xpath.rwapi.expression.OperatorExpr#getOperatorType()
	 */
	public short getOperatorType() {
		return COMMA;
	}

}
