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
 * originally based on software copyright (c) 2002, International
 * Business Machines Corporation., http://www.ibm.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xpath.impl;

import org.apache.xpath.expression.Expr;
import org.apache.xpath.expression.PathExpr;
import org.apache.xpath.expression.StepExpr;
import org.apache.xpath.expression.Visitor;
import org.apache.xpath.impl.parser.Node;
import org.apache.xpath.impl.parser.SimpleNode;
import org.apache.xpath.impl.parser.Singletons;
import org.apache.xpath.impl.parser.XPath;
import org.apache.xpath.impl.parser.XPathTreeConstants;

/**
 *
 */
public class PathExprImpl extends OperatorImpl implements PathExpr
{
	boolean m_isAbsolute;

	/**
	 * Constructor for PathExprImpl.
	 */
	protected PathExprImpl()
	{
		this(XPathTreeConstants.JJTPATHEXPR);
	}

	/**
	 * Constructor for PathExprImpl.
	 *
	 * @param i
	 */
	public PathExprImpl(int i)
	{
		id = i;
		m_exprType = PATH_EXPR;
		m_opType = SLASH_STEP;
		m_isAbsolute = false;
	}

	/**
	 * Constructor for PathExprImpl.
	 *
	 * @param p
	 * @param i
	 */
	public PathExprImpl(XPath p, int i)
	{
		super(p, i);

		m_isAbsolute = false;
	}

	/**
	 * @param expr
	 */
	protected PathExprImpl(PathExprImpl expr)
	{
		super(expr);
		m_isAbsolute = expr.m_isAbsolute;
	}

	/**
	 * @see org.apache.xpath.expression.PathExpr#isAbsolute()
	 */
	public boolean isAbsolute()
	{
		return m_isAbsolute;
	}

	/**
	 * @see org.apache.xpath.expression.Visitable#visit(Visitor)
	 */
	public boolean visit(Visitor visitor)
	{
		if (visitor.visitPath(this))
		{
			// visit each step (operand)
			return super.visit(visitor);
		}

		return false;
	}

	/**
	 * @see org.apache.xpath.impl.ExprImpl#getString(StringBuffer, boolean)
	 */
	public void getString(StringBuffer expr, boolean abbreviate)
	{
		if (m_isAbsolute)
		{
			expr.append("/");
		}

		super.getString(expr, abbreviate);
	}

	
	public Expr cloneExpression()
	{

		return new PathExprImpl(this);
	}

	/**
	 * @see org.apache.xpath.impl.parser.Node#jjtAddChild(Node, int)
	 */
	public void jjtAddChild(Node n, int i)
	{
		if (n.getId() == XPathTreeConstants.JJTROOT)
		{
			m_isAbsolute = true;
		}
		else if (n.getId() == XPathTreeConstants.JJTROOTDESCENDANTS)
		{
			m_isAbsolute = true;
			super.jjtAddChild(Singletons.SLASHSLASH, i);
		}
		else
		{
			if (((SimpleNode) n).canBeReduced())
			{
				if ((m_exprType == PATH_EXPR)
					&& (n.jjtGetNumChildren() > 0)
					&& (n.jjtGetChild(0).getId()
						== XPathTreeConstants.JJTPATHEXPR))
				{
					super.jjtInsertNodeChildren(n.jjtGetChild(0));
				}
				else
				{
					super.jjtInsertChild(n.jjtGetChild(0));
				}
			}
			else
			{
				super.jjtInsertChild(n);
			}
		}
	}

	/**
	 * @see org.apache.xpath.impl.parser.SimpleNode#canBeReduced()
	 */
	public boolean canBeReduced()
	{
		// Can be reduced whenever there is only one step and this step is a primary expression
		if ((m_children != null) && (m_children.length == 1))
		{
			Expr step = (Expr) m_children[0];
			int et = step.getExprType();

			return (
				((et == STEP) && ((StepExpr) step).isPrimaryExpr())
					|| (et == LITERAL_EXPR)
					|| (et == FUNCTION_CALL_EXPR)
					|| (et == SEQUENCE_EXPR)
					|| (et == VARIABLE_REF_EXPR)
					|| (et == ARITHMETIC_EXPR));
		}

		return false;
	}
}
