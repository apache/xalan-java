/*
 * The Apache Software License, Version 1.1
 * 
 * Copyright (c) 2002-2003 The Apache Software Foundation. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 1.
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The end-user documentation
 * included with the redistribution, if any, must include the following
 * acknowledgment: "This product includes software developed by the Apache
 * Software Foundation (http://www.apache.org/)." Alternately, this
 * acknowledgment may appear in the software itself, if and wherever such
 * third-party acknowledgments normally appear. 4. The names "Xalan" and
 * "Apache Software Foundation" must not be used to endorse or promote products
 * derived from this software without prior written permission. For written
 * permission, please contact apache@apache.org. 5. Products derived from this
 * software may not be called "Apache", nor may "Apache" appear in their name,
 * without prior written permission of the Apache Software Foundation.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * APACHE SOFTWARE FOUNDATION OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals
 * on behalf of the Apache Software Foundation and was originally based on
 * software copyright (c) 2002, International Business Machines Corporation.,
 * http://www.ibm.com. For more information on the Apache Software Foundation,
 * please see <http://www.apache.org/> .
 */
package org.apache.xpath.impl;

import org.apache.xpath.XPath20Exception;
import org.apache.xpath.expression.Expr;
import org.apache.xpath.expression.PathExpr;
import org.apache.xpath.expression.Visitor;
import org.apache.xpath.impl.parser.Node;
import org.apache.xpath.impl.parser.SimpleNode;
import org.apache.xpath.impl.parser.Singletons;
import org.apache.xpath.impl.parser.XPath;
import org.apache.xpath.impl.parser.XPathTreeConstants;

/**
 * JavaCC-based {@link PathExpr}implementation.
 * 
 * @author <a href="mailto:villard@us.ibm.com">Lionel Villard</a>
 * @version $Id$
 */
public class PathExprImpl extends OperatorImpl implements PathExpr
{
	/**
	 * True whenver this path is absolute
	 */
	private boolean m_absolute;

	// Constructors

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
	}

	// Methods

	protected void setAbsolute(ExpressionFactoryImpl factory, boolean value)
	{
		if (id != XPathTreeConstants.JJTPATHPATTERN)
		{
			if (value)
			{
				expandAbsolute(factory);
			}
			else if (
				getOperandCount() != 0
					&& ((ExprImpl) getOperand(0)).isRootOnSelfNode())
			{
				try
				{
					removeOperand(getOperand(0));
				}
				catch (XPath20Exception e)
				{
					throw new RuntimeException(e.getMessage());
				}
			}
		}
		m_absolute = value;
	}

	public boolean isRootOnSelfNode()
	{
		return getOperandCount() == 1
			&& ((ExprImpl) getOperand(0)).isRootOnSelfNode();
	}

	protected void expandAbsolute(ExpressionFactoryImpl factory)
	{
		// make sure that the first step is not fn:root(self::node())
		if (getOperandCount() == 0
			|| !((ExprImpl) getOperand(0)).isRootOnSelfNode())
		{
			super.insertOperand(
				0,
				factory.createRootOnSelfNode(
					id == XPathTreeConstants.JJTPATHPATTERN));
		}
	}

	protected boolean isPattern()
	{
		return id == XPathTreeConstants.JJTPATHPATTERN;
	}

	// Implements PathExpr

	public boolean isAbsolute()
	{
		return m_absolute;
	}

	// Implements Expr

	public boolean visit(Visitor visitor)
	{
		if (visitor.visitPath(this))
		{
			// visit each step (operand)
			return super.visit(visitor);
		}

		return false;
	}

	// Methods

	public void getString(StringBuffer expr, boolean abbreviate)
	{
		if (isAbsolute() && abbreviate)
		{
			expr.append('/');
		
			// ignore the first step when not a pattern
			
			int size = getOperandCount();
			ExprImpl op;
			for (int i = isPattern() ? 0:1; i < size; i++)
			{
				op = (ExprImpl) getOperand(i);
				op.getString(expr, abbreviate);
				if (i < (size - 1))
				{
					expr.append('/');
				}
			}
		}
		else
		{
			if (isAbsolute() && abbreviate)
			{
				expr.append('/');
			}
			super.getString(expr, abbreviate);
		}
	}

	// Parser

	public void jjtAddChild(Node n, int i)
	{
		if (n.getId() == XPathTreeConstants.JJTROOT)
		{
			setAbsolute(
				(ExpressionFactoryImpl) SimpleNode.getExpressionFactory(),
				true);
		}
		else if (n.getId() == XPathTreeConstants.JJTROOTDESCENDANTS)
		{
			setAbsolute(
				(ExpressionFactoryImpl) SimpleNode.getExpressionFactory(),
				true);

			if (id == XPathTreeConstants.JJTPATHEXPR)
			{
				super.insertOperand(1, (Expr) Singletons.SLASHSLASH);
			}
			else if (id == XPathTreeConstants.JJTPATHPATTERN)
			{
				super.insertOperand(1, (Expr) Singletons.SLASHSLASH_PATTERN);
			}
			else
			{
				//Assertion error
				throw new RuntimeException("Wrong id on PathExpr: " + id);
			}

		}

		else
		{
			if (n == Singletons.SLASHSLASH
				&& id == XPathTreeConstants.JJTPATHPATTERN)
			{
				n = Singletons.SLASHSLASH_PATTERN;
			}

			int idx = isAbsolute() ? 1 : 0;

			if (((SimpleNode) n).canBeReduced())
			{
				if ((m_exprType == PATH_EXPR)
					&& (n.jjtGetNumChildren() > 0)
					&& (n.jjtGetChild(0).getId()
						== XPathTreeConstants.JJTPATHEXPR))
				{
					super.jjtInsertNodeChildren(n.jjtGetChild(0), idx);
				}
				else
				{
					super.jjtInsertChild(n.jjtGetChild(0), idx);
				}
			}
			else
			{
				super.jjtInsertChild(n, idx);
			}
		}
	}

	/**
	 * Reduce when: - there is only one step - and it's not a pattern
	 */
	public boolean canBeReduced()
	{
		return (id == XPathTreeConstants.JJTPATHEXPR || !isAbsolute())
			&& getOperandCount() == 1;
	}
}
