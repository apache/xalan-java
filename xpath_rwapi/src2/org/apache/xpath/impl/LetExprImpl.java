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

import org.apache.xml.QName;
import org.apache.xpath.XPath20Exception;
import org.apache.xpath.datamodel.SequenceType;
import org.apache.xpath.expression.Expr;
import org.apache.xpath.expression.LetExpr;

/**
 * Basic implementation of {@link LetExpr}.
 * 
 * @author <a href="mailto:villard@us.ibm.com">Lionel Villard</a>
 * @version $Id$
 */
public class LetExprImpl extends ExprImpl implements LetExpr
{
	List m_varNameList;
	List m_typeList;
	List m_exprList;

	// Constructors

	protected LetExprImpl()
	{
		m_varNameList = new ArrayList(1);
		m_typeList = new ArrayList(1);
		m_exprList = new ArrayList(1);
	}

	/**
	 * Constructor for factory
	 */
	protected LetExprImpl(QName varName, SequenceType type, Expr expr)
	{
		this();

		appendClause(varName, type, expr);
	}

	// Implements LetExpr

	public int getClauseCount()
	{
		return m_varNameList.size();
	}

	public QName getVariableName(int i)
	{
		return (QName) m_varNameList.get(i);
	}

	public SequenceType getType(int i)
	{
		return (SequenceType) m_typeList.get(i);
	}

	public Expr getExpr(int i)
	{
		return (Expr) m_exprList.get(i);
	}

	public void appendClause(QName varName, SequenceType type, Expr expr)
	{
		m_varNameList.add(varName);
		m_typeList.add(type);
		m_exprList.add(expr);
	}

	public void insertClause(
		int i,
		QName varName,
		SequenceType type,
		Expr expr)
		throws XPath20Exception
	{
		if (i > m_varNameList.size())
		{
			throw new XPath20Exception("Index out of bounds");
		}

		m_varNameList.add(i, varName);
		m_typeList.add(i, type);
		m_exprList.add(i, expr);
	}

	public void removeClause(int i) throws XPath20Exception
	{
		try
		{
			m_varNameList.remove(i);
			m_typeList.remove(i);
			m_exprList.remove(i);
		} catch (IndexOutOfBoundsException e)
		{
			throw new XPath20Exception(e);
		}

	}

	// Implements Expr
	
	public short getExprType()
	{
		return Expr.LET_EXPR;
	}

	public Expr cloneExpression()
	{
		try
		{
			return (Expr) clone();
		} catch (CloneNotSupportedException e)
		{
			// Never
			return null;
		}
	}

}
