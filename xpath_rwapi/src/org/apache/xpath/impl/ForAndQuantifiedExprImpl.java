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

import java.util.ArrayList;
import java.util.List;

import org.apache.xpath.XPath20Exception;
import org.apache.xpath.expression.Expr;
import org.apache.xpath.expression.ForAndQuantifiedExpr;
import org.apache.xpath.expression.Variable;
import org.apache.xpath.expression.Visitor;
import org.apache.xpath.impl.parser.Node;
import org.apache.xpath.impl.parser.SimpleNode;
import org.apache.xpath.impl.parser.XPath;
import org.apache.xpath.impl.parser.XPathTreeConstants;

/**
 * JavaCC-based {@link ForAndQuantifiedExpr} implementation.
 * @author <a href="mailto:villard@us.ibm.com>Lionel Villard</a>
 * @version $Id$
 */
public class ForAndQuantifiedExprImpl
	extends ExprImpl
	implements ForAndQuantifiedExpr
{

	/**
	 * List<ExprImpl> of clause expressions
	 */
	protected List m_exprs;

	/**
	 * List<VariableImpl> of clause variables.
	 */
	protected List m_variables;

	/**
	 * The return/satifies expression
	 */
	protected ExprImpl m_resExpr;

	// Constructors

	/**
	 * Constructor for ForAndQuantifiedExprImpl.
	 * @param i
	 */
	public ForAndQuantifiedExprImpl(int i)
	{
		super(i);

		m_variables = new ArrayList(1);
		m_exprs = new ArrayList(1);
	}

	/**
	 * Constructor for ForAndQuantifiedExprImpl.
	 * @param p
	 * @param i
	 */
	public ForAndQuantifiedExprImpl(XPath p, int i)
	{
		super(p, i);
	}

	// Methods

	protected String getKeyword1()
	{
		switch (getExprType())
		{
			case ITERATION_EXPR :
				return "for";

			case EVERY_EXPR :
				return "every";

			case SOME_EXPR :
				return "some";

			default :
				throw new RuntimeException(
					"Invalid expression type: " + getExprType());
		}
	}

	protected String getKeyword2()
	{
		switch (getExprType())
		{
			case ITERATION_EXPR :
				return "return";

			case EVERY_EXPR :
			case SOME_EXPR :
				return "satisfies";

			default :
				throw new RuntimeException(
					"Invalid expression type: " + getExprType());
		}
	}

	protected String getSeparator()
	{
		return "in";
	}

	public void getString(StringBuffer expr, boolean abbreviate)
	{
		boolean pred = lowerPrecedence();
		if (pred)
		{
			expr.append('(');
		}

		expr.append(getKeyword1()).append(' ');

		for (int i = m_variables.size() - 1; i >= 0; i--)
		{
			((SimpleNode) m_variables.get(i)).getString(expr, abbreviate);
			expr.append(' ').append("in").append(' ');
			((SimpleNode) m_exprs.get(i)).getString(expr, abbreviate);

			if (i != 0)
			{
				expr.append(", ");
			}
		}

		expr.append(' ').append(getKeyword2()).append(' ');
		m_resExpr.getString(expr, abbreviate);

		if (pred)
		{
			expr.append(')');
		}
	}

	protected short getOperatorPrecedence()
	{
		return 2;
	}

	// Implements ForAndQuantifiedExpr

	public Expr getExpr(int i) throws XPath20Exception
	{
		return (Expr) m_exprs.get(i);
	}

	public Variable getVariable(int i) throws XPath20Exception
	{
		return (Variable) m_variables.get(i);
	}

	public int getClauseCount()
	{
		return (m_variables == null) ? 0 : m_variables.size();
	}

	public Expr getResultingExpr()
	{
		return m_resExpr;
	}

	public Expr setResultingExpr(Expr resultExpr)
	{
		resultExpr = selfOrclone(resultExpr);
		Expr old = m_resExpr;

		m_resExpr = (ExprImpl) resultExpr;
		return old;
	}

	public void addClause(Variable variable, Expr expr)
	{
		//assert variable instanceof VariableImpl;
		//assert expr instanceof ExprImpl;

		m_variables.add(variable);
		m_exprs.add(expr);
	}

	public void insertClause(int i, Variable variable, Expr expr)
		throws XPath20Exception
	{
		if (i > m_variables.size())
		{
			throw new XPath20Exception("Index out of bounds");
		}

		m_variables.add(i, variable);
		m_exprs.add(i, expr);
	}

	public void removeClause(int i) throws XPath20Exception
	{
		try
		{
			m_variables.remove(i);
			m_exprs.remove(i);
		}
		catch (IndexOutOfBoundsException e)
		{
			throw new XPath20Exception(e);
		}
	}

	// Implements Expr

	public short getExprType()
	{
		switch (id)
		{
			case XPathTreeConstants.JJTFOREXPR :
				return ITERATION_EXPR;

			case XPathTreeConstants.JJTEVERY :
				return EVERY_EXPR;

			case XPathTreeConstants.JJTSOME :
				return SOME_EXPR;
			default :
				throw new RuntimeException("Invalid expression type: " + id);
		}
	}

	public boolean visit(Visitor visitor)
	{
		// TODO:
		return true;
	}

	// Parser

	final public void jjtAddChild(Node n, int i)
	{
		switch (n.getId())
		{
			case XPathTreeConstants.JJTRETURN :
			case XPathTreeConstants.JJTIN :
				// Nothing
				break;
			case XPathTreeConstants.JJTSOME :
			case XPathTreeConstants.JJTEVERY :
				id = n.getId();
				break;
			default :
				switch (SimpleNode.getFQState())
				{
					case SimpleNode.FQ_EXPECT_RANGE_VAR :
						m_variables.add(reducedNode((SimpleNode) n));
						SimpleNode.setFQState(FQ_EXPECT_IN_SEQ);
						break;
					case SimpleNode.FQ_EXPECT_IN_SEQ :
						m_exprs.add(reducedNode((SimpleNode) n));
						SimpleNode.setFQState(FQ_EXPECT_RANGE_VAR);
						break;
					case SimpleNode.FQ_EXPECT_RESULT :
						m_resExpr = (ExprImpl) reducedNode((SimpleNode) n);
						SimpleNode.setFQState(FQ_EXPECT_IN_SEQ);
						break;

				}
		}
	}

	protected int initialChildNumber()
	{
		return 3;
	}

}
