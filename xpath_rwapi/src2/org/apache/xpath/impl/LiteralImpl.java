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

import org.apache.xpath.XPathException;
import org.apache.xpath.expression.Expr;
import org.apache.xpath.expression.Literal;
import org.apache.xpath.expression.Visitor;
import org.apache.xpath.impl.parser.Token;
import org.apache.xpath.impl.parser.XPath;
import org.apache.xpath.impl.parser.XPathTreeConstants;

import java.math.BigDecimal;
import java.math.BigInteger;


/**
 * Default implementation of literal XPath expression type.
 */
public class LiteralImpl extends ExprImpl implements Literal
{
    /**
     * Literal object. Either BigInteger, BigDecimal, String or Double
     */
    protected Object m_literal;

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

    /**
     * Constructor for LiteralImpl. Internal uses only.
     *
     * @param p
     * @param i
     */
    public LiteralImpl(XPath p, int i)
    {
        super(p, i);
    }

    /**
     * Constructor for cloning.
     *
     * @param expr DOCUMENT ME!
     */
    public LiteralImpl(LiteralImpl expr)
    {
        super(expr.id);

        m_literal = expr.m_literal;

        // no cloning since m_literal reference is immutable
    }

    /**
     * @see org.apache.xpath.expression.Expr#getExprType()
     */
    public short getExprType()
    {
        return Expr.LITERAL_EXPR;
    }

    /**
     * @see org.apache.xpath.expression.Expr#cloneExpression()
     */
    public Expr cloneExpression()
    {
        return new LiteralImpl(this);
    }

    /**
     * @see org.apache.xpath.expression.Literal#getDecimalLiteral()
     */
    public BigDecimal getDecimalLiteral() throws XPathException
    {
        if (getLiteralType() == DECIMAL_LITERAL)
        {
            return (BigDecimal) m_literal;
        }

        throw new XPathException(
            "Invalid method call: the literal is not a decimal");
    }

    /* (non-Javadoc)
     * @see org.apache.xpath.expression.Literal#getDecimalLiteralAsDouble()
     */
    public double getDecimalLiteralAsDouble() throws XPathException
    {
        return getDecimalLiteral().doubleValue();
    }

    /**
     * @see org.apache.xpath.expression.Literal#getDoubleLiteral()
     */
    public double getDoubleLiteral() throws XPathException
    {
        if (getLiteralType() == DOUBLE_LITERAL)
        {
            return ((Double) m_literal).doubleValue();
        }

        throw new XPathException(
            "Invalid method call: the literal is not a double");
    }

    /**
     * @see org.apache.xpath.expression.Literal#getIntegerLiteral()
     */
    public BigInteger getIntegerLiteral() throws XPathException
    {
        if (getLiteralType() == INTEGER_LITERAL)
        {
            return (BigInteger) m_literal;
        }

        throw new XPathException(
            "Invalid method call: the literal is not a integer"); //I16
    }

    /* (non-Javadoc)
     * @see org.apache.xpath.expression.Literal#getIntLiteral()
     */
    public int getIntegerLiteralAsInt() throws XPathException
    {
        return getIntegerLiteral().intValue();
    }

    /**
     * @see org.apache.xpath.expression.Literal#getLiteralType()
     */
    public short getLiteralType()
    {
        switch (id)
        {
            case XPathTreeConstants.JJTINTEGERLITERAL:
                return INTEGER_LITERAL;

            //break;
            case XPathTreeConstants.JJTDOUBLELITERAL:
                return DOUBLE_LITERAL;

            //break;
            case XPathTreeConstants.JJTSTRINGLITERAL:
                return STRING_LITERAL;

            //break;
            case XPathTreeConstants.JJTDECIMALLITERAL:
                return DECIMAL_LITERAL;

            //break;
            default:

                // Invalid state of this object
                throw new RuntimeException("Invalid JJTree id:" + id);
        }
    }

    /**
     * @see org.apache.xpath.expression.Literal#getStringLiteral()
     */
    public String getStringLiteral() throws XPathException
    {
        if (getLiteralType() == STRING_LITERAL)
        {
            return (String) m_literal;
        }

        throw new XPathException(
            "Invalid method call: the literal is not a string");
    }

    /**
     * @see org.apache.xpath.expression.Expr#getString(boolean)
     */
    public String getString(boolean abbreviate)
    {
        return m_literal.toString();
    }

    /**
     * @see org.apache.xpath.expression.Visitable#visited(Visitor)
     */
    public void visited(Visitor visitor)
    {
        visitor.visitLiteral(this);
    }

    /**
     * @see org.apache.xpath.impl.parser.SimpleNode#processToken(Token)
     */
    public void processToken(Token token)
    {
        super.processToken(token);

        switch (id)
        {
            case XPathTreeConstants.JJTINTEGERLITERAL:
                m_literal = new BigInteger(token.image);

                break;

            case XPathTreeConstants.JJTDOUBLELITERAL:
                m_literal = new Double(token.image);

                break;

            case XPathTreeConstants.JJTSTRINGLITERAL:
                m_literal = token.image;

                break;

            case XPathTreeConstants.JJTDECIMALLITERAL:
                m_literal = new BigDecimal(token.image);

                break;

            default:

                // bug
                throw new RuntimeException("Invalid JJTree id:" + id);
        }
    }

    /**
     * @see org.apache.xpath.impl.ExprImpl#getString(StringBuffer, boolean)
     */
    public void getString(StringBuffer expr, boolean abbreviate)
    {
        expr.append(m_literal.toString());
    }

    /**
     * DOCUMENT ME!
     *
     * @param value
     */
    protected void setIntValue(BigInteger value)
    {
        m_literal = value;
        id = XPathTreeConstants.JJTINTEGERLITERAL;
    }

    /**
     * DOCUMENT ME!
     *
     * @param value
     */
    protected void setDecimalValue(BigDecimal value)
    {
        m_literal = value;
        id = XPathTreeConstants.JJTDECIMALLITERAL;
    }

    /**
     * DOCUMENT ME!
     *
     * @param value
     */
    protected void setStringValue(String value)
    {
        m_literal = value;
        id = XPathTreeConstants.JJTSTRINGLITERAL;
    }

    /**
     * DOCUMENT ME!
     *
     * @param value
     */
    protected void setDoubleValue(double value)
    {
        m_literal = new Double(value);
        id = XPathTreeConstants.JJTDOUBLELITERAL;
    }

    /**
     * Override to print out useful instance data.
     *
     * @see org.apache.xpath.impl.parser.SimpleNode#toString()
     */
    public String toString()
    {
        return XPathTreeConstants.jjtNodeName[id] + " " + getClass() + " "
        + getString(false);
    }
}
