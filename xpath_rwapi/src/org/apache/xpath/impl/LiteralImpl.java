/*
 * The Apache Software License, Version 1.1
 * 
 * Copyright (c) 2002-2003 The Apache Software Foundation. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *  3. The end-user documentation included with the redistribution, if any,
 * must include the following acknowledgment: "This product includes software
 * developed by the Apache Software Foundation (http://www.apache.org/)."
 * Alternately, this acknowledgment may appear in the software itself, if and
 * wherever such third-party acknowledgments normally appear.
 *  4. The names "Xalan" and "Apache Software Foundation" must not be used to
 * endorse or promote products derived from this software without prior written
 * permission. For written permission, please contact apache@apache.org.
 *  5. Products derived from this software may not be called "Apache", nor may
 * "Apache" appear in their name, without prior written permission of the
 * Apache Software Foundation.
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

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.xpath.XPath20Exception;
import org.apache.xpath.expression.Expr;
import org.apache.xpath.expression.Literal;
import org.apache.xpath.expression.Visitor;
import org.apache.xpath.impl.parser.Token;
import org.apache.xpath.impl.parser.XPathTreeConstants;

/**
 * JavaCC-based {@link Literal}implementation.
 * 
 * @author <a href="mailto:villard@us.ibm.com">Lionel Villard</a>
 * @version $Id$
 */
public class LiteralImpl extends ExprImpl implements Literal
{
	/**
	 * Literal object. Either BigInteger, BigDecimal, String or Double
	 */
	protected Object m_literal;

	// Constructors

	/**
	 * Constructor for LiteralImpl. Internal uses only.
	 */
	protected LiteralImpl()
	{
		super();
	}

	/**
	 * Constructor for LiteralImpl. Internal uses only.
	 * 
	 * @param i
	 */
	public LiteralImpl(int i)
	{
		super(i);
	}

	// Methods

	public void getString(StringBuffer expr, boolean abbreviate)
	{
		switch (getLiteralType())
		{
			case STRING_LITERAL :
				// TODO: choice between quote and double quote.
				expr.append('\'').append(m_literal.toString()).append('\'');
				break;
			default :
				// TODO: check whether the format is good or not

				expr.append(m_literal.toString());
		}
	}

	protected void setIntValue(BigInteger value)
	{
		m_literal = value;
		id = XPathTreeConstants.JJTINTEGERLITERAL;
	}

	protected void setDecimalValue(BigDecimal value)
	{
		m_literal = value;
		id = XPathTreeConstants.JJTDECIMALLITERAL;
	}

	protected void setStringValue(String value)
	{
		m_literal = value;
		id = XPathTreeConstants.JJTSTRINGLITERAL;
	}

	protected void setDoubleValue(double value)
	{
		m_literal = new Double(value);
		id = XPathTreeConstants.JJTDOUBLELITERAL;
	}

	// Implements Expr

	public short getExprType()
	{
		return Expr.LITERAL_EXPR;
	}

	public boolean visit(Visitor visitor)
	{
		return visitor.visitLiteral(this);
	}

	// Implements Literal

	public BigDecimal getDecimalLiteral() throws XPath20Exception
	{
		if (getLiteralType() == DECIMAL_LITERAL)
		{
			return (BigDecimal) m_literal;
		}

		throw new XPath20Exception("Invalid method call: the literal is not a decimal");
	}

	public double getDecimalLiteralAsDouble() throws XPath20Exception
	{
		return getDecimalLiteral().doubleValue();
	}

	public double getDoubleLiteral() throws XPath20Exception
	{
		if (getLiteralType() == DOUBLE_LITERAL)
		{
			return ((Double) m_literal).doubleValue();
		}

		throw new XPath20Exception("Invalid method call: the literal is not a double");
	}

	public BigInteger getIntegerLiteral() throws XPath20Exception
	{
		if (getLiteralType() == INTEGER_LITERAL)
		{
			return (BigInteger) m_literal;
		}

		throw new XPath20Exception("Invalid method call: the literal is not a integer");
		//I16
	}

	public int getIntegerLiteralAsInt() throws XPath20Exception
	{
		return getIntegerLiteral().intValue();
	}

	public short getLiteralType()
	{
		switch (id)
		{
			case XPathTreeConstants.JJTINTEGERLITERAL :
				return INTEGER_LITERAL;

				//break;
			case XPathTreeConstants.JJTDOUBLELITERAL :
				return DOUBLE_LITERAL;

				//break;
			case XPathTreeConstants.JJTSTRINGLITERAL :
				return STRING_LITERAL;

				//break;
			case XPathTreeConstants.JJTDECIMALLITERAL :
				return DECIMAL_LITERAL;

				//break;
			default :
				// Invalid state of this object
				throw new RuntimeException("Invalid JJTree id:" + id);
		}
	}

	public String getStringLiteral() throws XPath20Exception
	{
		if (getLiteralType() == STRING_LITERAL)
		{
			return (String) m_literal;
		}

		throw new XPath20Exception("Invalid method call: the literal is not a string");
	}

	public String getString(boolean abbreviate)
	{
		return m_literal.toString();
	}

	// Parser

	public void processToken(Token token)
	{
		super.processToken(token);

		switch (id)
		{
			case XPathTreeConstants.JJTINTEGERLITERAL :
				m_literal = new BigInteger(token.image);

				break;

			case XPathTreeConstants.JJTDOUBLELITERAL :
				m_literal = new Double(token.image);

				break;

			case XPathTreeConstants.JJTSTRINGLITERAL :
				// remove quote

				m_literal = token.image.substring(1, token.image.length() - 1);

				break;

			case XPathTreeConstants.JJTDECIMALLITERAL :
				m_literal = new BigDecimal(token.image);

				break;

			default :

				// bug
				throw new RuntimeException("Invalid JJTree id:" + id);
		}
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
