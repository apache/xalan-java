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

import org.apache.xpath.datamodel.SingleType;
import org.apache.xpath.expression.CastableOrCastExpr;
import org.apache.xpath.expression.Expr;
import org.apache.xpath.impl.parser.Node;
import org.apache.xpath.impl.parser.SimpleNode;
import org.apache.xpath.impl.parser.XPath;
import org.apache.xpath.impl.parser.XPathTreeConstants;

/**
 * JavaCC-based {@link org.apache.xpath.expression.CastableOrCastExpr} implementation.
 * @author <a href="mailto:villard@us.ibm.com>Lionel Villard</a>
 * @version $Id$
 */
public class CastableOrCastExprImpl
	extends ExprImpl
	implements CastableOrCastExpr
{
	// Constructors

	/**
	 * Constructor for CastExprImpl.
	 */
	public CastableOrCastExprImpl(int i)
	{
		super(i);
	}

	/**
	 * Constructor for CastExprImpl.
	 */
	public CastableOrCastExprImpl(XPath p, int i)
	{
		super(p, i);
	}

	/**
	 * Constructor for factory
	 */
	protected CastableOrCastExprImpl(
		Expr expr,
		SingleType type,
		boolean isCast)
	{
		super(
			(isCast)
				? XPathTreeConstants.JJTCASTEXPR
				: XPathTreeConstants.JJTCASTABLEEXPR);

		jjtAddChild((SimpleNode) type, 0);
		jjtAddChild((SimpleNode) expr, 1);
	}

	// Methods

	public void getString(StringBuffer expr, boolean abbreviate)
	{
		boolean pred = lowerPrecedence();
		if (pred)
		{
			expr.append('(');
		}

		((SimpleNode) getExpr()).getString(expr, abbreviate);
		expr.append(
			(getExprType() == CAST_AS_EXPR) ? " cast as " : " castable as ");
		((SimpleNode) getSingleType()).getString(expr, abbreviate);

		if (pred)
		{
			expr.append(')');
		}
	}

	protected short getOperatorPrecedence()
	{
		return (short) (getExprType() == CAST_AS_EXPR ? 7 : 6);
	}

	protected int initialChildNumber()
	{
		return 2;
	}

	// Implements Expr

	public short getExprType()
	{
		return (id == XPathTreeConstants.JJTCASTEXPR)
			? CAST_AS_EXPR
			: CASTABLE_EXPR;
	}

	// Implements CastableOrCastExpr

	public Expr getExpr()
	{
		return (Expr) jjtGetChild(0);
	}

	public SingleType getSingleType()
	{
		return (SingleType) jjtGetChild(1);
	}
	
	public Expr replaceExpr(Expr expr)
	{
		expr = selfOrclone(expr);		
		super.jjtAddChild((Node) expr, 0);		
		return expr;
	}

	public SingleType replaceSingleType(SingleType type)
	{
		type = (SingleType) selfOrclone(type);		
		super.jjtAddChild((Node) type, 1);		
		return type;
	}

	// Parser

	public void jjtAddChild(Node n, int i)
	{
		if (((SimpleNode) n).canBeReduced())
		{
			super.jjtAddChild(n.jjtGetChild(0), i);
		}
		else
		{
			super.jjtAddChild(n, i);
		}
	}

}
