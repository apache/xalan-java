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

import org.apache.xpath.rwapi.XPathException;
import org.apache.xpath.rwapi.expression.Expr;
import org.apache.xpath.rwapi.expression.NodeTest;
import org.apache.xpath.rwapi.expression.Visitor;
import org.apache.xpath.rwapi.impl.parser.Node;
import org.apache.xpath.rwapi.impl.parser.XPath;
import org.apache.xpath.rwapi.impl.parser.XPathTreeConstants;


/**
 *
 */
public class KindTestImpl extends ExprImpl implements NodeTest
{
    protected short m_kindTest;

    /**
    * Creates an any kind test node 
    */
    public KindTestImpl()
    {
        super(XPathTreeConstants.JJTANYKINDTEST);
    }

    /**
     * Constructor for KindTestImpl.
     * @param i
     */
    public KindTestImpl(int i)
    {
        super(i);

        setKindTestFromJJTID(i);
    }

    /**
     * Constructor for KindTestImpl.
     * @param p
     * @param i
     */
    public KindTestImpl(XPath p, int i)
    {
        super(p, i);

        setKindTestFromJJTID(i);
    }

    /**
     * @see org.apache.xpath.rwapi.expression.NodeTest#isNameTest()
     */
    public boolean isNameTest()
    {
        return false;
    }

    /**
     * @see org.apache.xpath.rwapi.expression.NodeTest#isKindTest()
     */
    public boolean isKindTest()
    {
        return true;
    }

    /**
     * @see org.apache.xpath.rwapi.expression.NodeTest#getKindTest()
     */
    public short getKindTest() throws XPathException
    {
        return m_kindTest;
    }

    /**
     * @see org.apache.xpath.rwapi.expression.NodeTest#getLocalNameTest()
     */
    public String getLocalNameTest() throws XPathException
    {
        throw new XPathException("Invalid call this method on kind test"); // I8
    }

    /**
     * @see org.apache.xpath.rwapi.expression.NodeTest#getPrefix()
     */
    public String getPrefix() throws XPathException
    {
        throw new XPathException("Invalid call this method on kind test"); // I8
    }

    /**
     * @see org.apache.xpath.rwapi.expression.Expr#getExprType()
     */
    public short getExprType()
    {
        return Expr.KINDTEST_EXPR;
    }

    /**
     * @see org.apache.xpath.rwapi.expression.Expr#cloneExpression()
     */
    public Expr cloneExpression()
    {
        return null; //TODO
    }

    /**
     * @see org.apache.xpath.rwapi.expression.Expr#getString(boolean)
     */
    public String getString(boolean abbreviate)
    {
        return KIND_TEST_NAME[m_kindTest];
    }

    /**
     * @see org.apache.xpath.rwapi.expression.Visitable#visited(Visitor)
     */
    public void visited(Visitor visitor)
    {
    }

    /**
     * @see org.apache.xpath.rwapi.impl.parser.Node#jjtAddChild(Node, int)
     */
    public void jjtAddChild(Node n, int i)
    {
        // filter child
        // m_kindTest = ((KindTest) n).getKindTest();
    }

    /**
     * Set the kindtest type from JJT id
     * @param id
     */
    private void setKindTestFromJJTID(int id)
    {
        switch (id)
        {
            case XPathTreeConstants.JJTPROCESSINGINSTRUCTIONTEST:
                m_kindTest = NodeTest.PROCESSING_INSTRUCTION_TEST;

                break;

            case XPathTreeConstants.JJTCOMMENTTEST:
                m_kindTest = NodeTest.COMMENT_TEST;

                break;

            case XPathTreeConstants.JJTTEXTTEST:
                m_kindTest = NodeTest.TEXT_TEST;

                break;

            case XPathTreeConstants.JJTANYKINDTEST:
                m_kindTest = NodeTest.ANY_KIND_TEST;

                break;
        }
    }

    /**
     * @see org.apache.xpath.rwapi.impl.ExprImpl#getString(StringBuffer, boolean)
     */
    protected void getString(StringBuffer expr, boolean abbreviate)
    {
        expr.append(KIND_TEST_NAME[m_kindTest]);
    }

    /**
     * Sets the kindTest.
     * @param kindTest The kindTest to set
     */
    public void setKindTest(short kindTest)
    {
        m_kindTest = kindTest;
    }

    /**
     * Override to print out useful instance data.  
     * @see org.apache.xpath.rwapi.impl.parser.SimpleNode#toString()
     */
    public String toString()
    {
        return XPathTreeConstants.jjtNodeName[id] + " " 
                + getClass() + " " 
                + getString(false);
    }
}
