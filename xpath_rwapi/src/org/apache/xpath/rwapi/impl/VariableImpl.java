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
import org.apache.xpath.rwapi.expression.Variable;
import org.apache.xpath.rwapi.expression.Visitor;
import org.apache.xpath.rwapi.impl.parser.Token;
import org.apache.xpath.rwapi.impl.parser.XPath;

/**
 *
 */
public class VariableImpl extends ExprImpl implements Variable {

    String m_varName;    

	/**
	 * Constructor for VariableImpl.
	 * @param i
	 */
	public VariableImpl(int i) {
		super(i);
	}

	/**
	 * Constructor for VariableImpl.
	 * @param p
	 * @param i
	 */
	public VariableImpl(XPath p, int i) {
		super(p, i);
	}

	/**
	 * @see org.apache.xpath.rwapi.expression.Variable#getVariableName()
	 */
	public String getVariableName() {
		return null;
	}

	/**
	 * @see org.apache.xpath.rwapi.expression.Expr#getExprType()
	 */
	public short getExprType() {
		return VARIABLE_REF_EXPR;
	}

	/**
	 * @see org.apache.xpath.rwapi.expression.Expr#cloneExpression()
	 */
	public Expr cloneExpression() {
		return null;
	}

	/**
	 * @see org.apache.xpath.rwapi.expression.Visitable#visited(Visitor)
	 */
	public void visited(Visitor visitor) {
	}

	/**
	 * @see org.apache.xpath.rwapi.impl.parser.SimpleNode#processToken(Token)
	 */
	public void processToken(Token token) {
		super.processToken(token);
        m_varName = token.image.trim();
	}

	/**
	 * @see org.apache.xpath.rwapi.impl.ExprImpl#getString(StringBuffer, boolean)
	 */
	protected void getString(StringBuffer expr, boolean abbreviate) {
		expr.append('$').append(m_varName);
	}

}
