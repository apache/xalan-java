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

import java.util.Hashtable;

import org.apache.xpath.expression.Expr;
import org.apache.xpath.expression.Visitor;
import org.apache.xpath.impl.parser.SimpleNode;
import org.apache.xpath.impl.parser.XPath;

/**
 * Base class for {@link Expr} implementation.
 */
public abstract class ExprImpl extends SimpleNode implements Expr
{

	// Field required for decorator support. An expression
	// can have a list of decorated properties, which are
	// set by the visitors.
	//
	// %REVISIT% The hashtable implementation might be inefficient.
	// Use a different internal implementation if necessary.
	private Hashtable m_properties = null;

	// Constructors

	/**
	 *
	 */
	protected ExprImpl()
	{
		super();
	}

	/**
	 * Constructor for ExprImpl.
	 *
	 * @param i
	 */
	public ExprImpl(int i)
	{
		super(i);
	}

	/**
	 * Constructor for ExprImpl.
	 *
	 * @param p
	 * @param i
	 */
	public ExprImpl(XPath p, int i)
	{
		super(i);
	}

	// Methods

	/**
	 * Gets expression as external string representation
	 * @param expr 
	 * @param abbreviate 
	 */
	public abstract void getString(StringBuffer expr, boolean abbreviate);

	/**
	 * Returns the specified expression or a clone of it if it's part of
	 * an AST (same of this one or another).
	 * @param expr
	 * @return
	 */
	protected Expr selfOrclone(Expr expr)
	{
		if (expr.getParentExpr() != null)
		{
			return expr.cloneExpression();
		}
		else
		{
			return expr;
		}
	}

	public int hashCode()
	{
		return getString(true).hashCode();
	}

	/**
	 * @return
	 */
	public boolean isRootOnSelfNode()
	{
		return false;
	}

	protected boolean withinExpr(int type)
	{
		Expr parent = getParentExpr();
		while (parent != null)
		{
			if (parent.getExprType() == type)
			{
				return true;
			}
			parent = parent.getParentExpr();
		}
		return false;
	}

	// Implements Expr

	/**
	 * Add a key/value pair to the expression
	 * @param key
	 * @param value
	 */
	public void addProperty(Object key, Object value)
	{
		if (m_properties == null)
			m_properties = new Hashtable();

		m_properties.put(key, value);
	}

	/**
	 * Retrieve the property from a given key
	 * @param key
	 * @return
	 */
	public Object getProperty(Object key)
	{
		return m_properties != null ? m_properties.get(key) : null;
	}

	/**
	 * Return the property associated with the given key.
	 * @param key
	 * @return
	 */
	public Object removeProperty(Object key)
	{
		return m_properties.remove(key);
	}

	/**
	 * Return all properties.
	 */
	public void removeAllProperties()
	{
		m_properties.clear();
	}

	public Expr cloneExpression()
	{
		return (Expr) super.clone();
	}

	public String getString(boolean abbreviate)
	{
		StringBuffer buf = new StringBuffer();
		getString(buf, abbreviate);
		return buf.toString();
	}

	public Expr getParentExpr()
	{
		return (Expr) m_parent;
	}

	public boolean visit(Visitor visitor)
	{
		return true;
	}

	public void normalize()
	{
		// @TODO
	}

	public boolean equals(Object obj)
	{
		return obj instanceof ExprImpl
			&& ((ExprImpl) obj).getString(true).equals(getString(true));
	}

}
