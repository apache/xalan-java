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

import org.apache.xml.QName;
import org.apache.xpath.expression.NameTest;
import org.apache.xpath.expression.Visitor;
import org.apache.xpath.impl.parser.Node;
import org.apache.xpath.impl.parser.IQNameWrapper;
import org.apache.xpath.impl.parser.XPathTreeConstants;

/**
 * JavaCC-based {@link NameTest} implementation 
 * @author <a href="mailto:villard@us.ibm.com">Lionel Villard</a>
 * @version $Id$
 */
public class NameTestImpl extends ExprImpl implements NameTest
{
	/**
	 * Name test
	 */
	QName m_qname;

	/**
	 * NCName
	 */
	String m_ncname;

	/**
	 * Nametest type
	 */
	short m_type;

	// Constructors

	/**
	 * Constructor for NameTestImpl. Internal uses only
	 *
	 * @param i
	 */
	public NameTestImpl(int i)
	{
		super(i);
	}

	/**
	 * Constructor for NodeTestImpl. Internal uses only
	 */
	public NameTestImpl(QName qname)
	{
		super(XPathTreeConstants.JJTNAMETEST);

		m_qname = qname;
		m_type = QNAME;
	}

	// Methods

	public void getString(StringBuffer expr, boolean abbreviate)
	{
		expr.append(getString(abbreviate));
	}

	// Implements NameTest

	public QName getName()
	{
		return m_qname;
	}

	public String getNCName()
	{
		return m_ncname;
	}

	public short getNameTestType()
	{
		return m_type;
	}

	// Implements NodeTest

	final public boolean isNameTest()
	{
		return true;
	}

	final public boolean isKindTest()
	{
		return false;
	}

	// Implements Expr

	public String getString(boolean abbreviate)
	{
		switch (m_type)
		{
			case WILDCARD :
				return "*";
			case QNAME :
				return m_qname.toString();
			case NCNAME_WILDCARD :
				return m_ncname + ":*";
			case WILDCARD_NCNAME :
				return "*:" + m_ncname;
			default :
				throw new IllegalStateException(
					"Invalid NameTest type " + m_type);
		}
	}

	public short getExprType()
	{
		return NODE_TEST_EXPR;
	}

	public boolean visit(Visitor visitor)
	{
		return visitor.visitNameTest(this);
	}

	// Parser

	public void jjtAddChild(Node n, int i)
	{
		IQNameWrapper w = (IQNameWrapper) n;

		m_type = w.m_type;
		m_ncname = w.m_ncname;
		m_qname = w.getQName();
	}

	// Debugging

	/**
	 * Override to print out useful instance data.
	 *
	 * @see org.apache.xpath.impl.parser.SimpleNode#toString()
	 */
	public String toString()
	{
		return XPathTreeConstants.jjtNodeName[id]
			+ " "
			+ getClass()
			+ " "
			+ getString(false);
	}

}
